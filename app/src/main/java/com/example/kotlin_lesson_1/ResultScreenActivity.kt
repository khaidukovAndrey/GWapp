package com.example.kotlin_lesson_1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.asLiveData
import com.example.kotlin_lesson_1.databinding.ActivityResultScreenBinding
import com.example.kotlin_lesson_1.domain.models.LesionData
import com.example.kotlin_lesson_1.domain.models.MainDbClass

class ResultScreenActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityResultScreenBinding
    private lateinit var db: MainDbClass
    private lateinit var uriPickedPhoto: String
    private val lesionData:LesionData = LesionData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val dbEntryId = intent.getStringExtra("id_entry")?.toInt()
        db = MainDbClass.getDb(this)
        db.getDao().getAllItems().asLiveData().observe(this@ResultScreenActivity){list->
            run {
                uriPickedPhoto = list[dbEntryId!!-1].imgName
                binding.imageView4.setImageURI(uriPickedPhoto.toUri())
                binding.tvOutputRes.text = lesionData.lesions[list[dbEntryId-1].disease]
                binding.descriptionView.text = lesionData.descriptions[list[dbEntryId-1].disease]
                binding.recomendationView.text = lesionData.recomendation[list[dbEntryId-1].disease]
            }
        }
    }
}