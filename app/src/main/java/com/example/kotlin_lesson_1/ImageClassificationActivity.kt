package com.example.kotlin_lesson_1

import android.content.Context
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
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class ImageClassificationActivity : AppCompatActivity(){
    private lateinit var binding: ActivityImageClassificationBinding
    private lateinit var imageView:ImageView
    private lateinit var button: Button
    private lateinit var tvOutput:TextView
    private lateinit var uriPickedPhoto: String

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
        uriPickedPhoto = intent.getStringExtra("uri_ml").toString()
        val db = MainDbClass.getDb(this)
        var result =0

        imageView = binding.imageView3
        imageView.setImageURI(uriPickedPhoto.toUri())
        button = binding.btnLoadImg
        tvOutput = binding.tvOutput
        button.setOnClickListener{
            result = OutputGenerator2()
        }
        //val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        binding.btnSave.setOnClickListener{
            val item = Item(null, result, uriPickedPhoto, 70.0F)
            Thread{
                db.getDao().insertItem(item)
            }.start()
        }
    }

    private fun OutputGenerator2(): Int {

        val bitmap = BitmapFactory.decodeStream(uriPickedPhoto.let {
            contentResolver.openInputStream(it.toUri())
        })
        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        lateinit var module:Module

        try {
            module = LiteModuleLoader.load(assetFilePath(this, "modellite_effnetb01.ptl"))
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resized, TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB)
        val outputTensor: Tensor = module.forward(IValue.from(inputTensor)).toTensor()
        val scores = outputTensor.dataAsFloatArray
        var maxScore = -Float.MAX_VALUE
        var maxScoreIdx = -1
        for (i in scores.indices) {
            if (scores[i] > maxScore) {
                maxScore = scores[i]
                maxScoreIdx = i
            }
        }
        Log.d("MyLog", "$maxScoreIdx")
        binding.tvOutput.text = lesions[maxScoreIdx]
        return maxScoreIdx
    }

    fun assetFilePath(context: Context, asset: String): String {
        val file = File(context.filesDir, asset)

        try {
            val inpStream: InputStream = context.assets.open(asset)
            try {
                val outStream = FileOutputStream(file, false)
                val buffer = ByteArray(4 * 1024)
                var read: Int

                while (true) {
                    read = inpStream.read(buffer)
                    if (read == -1) {
                        break
                    }
                    outStream.write(buffer, 0, read)
                }
                outStream.flush()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

}