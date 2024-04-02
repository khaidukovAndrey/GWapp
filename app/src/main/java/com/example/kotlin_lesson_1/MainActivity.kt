package com.example.kotlin_lesson_1

import android.content.ContentValues
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kotlin_lesson_1.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private val multiplePermissionId = 14
    private val multiplePermissionNameList = if (Build.VERSION.SDK_INT >= 33) {
        arrayListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )
    } else {
        arrayListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var camera: Camera
    private lateinit var cameraSelector: CameraSelector
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    //private var aspectRatio = AspectRatio.RATIO_16_9


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
            binding = ActivityMainBinding.inflate(layoutInflater)
        }catch (e:Exception){
            e.printStackTrace()
        }
        try{
            setContentView(binding.root)
        }catch (e:Exception){
            e.printStackTrace()
        }
        startCamera()
//        if (checkMultiplePermission()) {
//            startCamera()
//        }
        binding.flipCameraIB.setOnClickListener{
            lensFacing = if(lensFacing == CameraSelector.LENS_FACING_FRONT){
                CameraSelector.LENS_FACING_BACK
            }else{
                CameraSelector.LENS_FACING_FRONT
            }
            bindCameraUserCases()
        }
        binding.captureIB.setOnClickListener{
            takePhoto()
        }
        binding.flashToggleIB.setOnClickListener{
            setFlashIcon(camera)
        }
    }

    private fun setUpZoom(){
        val listener = object :ScaleGestureDetector.SimpleOnScaleGestureListener(){
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio ?: 1f
                val delta = detector.scaleFactor
                camera.cameraControl.setZoomRatio(currentZoomRatio * delta)
                return true
            }
        }
        val scaleGestureDetector = ScaleGestureDetector(this, listener)

        binding.previewView.setOnTouchListener{view, event ->
            scaleGestureDetector.onTouchEvent(event)
            if(event.action == MotionEvent.ACTION_DOWN){
                val factory = binding.previewView.meteringPointFactory
                val point = factory.createPoint(event.x, event.y)
                val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                    .setAutoCancelDuration(500,TimeUnit.MILLISECONDS)
                    .build()

                camera.cameraControl.startFocusAndMetering(action)
                view.performClick()
            }
            true
        }
    }

    private fun checkMultiplePermission(): Boolean {
        val listPermissionNeeded = arrayListOf<String>()
        for (permission in multiplePermissionNameList) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionNeeded.add(permission)
            }
        }
        if (listPermissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionNeeded.toTypedArray(),
                multiplePermissionId
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == multiplePermissionId) {
            if (grantResults.isNotEmpty()) {
                var isGrant = true
                for (element in grantResults) {
                    if (element == PackageManager.PERMISSION_DENIED) {
                        isGrant = false
                    }
                }
                if (isGrant) {
                    // here all permission granted successfully
                    startCamera()
                } else {
                    var someDenied = false
                    for (permission in permissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                permission
                            )
                        ) {
                            if (ActivityCompat.checkSelfPermission(
                                    this,
                                    permission
                                ) == PackageManager.PERMISSION_DENIED
                            ) {
                                someDenied = true
                            }
                        }
                    }
                    if (someDenied) {
                        // here app Setting open because all permission is not granted
                        // and permanent denied
                        appSettingOpen(this)
                    } else {
                        // here warning permission show
                        warningPermissionDialog(this) { _: DialogInterface, which: Int ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE ->
                                    checkMultiplePermission()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUserCases()
        },ContextCompat.getMainExecutor((this)))
    }

    private fun aspectRatio(width:Int, height: Int): Int{
        val previewRatio = maxOf(width, height).toDouble()/minOf(width,height)
        return if (abs(previewRatio-4.0/3.0) <= abs(previewRatio - 16.0/9.0)){
            AspectRatio.RATIO_4_3
        }else{
            AspectRatio.RATIO_16_9
        }
    }

    private fun bindCameraUserCases(){
        val screenAspectRatio = aspectRatio(
            binding.previewView.width,
            binding.previewView.height
        )
        val rotation = binding.previewView.display.rotation
        val resolutionSelector = ResolutionSelector.Builder()
            .setAspectRatioStrategy(
                AspectRatioStrategy(
                    screenAspectRatio,
                    AspectRatioStrategy.FALLBACK_RULE_AUTO
                )
            )
            .build()
        val preview = Preview.Builder()
            .setResolutionSelector(resolutionSelector)
            .setTargetRotation(rotation)
            .build()
            .also{
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setResolutionSelector(resolutionSelector)
            .setTargetRotation(rotation)
            .build()

        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        try{
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture
            )
            setUpZoom()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun setFlashIcon(camera: Camera) {
        if(camera.cameraInfo.hasFlashUnit()){
            if(camera.cameraInfo.torchState.value == 0){
                camera.cameraControl.enableTorch(true)
                binding.flashToggleIB.setImageResource(R.drawable.flash_off)
            }else{
                camera.cameraControl.enableTorch(false)
                binding.flashToggleIB.setImageResource(R.drawable.flash_on)
            }
        }else{
            Toast.makeText(
                this,
                "Flash is Not Available",
                Toast.LENGTH_LONG
            ).show()
            binding.flashToggleIB.isEnabled = false
        }
    }

    private fun takePhoto() {
        val imageFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Images")

        if(!imageFolder.exists()){
            imageFolder.mkdir()
        }

        val fileName = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(System.currentTimeMillis()) + ".jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME,fileName)
            put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/Images")
            }
        }

        //val imageFile = File(imageFolder, fileName)
        val metadata = ImageCapture.Metadata().apply {
            isReversedHorizontal = (lensFacing == CameraSelector.LENS_FACING_FRONT)
        }
        val outputOption =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                OutputFileOptions.Builder(
                    contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ).setMetadata(metadata).build()
            }else{
                val imageFile = File(imageFolder, fileName)
                OutputFileOptions.Builder(imageFile)
                    .setMetadata(metadata).build()
            }

        imageCapture.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this),
            object:ImageCapture.OnImageCapturedCallback(), ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val message = "Photo Capture Succeeded: ${outputFileResults.savedUri}"
                    Toast.makeText(
                        this@MainActivity,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@MainActivity,
                        exception.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        )
    }

}
