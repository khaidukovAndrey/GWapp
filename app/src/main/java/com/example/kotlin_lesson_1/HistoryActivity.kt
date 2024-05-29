package com.example.kotlin_lesson_1

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kotlin_lesson_1.databinding.ActivityHistoryBinding
import com.example.kotlin_lesson_1.domain.models.LesionData
import com.example.kotlin_lesson_1.domain.models.MainDbClass
import com.example.kotlin_lesson_1.domain.models.Result


class HistoryActivity: AppCompatActivity() {
    lateinit var binding: ActivityHistoryBinding
    private var adapter = ResAdapter(object : OnClickListener{
        override fun onClicked(res: Result) {
            val i = Intent(this@HistoryActivity, ResultScreenActivity::class.java)
            i.putExtra("id_entry", res.id.toString())
            startActivity(i)
        }
    })
    private val lesionData: Map<Int, String> = LesionData().lesions

    //private val diseaseList = listOf("rodinka", "melanoma", "eczema", "polnaya_pizda","rodinka", "melanoma", "eczema", "polnaya_pizda")
    private lateinit var db: MainDbClass
    private var resScreenLauncher: ActivityResultLauncher<Intent>?=null

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
                    adapter.addResults(Result(lesionData[it.disease],it.imgName, it.date, it.id))
                }
            }
        }
    }
}