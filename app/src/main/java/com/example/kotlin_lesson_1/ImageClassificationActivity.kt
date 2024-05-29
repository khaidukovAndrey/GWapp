package com.example.kotlin_lesson_1

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.kotlin_lesson_1.databinding.ActivityImageClassificationBinding
import com.example.kotlin_lesson_1.domain.models.Item
import com.example.kotlin_lesson_1.domain.models.LesionData
import com.example.kotlin_lesson_1.domain.models.MainDbClass
import com.example.kotlin_lesson_1.domain.usecase.AssetFilePathUseCase
import com.example.kotlin_lesson_1.domain.usecase.ImageClassificationUseCase
import com.example.kotlin_lesson_1.domain.usecase.PreprocImgUseCase
import com.example.kotlin_lesson_1.domain.usecase.ProcessingResultUseCase
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.text.SimpleDateFormat
import java.util.Date


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
    private var bestScore:Float = 0.0F

    val lesionData = LesionData()
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityImageClassificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        imageView = binding.imageView3
        button = binding.btnLoadImg
        tvOutput = binding.tvOutput
        var scanFlag : Boolean = false

        uriPickedPhoto = intent.getStringExtra("uri_ml").toString()
        imageView.setImageURI(uriPickedPhoto.toUri())
        var result:FloatArray
        try {
            module = LiteModuleLoader.load(assetFilePathUseCase.execute(this, "effnet_v2_s_isic2019.ptl"))
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

        button.setOnClickListener{
            try {
                var bitmap = BitmapFactory.decodeStream(uriPickedPhoto.let {
                    contentResolver.openInputStream(it.toUri())
                })
                var preprocImg = preProcImgUseCase.execute(bitmap = bitmap)
                result = imageClassificationUseCase.execute(module, preprocImg)
                diseaseId = processingResultUseCase.execute(result)
                bestScore = result[diseaseId]
                binding.tvOutput.text = lesionData.lesions[diseaseId]
                binding.descriptionScanView.text = lesionData.descriptions[diseaseId]
                binding.recomendationScanView.text = lesionData.recomendation[diseaseId]
                scanFlag = true
            }catch(e:Exception)
            {
                e.printStackTrace()
                scanFlag = false
            }
        }

        val db = MainDbClass.getDb(this)
        binding.btnSave.setOnClickListener{
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val timeOfSave:String = dateFormat.format(Date()).toString()
            try {
                if (diseaseId!=-1 && scanFlag && bestScore>0.0F){
                    val item = Item(null, diseaseId, uriPickedPhoto, bestScore, timeOfSave)
                    Thread {
                        db.getDao().insertItem(item)
                    }.start()
                }
            }catch(e:Exception)
            {
                e.printStackTrace()
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}