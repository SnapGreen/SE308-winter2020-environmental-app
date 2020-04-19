package com.acme.snapgreen.ui.scanner


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView.SurfaceTextureListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.util.size
import com.acme.snapgreen.R
import com.acme.snapgreen.ui.dashboard.EXTRA_MESSAGE
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class PreviewActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    /**
     * A representation of a single camera connected to an
     * Android device
     */
    private var cameraDevice: CameraDevice? = null

    /**
     * Threads / handler to launch the camera on
     * (we don't want to block the UI thread
     */
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null

    /**
     * Used for capturing images from the
     * camera
     */
    private var cameraCaptureSession: CameraCaptureSession? = null

    /**
     * The UI view to display the preview
     */
    private lateinit var textureView: AutoFitTextureView

    /**
     * The resolution of the displayed camera preview
     */
    private lateinit var previewSize: Size

    /**
     * Id of the camera being used by the app
     */
    private lateinit var cameraId: String

    /**
     * System service to open a camera and get relevant information
     */
    private lateinit var cameraManager: CameraManager

    /**
     * Handles scanning the image provided by the preview
     */
    private val detector: BarcodeDetector by lazy {
        BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.UPC_A)
            .build()
    }

    /**
     * Orientation of the camera sensor
     */
    private var sensorOrientation = 0


    /**
     * This is a listener: an abstract class implementation that can be passed into other objects
     * which will call these functions when a certain event happens. These are incredibly common
     * in android dev as events rarely happen in a linear / set fashion.
     *
     * A listener for the (UI) surface. Opens the camera once it is loaded and sends each frame
     * to the barcode scanner
     */
    private val surfaceTextureListener = object : SurfaceTextureListener {
        // called when the preview is first initialized
        override fun onSurfaceTextureAvailable(
            surfaceTexture: SurfaceTexture,
            width: Int,
            height: Int
        ) {
            setUpCamera(width, height)
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(
            surfaceTexture: SurfaceTexture,
            width: Int,
            height: Int
        ) {
        }

        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {

            scanBarcode(textureView.bitmap)

        }
    }

    /**
     * A listener for the physical camera state. Opens the preview when the camera becomes available.
     */
    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            this@PreviewActivity.cameraDevice = cameraDevice
            createPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraDevice.close()
            this@PreviewActivity.cameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            cameraDevice.close()
            this@PreviewActivity.cameraDevice = null
        }
    }

    /**
     * Handle camera permissions before accessing camera through OS
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        mJob = Job()
        textureView = findViewById(R.id.texture_view)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        textureView.surfaceTextureListener = surfaceTextureListener


    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }

    /**
     * Finds the highest resolution rear facing camera on the users device and saves them as
     * member data. Sets the resolution of the camera preview accordingly.
     */
    private fun setUpCamera(width: Int, height: Int) {
        try {
            for (cameraId in cameraManager.cameraIdList) {
                val cameraCharacteristics: CameraCharacteristics =
                    cameraManager.getCameraCharacteristics(cameraId)
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                    cameraFacing
                ) {
                    val map =
                        cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
                        ) ?: continue
                    sensorOrientation =
                        cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)

                    // For still image captures, we use the largest available size.
                    val largest = Collections.max(
                        listOf(*map.getOutputSizes(ImageFormat.JPEG)),
                        CompareSizesByArea()
                    )

                    // Find out if we need to swap dimension to get the preview size relative to sensor
                    // coordinate.
                    val displayRotation = this.windowManager.defaultDisplay.rotation

                    sensorOrientation =
                        cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
                    val swappedDimensions = areDimensionsSwapped(displayRotation)

                    val displaySize = Point()
                    this.windowManager.defaultDisplay.getSize(displaySize)
                    val rotatedPreviewWidth =
                        if (swappedDimensions) height else width
                    val rotatedPreviewHeight =
                        if (swappedDimensions) width else height
                    var maxPreviewWidth = if (swappedDimensions) displaySize.y else displaySize.x
                    var maxPreviewHeight = if (swappedDimensions) displaySize.x else displaySize.y


                    if (maxPreviewWidth > MAX_PREVIEW_WIDTH) maxPreviewWidth = MAX_PREVIEW_WIDTH
                    if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) maxPreviewHeight = MAX_PREVIEW_HEIGHT


                    previewSize = chooseOptimalSize(
                        map.getOutputSizes(SurfaceTexture::class.java),
                        rotatedPreviewWidth, rotatedPreviewHeight,
                        maxPreviewWidth, maxPreviewHeight,
                        largest
                    )

                    textureView.setAspectRatio(previewSize.height, previewSize.width)
                    textureView.matrix.setScale(1F, (displaySize.y / displaySize.x).toFloat())
                    this.cameraId = cameraId
                    return
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun areDimensionsSwapped(displayRotation: Int): Boolean {
        var swappedDimensions = false
        when (displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                if (sensorOrientation == 90 || sensorOrientation == 270) {
                    swappedDimensions = true
                }
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                if (sensorOrientation == 0 || sensorOrientation == 180) {
                    swappedDimensions = true
                }
            }
            else -> {
                Log.e(TAG, "Display rotation is invalid: $displayRotation")
            }
        }
        return swappedDimensions
    }


    /**
     * Checks permissions before opening the devices's rear facing camera.
     */
    private fun openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * Start a new thread for the camera to run on. The camera logic needs to be asynchronous.
     */
    private fun openBackgroundThread() {
        val backgroundThread = HandlerThread("camera_background_thread")
        backgroundThread.start()
        this.backgroundThread = backgroundThread
        this.backgroundHandler = Handler(backgroundThread.looper)
    }

    /**
     * Handles the user reopening the app without fully closing it
     */
    override fun onResume() {
        super.onResume()
        openBackgroundThread()
        if (textureView.isAvailable) {
            setUpCamera(textureView.width, textureView.height)
            openCamera()
        } else {
            textureView.surfaceTextureListener = surfaceTextureListener
        }
    }

    /**
     * Handles the user tabbing out of the app without fully closing it
     */
    override fun onStop() {
        super.onStop()
        closeCamera()
        closeBackgroundThread()
    }

    /**
     * Closes the camera and prevents memory leaks associated with keeping the camera data pipeline
     * open.
     */
    private fun closeCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession!!.close()

            cameraCaptureSession = null
        }
        if (cameraDevice != null) {
            cameraDevice!!.close()
            cameraDevice = null
        }
    }

    /**
     * Closes background thread and nulls member data to prevent memory leaks
     */
    private fun closeBackgroundThread() {
        if (backgroundHandler != null) {
            backgroundThread?.quitSafely()
            backgroundThread = null
            backgroundHandler = null
        }
    }

    /**
     * Tells the camera to begin recording and sets the texture view from the UI as a target
     * for captured images.
     */
    private fun createPreviewSession() {
        try {
            val surfaceTexture = textureView.surfaceTexture
            surfaceTexture.setDefaultBufferSize(previewSize.width, previewSize.height)

            val previewSurface = Surface(surfaceTexture)

            val captureRequestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(previewSurface)

            cameraDevice!!.createCaptureSession(
                Collections.singletonList(previewSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        if (cameraDevice == null) {
                            return
                        }
                        try {
                            val captureRequest = captureRequestBuilder.build()
                            cameraCaptureSession.setRepeatingRequest(
                                captureRequest,
                                null, backgroundHandler
                            )
                            this@PreviewActivity.cameraCaptureSession = cameraCaptureSession

                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {}
                }, backgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * Launches another activity with the result of the successful barcode scan.
     * @param barcode: The barcode scanned by the camera
     */
    private fun scanBarcode(bitmap: Bitmap) {
        val intent = Intent(this, ScanResultActivity::class.java)

        launch {
            //Working on UI thread

            async(Dispatchers.Default) {
                //Working on background thread
                val frame = Frame.Builder().setBitmap(bitmap).build()
                val result = detector.detect(frame)

                // exit to result activity if scan is successful
                if (result.size > 0) {
                    intent.putExtra(EXTRA_MESSAGE, result.valueAt(0).displayValue)
                    startActivity(intent)
                    closeCamera()
                    closeBackgroundThread()
                    finish()
                }
            }

        }

    }

    companion object {

        /**
         * Conversion from screen rotation to JPEG orientation.
         */
        private val ORIENTATIONS = SparseIntArray()
        private const val cameraFacing = CameraCharacteristics.LENS_FACING_BACK
        private const val CAMERA_REQUEST_CODE = 10001

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }

        /**
         * Tag for the [Log].
         */
        private const val TAG = "PreviewActivity"

        /**
         * Max preview width that is guaranteed by Camera2 API
         */
        private const val MAX_PREVIEW_WIDTH = 1920

        /**
         * Max preview height that is guaranteed by Camera2 API
         */
        private const val MAX_PREVIEW_HEIGHT = 1080

        /**
         * Given `choices` of `Size`s supported by a camera, choose the smallest one that
         * is at least as large as the respective texture view size, and that is at most as large as
         * the respective max size, and whose aspect ratio matches with the specified value. If such
         * size doesn't exist, choose the largest one that is at most as large as the respective max
         * size, and whose aspect ratio matches with the specified value.
         *
         * @param choices           The list of sizes that the camera supports for the intended
         *                          output class
         * @param textureViewWidth  The width of the texture view relative to sensor coordinate
         * @param textureViewHeight The height of the texture view relative to sensor coordinate
         * @param maxWidth          The maximum width that can be chosen
         * @param maxHeight         The maximum height that can be chosen
         * @param aspectRatio       The aspect ratio
         * @return The optimal `Size`, or an arbitrary one if none were big enough
         */
        @JvmStatic
        private fun chooseOptimalSize(
            choices: Array<Size>,
            textureViewWidth: Int,
            textureViewHeight: Int,
            maxWidth: Int,
            maxHeight: Int,
            aspectRatio: Size
        ): Size {

            // Collect the supported resolutions that are at least as big as the preview Surface
            val bigEnough = ArrayList<Size>()
            // Collect the supported resolutions that are smaller than the preview Surface
            val notBigEnough = ArrayList<Size>()
            val w = aspectRatio.width
            val h = aspectRatio.height
            for (option in choices) {
                if (option.width <= maxWidth && option.height <= maxHeight &&
                    option.height == option.width * h / w
                ) {
                    if (option.width >= textureViewWidth && option.height >= textureViewHeight) {
                        bigEnough.add(option)
                    } else {
                        notBigEnough.add(option)
                    }
                }
            }

            // Pick the smallest of those big enough. If there is no one big enough, pick the
            // largest of those not big enough.
            return when {
                bigEnough.size > 0 -> {
                    Collections.min(bigEnough, CompareSizesByArea())
                }
                notBigEnough.size > 0 -> {
                    Collections.max(notBigEnough, CompareSizesByArea())
                }
                else -> {
                    Log.e(TAG, "Couldn't find any suitable preview size")
                    choices[0]
                }
            }
        }

    }
}
