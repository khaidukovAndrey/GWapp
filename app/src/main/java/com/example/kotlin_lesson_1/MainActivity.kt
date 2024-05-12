package com.example.kotlin_lesson_1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin_lesson_1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?){
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
        binding.mainMenuGotoScan.setOnClickListener {
            val intent = Intent(this, ScanMenuActivity::class.java)
            startActivity(intent)
        }
        binding.mainMenuShowHistory.setOnClickListener{
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }
}