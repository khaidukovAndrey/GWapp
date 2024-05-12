package com.example.kotlin_lesson_1.domain.usecase

import android.graphics.Bitmap
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils

class ImageClassificationUseCase {
    fun execute(module: Module, resized: Bitmap): FloatArray {
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            resized,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
        val outputTensor: Tensor = module.forward(IValue.from(inputTensor)).toTensor()
        return outputTensor.dataAsFloatArray
    }
}