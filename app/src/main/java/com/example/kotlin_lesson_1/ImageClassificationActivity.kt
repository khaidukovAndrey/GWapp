package com.example.kotlin_lesson_1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.kotlin_lesson_1.databinding.ActivityImageClassificationBinding
import com.example.kotlin_lesson_1.ml.AutoModel3
import org.tensorflow.lite.support.image.TensorImage

class ImageClassificationActivity : AppCompatActivity(){
    private lateinit var binding: ActivityImageClassificationBinding
    private lateinit var imageView:ImageView
    private lateinit var button: Button
    private lateinit var tvOutput:TextView
    private val GALLERY_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityImageClassificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        imageView = binding.imageView
        button = binding.btnLoadImg
        tvOutput = binding.tvOutput
        button.setOnClickListener(){
            OutputGenerator()
        }


    }
    private fun OutputGenerator(){
        val model = AutoModel3.newInstance(this)
        val uriPickedPhoto = intent.getStringExtra("uri_ml")
        val bitmap = BitmapFactory.decodeStream(uriPickedPhoto?.let {
            contentResolver.openInputStream(it.toUri())
        })
// Creates inputs for reference.
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true)

        val tfimage = TensorImage.fromBitmap(bitmap)

// Runs model inference and gets result.
        val outputs = model.process(tfimage)
            .probabilityAsCategoryList.apply {
                sortByDescending { it.score }
            }
        val highProbabilityOutput = outputs[0]

        tvOutput.text=highProbabilityOutput.label
        Log.i("TAG", "outputGenerator: $highProbabilityOutput")

// Releases model resources if no longer used.
        model.close()
    }

}