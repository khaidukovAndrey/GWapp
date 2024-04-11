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
        imageView = binding.imageView
        button = binding.btnLoadImg
        tvOutput = binding.tvOutput
        button.setOnClickListener{
            OutputGenerator2()
        }


    }
    /*private fun OutputGenerator(){
        val model = AutoModel3.newInstance(this)
        val uriPickedPhoto = intent.getStringExtra("uri_ml")
        val bitmap = BitmapFactory.decodeStream(uriPickedPhoto?.let {
            contentResolver.openInputStream(it.toUri())
        })
// Creates inputs for reference.
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true)

        val tfimage = TensorImage.fromBitmap(newBitmap)

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


    }*/
    /*private fun OutputGenerator1(){
        val model = Model.newInstance(this)
        val uriPickedPhoto = intent.getStringExtra("uri_ml")
        val bitmap = BitmapFactory.decodeStream(uriPickedPhoto?.let {
            contentResolver.openInputStream(it.toUri())
        })
        val resized = Bitmap.createScaledBitmap(bitmap, 300, 300, true)
        //val byteBuffer = ByteBuffer.allocateDirect(resized.rowBytes * resized.height)
        //bitmap.copyPixelsToBuffer(byteBuffer)
        //byteBuffer.rewind()
// Creates inputs for reference.
        val newBitmap = resized.copy(Bitmap.Config.ARGB_8888,true)
        val tfimage = TensorImage.fromBitmap(newBitmap)
        val height = resized.height
        val width = resized.width
// Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 300, 300, 3), DataType.FLOAT32)
        //val inputFeature0 = TensorBuffer.createDynamic(DataType.FLOAT32)
        val buf = tfimage.tensorBuffer.buffer
        try{
            inputFeature0.loadBuffer(buf)
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

// Runs model inference and gets result.
        val outputs = model.process(inputFeature0)

        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
        Log.d("MyLog", "outputGenerator: $outputFeature0")
// Releases model resources if no longer used.
        model.close()

    }*/

    private fun OutputGenerator2(){
        val uriPickedPhoto = intent.getStringExtra("uri_ml")
        val bitmap = BitmapFactory.decodeStream(uriPickedPhoto?.let {
            contentResolver.openInputStream(it.toUri())
        })
        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        lateinit var module:Module

        try {
            module = LiteModuleLoader.load(assetFilePath(this, "modellite.ptl"))
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