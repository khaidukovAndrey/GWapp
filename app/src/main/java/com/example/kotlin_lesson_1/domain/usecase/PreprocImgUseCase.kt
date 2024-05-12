package com.example.kotlin_lesson_1.domain.usecase

import android.graphics.Bitmap

class PreprocImgUseCase {
    fun execute(bitmap: Bitmap): Bitmap {
        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        return resized
    }
}