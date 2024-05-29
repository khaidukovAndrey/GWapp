package com.example.kotlin_lesson_1

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin_lesson_1.databinding.ActivityInfoBinding

class InfoActivity: AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.infoView.movementMethod=ScrollingMovementMethod()
        binding.disclamerView.text = "Это приложение создано в образовательных целях. Результат, полученный приложением, не является диагнозом и не обоснован с медецинской точки зрения. Полученный результат несет рекомендательный характер и призван заставить Вас задуматься о посещении квалифицированного специалиста."
        binding.infoView.text = "Рекомендации по использованию приложения, которые помогут получить более точный результат сканирования:\n" +
                "1) Изображение должно быть хорошего качества\n" +
                "2) Следует избегать засветов или падения тени\n" +
                "3) Пораженный участок должен быть охвачен полностью и занимать не менее 50% изображения\n" +
                "4) Если пораженный участок большой, то рекомендуется оставить его часть, чтобы были различимы мелкие детали\n" +
                "5) Пораженный участок не должен быть частично закрыт волосяным покровом или какими либо предметами\n" +
                "6) На изображении должен быть исключительно участок пораженной кожи"
    }
}