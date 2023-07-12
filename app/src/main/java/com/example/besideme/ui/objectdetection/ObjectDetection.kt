package com.example.besideme.ui.objectdetection

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.besideme.databinding.ActivityObjectDetectionBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
class ObjectDetection : AppCompatActivity() {
    private lateinit var binding: ActivityObjectDetectionBinding

    private lateinit var textToSpeech: TextToSpeech

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var imageLabeler: ImageLabeler

    private val option = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.INDONESIAN)
        .build()

    private lateinit var englishIndonesianTranslator: Translator

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle Permission granted/rejected
        var permissionGranted = true
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && !it.value) permissionGranted = false
        }
        if (!permissionGranted) {
            Toast.makeText(
                baseContext, "Permission request denied", Toast.LENGTH_SHORT
            ).show()
        } else {
            startCamera()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityObjectDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cameraExecutor = Executors.newSingleThreadExecutor()

        englishIndonesianTranslator = Translation.getClient(option)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
        binding.btnDetect.setOnClickListener {
            startCamera()
        }


        imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    }

    private fun initializeTtsWithLabel(labels: List<ImageLabel>) {
        var downloadConditions = DownloadConditions.Builder().build()

        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale("id")
                if (status == TextToSpeech.SUCCESS) {
                    val stringBuilder = StringBuilder()
                    var position = 1
                    for (label in labels) {
                        if (position == 1) stringBuilder.append(label.text) else stringBuilder.append(
                            ", ${label.text}"
                        )
                        position++
                    }
                    var detectionResultEng = stringBuilder.toString()
                    englishIndonesianTranslator.downloadModelIfNeeded(downloadConditions)
                        .addOnSuccessListener {
                            englishIndonesianTranslator.translate(detectionResultEng)
                                .addOnSuccessListener { translationResult ->
                                    textToSpeech.speak(
                                        translationResult,
                                        TextToSpeech.QUEUE_FLUSH,
                                        null,
                                        null
                                    )
                                }
                        }

                }
            }

        }
    }

    @androidx.camera.core.ExperimentalGetImage
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            //preview boss
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            val builder = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            val analysisUseCase = builder.build()

            analysisUseCase.setAnalyzer(
                ContextCompat.getMainExecutor(this)
            ) { imageProxy: ImageProxy ->
                val image = imageProxy.image
                if (image != null) {
                    val inputImage =
                        InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
                    imageLabeler.process(inputImage).addOnSuccessListener {
                        initializeTtsWithLabel(it)
                        Log.d(DETECTED_IMAGE_TAG, "detection running")
                    }.addOnFailureListener {
                        Log.d(DETECTED_IMAGE_TAG, "detection failed")
                    }
                }
            }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysisUseCase)

            } catch (e: Exception) {
                Log.d(TAG, "binding failed")
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val DETECTED_IMAGE_TAG = "DetectedImage"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

}