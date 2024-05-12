package com.example.kotlin_lesson_1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin_lesson_1.databinding.ActivityScanMenuBinding

class ScanMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanMenuBinding
    private var pickedPhoto: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityScanMenuBinding.inflate(layoutInflater)
            setContentView(binding.root)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.scanMenuTakePhoto.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        val changeImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                pickedPhoto = data?.data
                val i = Intent(this, ImageCropperActivity::class.java)
                i.putExtra("uri", pickedPhoto.toString())
                startActivity(i)
            }
        }
        binding.scanMenuLoadPhoto.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }
    }
}