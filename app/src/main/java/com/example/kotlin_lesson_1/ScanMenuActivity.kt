package com.example.kotlin_lesson_1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin_lesson_1.databinding.ActivityScanMenuBinding

class ScanMenuActivity : AppCompatActivity() {
    private lateinit var binding : ActivityScanMenuBinding
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        try{
            binding = ActivityScanMenuBinding.inflate(layoutInflater)
            setContentView(binding.root)
        }catch (e:Exception){
            e.printStackTrace()
        }
        binding.scanMenuTakePhoto.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }
}