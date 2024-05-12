package com.example.kotlin_lesson_1.domain.usecase

class ProcessingResultUseCase {
    fun execute(scores: FloatArray): Int {
        var maxScore = -Float.MAX_VALUE
        var maxScoreIdx = -1
        for (i in scores.indices) {
            if (scores[i] > maxScore) {
                maxScore = scores[i]
                maxScoreIdx = i
            }
        }
        return maxScoreIdx
    }
}