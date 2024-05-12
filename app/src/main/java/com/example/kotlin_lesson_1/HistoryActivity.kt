package com.example.kotlin_lesson_1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kotlin_lesson_1.databinding.ActivityHistoryBinding

class HistoryActivity: AppCompatActivity() {
    lateinit var binding: ActivityHistoryBinding
    private val adapter = ResAdapter()
    //private val diseaseList = listOf("rodinka", "melanoma", "eczema", "polnaya_pizda","rodinka", "melanoma", "eczema", "polnaya_pizda")
    private lateinit var db: MainDbClass

    val lesions = mapOf(
        0 to "Actinic keratoses",
        1 to "Basal cell carcinoma",
        2 to "benign keratosis-like lesions",
        3 to "dermatofibroma",
        4 to "melanocytic nevi",
        5 to "pyogenic granulomas and hemorrhage",
        6 to "melanoma"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = MainDbClass.getDb(this)

        init()
    }
    private fun init()=with(binding){
        binding.apply{
            rcView.layoutManager = GridLayoutManager(this@HistoryActivity, 1)
            rcView.adapter = adapter
            db.getDao().getAllItems().asLiveData().observe(this@HistoryActivity){list->
                list.forEach {

                    adapter.addResults(Result(lesions[it.disease]))
                }

            }
        }
    }
}