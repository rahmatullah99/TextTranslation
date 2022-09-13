package com.smart.translateapp.ocr.phototranslator

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.smart.translateapp.ocr.phototranslator.databinding.ActivityOcrtranslateBinding

class OCRTranslate : AppCompatActivity() {

    private lateinit var binding: ActivityOcrtranslateBinding

    private val TAG="OCRTranslate"
    private val REQUEST_IMAGE_CAPTURE=1
    private lateinit var imageBitmap:Bitmap
    private var changedLanguage = ""
    private var fromCode=""
    private var toCode=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOcrtranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val side = intent.getIntExtra("side",0)

        binding.apply {

            if(side!=0){
                changedLanguage = intent.getStringExtra("language").toString()
                if(side==1){
                    fromTextLanguage.text=changedLanguage
                    fromCode= intent.getStringExtra("from code")!! }
                else if(side==2){
                    toTextLanguage.text=changedLanguage
                    toCode = intent.getStringExtra("to code")!! }
            }else{

                val preferences: SharedPreferences = getSharedPreferences(
                    "sp",
                    Context.MODE_PRIVATE)

                fromTextLanguage.text = preferences.getString("fromLang","Auto Detect")
                fromCode = preferences.getString("fromCode","en")!!

                toTextLanguage.text = preferences.getString("toLang","English")
                fromCode = preferences.getString("toCode","en")!!
            }

            backBtn.setOnClickListener { finish() }
            fromLanguageBtn.setOnClickListener { changeTextLanguage() }

            interChangeLanguageBtn.setOnClickListener { interchangeLanguage() }
            toLanguageBtn.setOnClickListener { changetranslatedLanguage() }

        }

        if (checkPermission()) {
            captureImage()
        } else {
            requestPermission()
        }

    }

    private fun changetranslatedLanguage() {
        val intent = Intent(this@OCRTranslate, Languages::class.java)
        intent.putExtra("languageSide",1)
        startActivity(intent)
    }

    private fun changeTextLanguage() {
        val intent = Intent(this@OCRTranslate, Languages::class.java)
        intent.putExtra("languageSide",2)
        startActivity(intent)
    }

    private fun interchangeLanguage() {
        TODO("Not yet implemented")
    }

    private fun checkPermission():Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(applicationContext, CAMERA)
        return cameraPermission==PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(){
        val CODE=200
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA),CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty()){
            val cameraPermission=grantResults[0]==PackageManager.PERMISSION_GRANTED
            if(cameraPermission){
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                Log.d(TAG,"Permission granted")
            }else{
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                Log.d(TAG,"Permission denied")
            }
        }
    }

    private fun captureImage(){
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(takePicture.resolveActivity(packageManager)!=null){
            startActivityForResult(takePicture,REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.capturedImage.isVisible=true
            binding.capturedImage.setImageBitmap(imageBitmap)
            detectText()
        }
    }

    private fun detectText(){
        val image = InputImage.fromBitmap(imageBitmap,0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val result = recognizer.process(image).addOnSuccessListener {
            Log.d(TAG,"Success procesing image by recognizer")
            OnSuccessListener<Text>(){
                Log.d(TAG,"Success getting text")
                val result = StringBuilder()
                for(block in it.textBlocks){
                    Log.d(TAG,"Getting block")
                    val blockText = block.text
                    for(line in block.lines){
                        Log.d(TAG,"Getting line")
                        for(element in line.elements){
                            Log.d(TAG,"Getting element")
                            result.append(element.text)
                            Log.d(TAG,"result is $result")
                        }
                        binding.textFromPhoto.text=blockText
                        Log.d(TAG,"block text is $blockText")
                    }
                }
            }
        }

    }

    override fun onBackPressed() {
        finish()
    }

}