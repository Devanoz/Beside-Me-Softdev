package com.example.besideme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.example.besideme.databinding.ActivityHomeBinding
import java.util.Locale


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeTTS()

        binding.cvObjectDetection.setOnClickListener {

        }

        binding.cvTextRecognition.setOnClickListener {

        }


    }

    private fun initializeTTS() {
        textToSpeech = TextToSpeech(this) {
            if (it != TextToSpeech.ERROR ) {
                textToSpeech.language = Locale("id")
                if(it == TextToSpeech.SUCCESS) {
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