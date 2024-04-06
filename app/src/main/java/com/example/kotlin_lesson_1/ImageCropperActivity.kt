package com.example.kotlin_lesson_1

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.kotlin_lesson_1.databinding.ActivityImageCropperBinding


class ImageCropperActivity : AppCompatActivity() {
    private lateinit var binding : ActivityImageCropperBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
            binding = ActivityImageCropperBinding.inflate(layoutInflater)
            setContentView(binding.root)
        }catch (e:Exception){
            e.printStackTrace()
        }
        binding.selectImage.setOnClickListener {
            if (isPermitted()) {
                getImageFile()
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
    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission())
    { isGranted: Boolean ->
        if (isGranted) {
            getImageFile()
        } else {
            permissionDenied()
        }
    }

    private var android11StoragePermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { _: ActivityResult? ->
        if (isPermitted()) {
            getImageFile()
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

    private fun getImageFile() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        getImage.launch(intent)
    }


    private var getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null && data.data != null) {
                val imageUri = data.data
                if (imageUri != null) {
                    launchImageCropper(imageUri)
                }
            }
        }
    }

    private fun permissionDenied() {
        Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_LONG).show()
    }

    private var cropImage = registerForActivityResult(CropImageContract()) { result: CropImageView.CropResult ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent
            val cropped = BitmapFactory.decodeFile(result.getUriFilePath(applicationContext, true))
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
        cropImageOptions.guidelines = CropImageView.Guidelines.ON
        val cropImageContractOptions = CropImageContractOptions(uri, cropImageOptions)
        try {
            cropImage.launch(cropImageContractOptions)
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

    }
}