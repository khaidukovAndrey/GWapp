package com.example.kotlin_lesson_1

import android.Manifest
import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.kotlin_lesson_1.databinding.ActivityImageCropperBinding
import java.io.File
import java.io.OutputStream
import java.util.Date

class ImageCropperActivity : AppCompatActivity() {
    private lateinit var binding : ActivityImageCropperBinding
    private lateinit var saved_uri : Uri
    private lateinit var uriPickedPhoto : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var cropFlag = false
        binding = ActivityImageCropperBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.selectImage.setOnClickListener {
            if (isPermitted()) {
                cropFlag = true
                launchImageCropper(uriPickedPhoto.toUri())
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requestAndroid11StoragePermission()
                } else {
                    try {
                        requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }catch (e:Exception)
                    {
                        e.printStackTrace()
                    }
                }
            }
        }
        binding.button.setOnClickListener {
            val i = Intent(this, ImageClassificationActivity::class.java)
            if(cropFlag)
            {
                Log.d("MyLog", saved_uri.toString())
                i.putExtra("uri_ml", saved_uri.toString())
            }
            else{
                i.putExtra("uri_ml", uriPickedPhoto)
            }
            try {
                startActivity(i)
            }catch (e:Exception)
            {
                e.printStackTrace()
            }

        }
    }

    override fun onStart() {
        super.onStart()
        try{
            uriPickedPhoto = intent.getStringExtra("uri").toString()
            binding.imageView2.setImageURI(uriPickedPhoto.toUri())
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission())
    { isGranted: Boolean ->
        if (isGranted) {
            val uriPickedPhoto = intent.getStringExtra("uri")
            uriPickedPhoto?.let { it1 -> launchImageCropper(it1.toUri()) }
        } else {
            permissionDenied()
        }
    }

    private var android11StoragePermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { _: ActivityResult? ->
        if (isPermitted()) {
            val uriPickedPhoto = intent.getStringExtra("uri")
            uriPickedPhoto?.let { it1 -> launchImageCropper(it1.toUri()) }
        } else {
            permissionDenied()
        }
    }

    @TargetApi(Build.VERSION_CODES.R)
    private fun requestAndroid11StoragePermission() {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.addCategory("android.intent.category.DEFAULT")
        intent.setData(Uri.parse(String.format("package:%s", applicationContext.packageName)))
        android11StoragePermission.launch(intent)
    }

    private fun isPermitted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun permissionDenied() {
        Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_LONG).show()
    }

    private var cropImage = registerForActivityResult(CropImageContract()) { result: CropImageView.CropResult ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent
            val cropped = BitmapFactory.decodeFile(result.getUriFilePath(applicationContext, true))
            saveCroppedImage(cropped)
            if (uriContent != null) {
                saved_uri = uriContent
                binding.imageView2.setImageURI(saved_uri)
            }

            Log.d("MyLog", "Success")
        }
        else {
            // An error occurred.
            val exception = result.error
        }
    }

    private fun launchImageCropper(uri: Uri) {
        val cropImageOptions = CropImageOptions()
        cropImageOptions.imageSourceIncludeGallery = true
        cropImageOptions.imageSourceIncludeCamera = false
        cropImageOptions.aspectRatioX = 1
        cropImageOptions.aspectRatioY = 1
        cropImageOptions.outputRequestHeight=300
        cropImageOptions.outputRequestWidth=300
        //cropImageOptions.guidelines = CropImageView.Guidelines.ON
        val cropImageContractOptions = CropImageContractOptions(uri, cropImageOptions)
        try {
            cropImage.launch(cropImageContractOptions)
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    private fun saveCroppedImage(bitmap: Bitmap) {
        val myDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CroppedImages")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }

        // Generate a unique file name
        val imageName = "Image_" + Date().time + ".jpg"
        val file = File(myDir, imageName)
        if (file.exists()) file.delete()
        var fos: OutputStream? = null
        try {
            // Save the Bitmap to the file
            val outputStream: Any? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                this.contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        }
                    }
                    val imageUri :Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)
                    fos = imageUri?.let{
                        resolver.openOutputStream(it)
                    }
                }
            } else {
            }
            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                Toast.makeText(this, "Successfully", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showFailureMessage()
        }
    }
    private fun showFailureMessage() {
        Toast.makeText(applicationContext, "Cropped image not saved something went wrong", Toast.LENGTH_LONG).show()
    }
}

