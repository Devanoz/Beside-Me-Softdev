package com.example.besideme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatDelegate
import com.example.besideme.databinding.ActivityHomeBinding
import com.example.besideme.ui.objectdetection.ObjectDetection
import com.example.besideme.ui.textrecognition.TextRecognition
import java.util.Locale


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private lateinit var textToSpeech: TextToSpeech

    @androidx.camera.core.ExperimentalGetImage
    override fun  onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultNightMode()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeTTS()

        binding.cvObjectDetection.setOnClickListener {
            val intent = Intent(this,ObjectDetection::class.java)
            startActivity(intent)
        }

        binding.cvTextRecognition.setOnClickListener {
            val intent = Intent(this, TextRecognition::class.java)
            startActivity(intent)
        }
    }

    private fun setDefaultNightMode() { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) }


    private fun initializeTTS() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR ) {
                textToSpeech.language = Locale("id")
                if(status == TextToSpeech.SUCCESS) {
                    textToSpeech.speak(
                        getString(R.string.home_activity_welcoming_message),
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                }
            }

        }
    }
}