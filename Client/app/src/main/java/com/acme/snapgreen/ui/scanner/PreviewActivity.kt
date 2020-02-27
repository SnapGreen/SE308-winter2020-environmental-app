package com.acme.snapgreen.ui.scanner


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.util.size
import com.acme.snapgreen.R
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.util.*


class PreviewActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private val cameraFacing = CameraCharacteristics.LENS_FACING_BACK
    private lateinit var surfaceTextureListener: SurfaceTextureListener
    private lateinit var cameraId: String
    private var cameraCaptureSession: CameraCaptureSession? = null
    private lateinit var captureRequest: CaptureRequest
    private lateinit var previewSize: Size
    private lateinit var stateCallback: CameraDevice.StateCallback

    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private lateinit var textureView: TextureView
    private var cameraDevice: CameraDevice? = null
    private lateinit var captureCallBack: CameraCaptureSession.CaptureCallback
    private lateinit var detector : BarcodeDetector

    companion object {
        private const val CAMERA_REQUEST_CODE = 10001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        textureView = findViewById(R.id.texture_view)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )

        detector = BarcodeDetector.Builder(getApplicationContext())
            .setBarcodeFormats(Barcode.UPC_A)
            .build()
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        captureCallBack = object : CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
                super.onCaptureCompleted(session, request, result)

            }
        }

        surfaceTextureListener = object : SurfaceTextureListener {
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

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture)
            {
                scanBarCode(textureView.bitmap)
            }
        }

        stateCallback = object : CameraDevice.StateCallback() {
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

    }

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

    private fun openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler)
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
                )
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun openBackgroundThread() {
        val backgroundThread = HandlerThread("camera_background_thread")
        backgroundThread.start()
        this.backgroundThread = backgroundThread
        this.backgroundHandler = Handler(backgroundThread.getLooper())
    }


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


    override fun onStop() {
        super.onStop()
        closeCamera()
        closeBackgroundThread()
    }

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

    private fun closeBackgroundThread() {
        if (backgroundHandler != null) {
            backgroundThread?.quitSafely()
            backgroundThread = null
            backgroundHandler = null
        }
    }

    private fun createPreviewSession() {
        try {
            val surfaceTexture = textureView.surfaceTexture
            surfaceTexture.setDefaultBufferSize(previewSize.width, previewSize.height)

            val previewSurface = Surface(surfaceTexture)

            // new output surface for preview frame data
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
                            captureRequest = captureRequestBuilder.build()
                            cameraCaptureSession.setRepeatingRequest(
                                captureRequest,
                                captureCallBack, backgroundHandler
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
     * Break out of this activity as soon as a barcode is scanned
     * TODO: This definitely needs to be asynchronous and shouldnt happen from the activity class
     */
    fun scanBarCode(bitmap: Bitmap)
    {
        if(!detector.isOperational())
        {
            assert(false)
        }

        val frame = Frame.Builder().setBitmap(bitmap).build()
        val barcodes = detector.detect(frame)

        if(barcodes.size > 0)
        {
            val thisCode = barcodes.valueAt(0)
            finish()
        }
    }
}
