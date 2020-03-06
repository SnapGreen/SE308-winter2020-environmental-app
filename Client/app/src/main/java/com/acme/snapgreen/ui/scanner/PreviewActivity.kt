package com.acme.snapgreen.ui.scanner


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.util.size
import com.acme.snapgreen.R
import com.acme.snapgreen.data.NetworkManager
import com.acme.snapgreen.data.Result
import com.acme.snapgreen.ui.dashboard.EXTRA_MESSAGE
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.io.IOException
import java.util.*

class PreviewActivity : AppCompatActivity() {

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
    private lateinit var textureView: TextureView

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
    private val detector: BarcodeDetector by lazy {BarcodeDetector.Builder(applicationContext)
        .setBarcodeFormats(Barcode.UPC_A)
        .build() }

    /**
     * These are equivalent to static variables in java
     */
    companion object {
        private const val cameraFacing = CameraCharacteristics.LENS_FACING_BACK
        private const val CAMERA_REQUEST_CODE = 10001
    }

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
            setUpCamera()
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
            //TODO: possible performance implications of scanning every frame
            scanBarCode(textureView.bitmap)
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
        textureView = findViewById(R.id.texture_view)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        textureView.surfaceTextureListener = surfaceTextureListener


    }

    /**
     * Finds the highest resolution rear facing camera on the users device and saves them as
     * member data. Sets the resolution of the camera preview accordingly.
     */
    private fun setUpCamera() {
        try {
            for (cameraId in cameraManager.getCameraIdList()) {
                val cameraCharacteristics: CameraCharacteristics =
                    cameraManager.getCameraCharacteristics(cameraId)
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                    cameraFacing
                ) {
                    val streamConfigurationMap =
                        cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
                        )
                    previewSize =
                        streamConfigurationMap.getOutputSizes(SurfaceTexture::class.java)[0]
                    this.cameraId = cameraId
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

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
        if (textureView.isAvailable()) {
            setUpCamera()
            openCamera()
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener)
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
     * Attempt to scan barcode and transition if successful
     * TODO: Should separate scanning / network functionality to an async task to increase perf
     */
    @Synchronized
    fun scanBarCode(bitmap: Bitmap) {

        assert(detector.isOperational)
        val frame = Frame.Builder().setBitmap(bitmap).build()
        val barcodes = detector.detect(frame)

        if (barcodes.size > 0) {
            onBarcodeScanSuccess(barcodes.valueAt(0))
        }
    }

    /**
     * Launches another activity with the result of the successful barcode scan.
     * @param barcode: The barcode scanned by the camera
     */
    @Synchronized
    private fun onBarcodeScanSuccess(barcode: Barcode) {

        val intent = Intent(this, ScanResultActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, barcode.displayValue)
        }
        startActivity(intent)
        closeCamera()
        closeBackgroundThread()
        finish()
    }
}
