package com.example.kotlin_lesson_1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kotlin_lesson_1.databinding.ActivityHistoryBinding

class HistoryActivity: AppCompatActivity() {
    lateinit var binding: ActivityHistoryBinding
    private val adapter = ResAdapter()
    private val diseaseList = listOf("rodinka", "melanoma", "eczema", "polnaya_pizda")
    private var index=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    private fun init()=with(binding){
        binding.apply{
            rcView.layoutManager = GridLayoutManager(this@HistoryActivity, 1)
            rcView.adapter = adapter
            for(i in diseaseList) {
                adapter.addResults(Result(i))
            }
        }
    }
}