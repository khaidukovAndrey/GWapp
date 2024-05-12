package com.example.kotlin_lesson_1

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.kotlin_lesson_1.databinding.ActivityImageClassificationBinding
import com.example.kotlin_lesson_1.domain.usecase.AssetFilePathUseCase
import com.example.kotlin_lesson_1.domain.usecase.ImageClassificationUseCase
import com.example.kotlin_lesson_1.domain.usecase.PreprocImgUseCase
import com.example.kotlin_lesson_1.domain.usecase.ProcessingResultUseCase
import org.pytorch.LiteModuleLoader
import org.pytorch.Module


class ImageClassificationActivity : AppCompatActivity(){

    private val imageClassificationUseCase = ImageClassificationUseCase()
    private val preProcImgUseCase = PreprocImgUseCase()
    private val processingResultUseCase = ProcessingResultUseCase()
    private val assetFilePathUseCase = AssetFilePathUseCase()
    private lateinit var binding: ActivityImageClassificationBinding
    private lateinit var imageView:ImageView
    private lateinit var button: Button
    private lateinit var tvOutput:TextView
    private lateinit var uriPickedPhoto: String
    private lateinit var module:Module
    private var diseaseId:Int = -1

    val lesions = mapOf(
        0 to "Actinic keratoses",
        1 to "Basal cell carcinoma",
        2 to "benign keratosis-like lesions",
        3 to "dermatofibroma",
        4 to "melanocytic nevi",
        5 to "pyogenic granulomas and hemorrhage",
        6 to "melanoma"
    )
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityImageClassificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        imageView = binding.imageView3
        button = binding.btnLoadImg
        tvOutput = binding.tvOutput

        uriPickedPhoto = intent.getStringExtra("uri_ml").toString()
        imageView.setImageURI(uriPickedPhoto.toUri())
        var result:FloatArray
        try {
            module = LiteModuleLoader.load(assetFilePathUseCase.execute(this, "modellite_effnetb01.ptl"))
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

        button.setOnClickListener{
            var bitmap = BitmapFactory.decodeStream(uriPickedPhoto.let {
                contentResolver.openInputStream(it.toUri())
            })
            var preprocImg = preProcImgUseCase.execute(bitmap = bitmap)
            result = imageClassificationUseCase.execute(module, preprocImg)
            diseaseId = processingResultUseCase.execute(result)
            binding.tvOutput.text = lesions[diseaseId]
        }

        val db = MainDbClass.getDb(this)
        binding.btnSave.setOnClickListener{
            val item = Item(null, diseaseId, uriPickedPhoto, 70.0F)
            Thread{
                db.getDao().insertItem(item)
            }.start()
        }
    }

}