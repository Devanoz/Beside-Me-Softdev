package com.example.besideme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.example.besideme.databinding.ActivityHomeBinding
import com.example.besideme.ui.objectdetection.ObjectDetection
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
            val intent = Intent(this,ObjectDetection::class.java)
            startActivity(intent)
        }

        binding.cvTextRecognition.setOnClickListener {

        }


    }

    override fun onResume() {
        super.onResume()
        resumeTTS()
    }

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

    private fun resumeTTS() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR ) {
                textToSpeech.language = Locale("id")
                if(status == TextToSpeech.SUCCESS) {
                    textToSpeech.speak(
                        getString(R.string.home_activity_feature_reexplanation),
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                }
            }

        }
    }
}