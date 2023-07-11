package com.example.besideme

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.besideme.databinding.ActivityScanBinding
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private lateinit var bitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA ) !=  PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.CAMERA

            ), REQUEST_CAMERA_CODE);
        }

//        binding.buttonCapture.setOnClickListener{
//            CorpImage.activity().setGuidelines
//        }

        binding.buttonCopy.setOnClickListener{
            var scanned_text: String = binding.textData.text.toString();
            copyToClipBoard(scanned_text);
        }
    }

//    @Override
//    protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent){
//        super.onActivityResult(requestCode, resultCode, data)
//        if(req)
//    }

    private fun getTextFromImage(bitmap: Bitmap ){
        var recognizer: TextRecognizer = TextRecognizer.Builder(this).build();

        if(! recognizer.isOperational()){
            Toast.makeText(this, "Error!!!", Toast.LENGTH_SHORT);
        }
        else{
            var frame: Frame = Frame.Builder().setBitmap(bitmap).build();
            var textBlockSparseArray: SparseArray<TextBlock> = recognizer.detect(frame);
            var stringBuilder: StringBuilder = StringBuilder();
            for (i in 0..textBlockSparseArray.size()){
                var textBlock: TextBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.value);
                stringBuilder.append("\n");
            }
            binding.textData.setText(stringBuilder.toString());
            binding.buttonCapture.setText("Retake");
            binding.buttonCopy.setVisibility(View.VISIBLE);

        }
    }

    private fun copyToClipBoard(text: String){
        var clipBoard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager;
        var clip: ClipData = ClipData.newPlainText("Copied data", text);
        clipBoard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT);
    }
    companion object  {
        const val REQUEST_CAMERA_CODE = 100
    }
}