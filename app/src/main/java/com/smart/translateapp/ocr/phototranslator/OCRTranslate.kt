package com.smart.translateapp.ocr.phototranslator

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.smart.translateapp.ocr.phototranslator.databinding.ActivityOcrtranslateBinding
import java.io.IOException


class OCRTranslate : AppCompatActivity() {

    private lateinit var binding: ActivityOcrtranslateBinding

    private val TAG = "OCRTranslate"
    private val REQUEST_IMAGE_CAPTURE = 1
    private var imageBitmap: Bitmap? = null
    private var changedLanguage = ""
    private var fromCode = ""
    private var toCode = ""

    private val LANGUAGE_ACTIVITY = 5

    private var translate: Translate? = null

    private val languageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            languagesResult(result)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOcrtranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences: SharedPreferences = getSharedPreferences("sp", Context.MODE_PRIVATE)

        val intent = intent
        val activity = intent.getIntExtra("activity", 0)

        if (activity == LANGUAGE_ACTIVITY) {
            Log.d(TAG, "Opening from languages activity")

        } else {
            Log.d(TAG, "Opening from main")
            binding.fromTextLanguage.text = preferences.getString("fromLang", "Auto Detect")
            fromCode = preferences.getString("fromCode", "en")!!

            binding.toTextLanguage.text = preferences.getString("toLang", "English")
            toCode = preferences.getString("toCode", "en")!!
        }

        binding.apply {

            backBtn.setOnClickListener { finish() }

            fromLanguageBtn.setOnClickListener { launchLanguageActivity(1) }

            interChangeLanguageBtn.setOnClickListener { interchangeLanguage() }
            toLanguageBtn.setOnClickListener { launchLanguageActivity(2) }

        }

        if (checkPermission()) {
            captureImage()
        } else {
            requestPermission()
        }

    }

    private fun interchangeLanguage() {
        TODO("Not yet implemented")
    }

    private fun checkPermission(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(applicationContext, CAMERA)
        return cameraPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val CODE = 200
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA), CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            val cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (cameraPermission) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Permission granted")
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Permission denied")
            }
        }
    }

    private fun captureImage() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePicture.resolveActivity(packageManager) != null) {
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageBitmap = data?.extras?.get("data") as Bitmap
            binding.capturedImage.isVisible = true
            binding.capturedImage.setImageBitmap(imageBitmap)
            detectText()
        }
    }

    private fun detectText() {
        val image = InputImage.fromBitmap(imageBitmap!!, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val result = recognizer.process(image)
            .addOnSuccessListener {
                Log.d(TAG, "Success procesing image by recognizer")

                Log.d(TAG, "Success getting text")
                val result = StringBuilder()
                for (block in it.textBlocks) {
                    Log.d(TAG, "Getting block")
                    val blockText = block.text
                    for (line in block.lines) {
                        Log.d(TAG, "Getting line")
                        val lineText = line.text
                        val lineCornerPoints = line.cornerPoints
                        val lineFrame = line.boundingBox
                        Log.d(TAG, "Text is $lineText")

                        drawTextOnBitmap(
                            imageBitmap!!,
                            lineText,
                            resources.getDimension(R.dimen.smallText5),
                            Color.WHITE,
                            line.boundingBox!!,
                            lineFrame!!.exactCenterX(),
                            lineFrame!!.exactCenterX()
                        )

                        for (element in line.elements) {
                            val elementText = element.text
                            val elementCornerPoints = element.cornerPoints
                            val elementFrame = element.boundingBox

                            val tv = TextView(this)
                            tv.text = elementText
                            tv.left = elementFrame!!.left
                            tv.top = elementFrame.top
                            Log.d(TAG, "Getting element")
                            result.append(element.text)
                            Log.d(TAG, "result is $result")
                        }

                        //binding.textFromPhoto.text = blockText
                        translateText(
                            fromCode,
                            toCode,
                            blockText)

                        Log.d(TAG, "block text is $blockText")
                    }
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Exception - ${it.message}")
            }

    }


    private fun drawTextOnBitmap(
        bitmap: Bitmap,
        text: String = "null",
        textSize: Float = 15f,
        color: Int = Color.WHITE,
        rect2: android.graphics.Rect,
        x: Float = 0.0f,
        y: Float = 0.0f
    ) {
        val workingBitmap: Bitmap = Bitmap.createBitmap(bitmap)
        val mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        /* Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            this.color = color
            this.textSize = textSize
            typeface = Typeface.SERIF
            setShadowLayer(1f,0f,1f,Color.WHITE)
            Log.d(TAG,"Drawing on bitmap $text")
            canvas.drawText(text,x,y,this)
        }

        return bitmap*/

        val rect = RectF(rect2)

        val rectPaint = Paint()
        rectPaint.setColor(Color.WHITE)
        rectPaint.setStyle(Paint.Style.STROKE)
        rectPaint.setStrokeWidth(2.0f)

        val textPaint = Paint()
        textPaint.setColor(Color.WHITE)
        textPaint.setTextSize(24.0f)

        canvas.drawRect(rect, rectPaint)

        //  Log.d("area", "text: " + text.getText() + "\nArea: " + Area);
        /**Here we are defining a Map which takes a string(Text) and a Integer X_Axis.The text will act as key to the X_Axis.
        Once We Got the X_Axis we will pass its value to a SparseIntArray which will Assign X Axis To Y Axis
        .Then We might Create another Map which will Store Both The text and the coordinates*/

        //  Log.d("area", "text: " + text.getText() + "\nArea: " + Area);
        /**Here we are defining a Map which takes a string(Text) and a Integer X_Axis.The text will act as key to the X_Axis.
         * Once We Got the X_Axis we will pass its value to a SparseIntArray which will Assign X Axis To Y Axis
         * .Then We might Create another Map which will Store Both The text and the coordinates */
        val X_Axis: Int = rect.left.toInt()
        val Y_Axis: Int = rect.bottom.toInt()

        // Renders the text at the bottom of the box.

        // Renders the text at the bottom of the box.
        Log.d("PositionXY", "x: $X_Axis |Y: $Y_Axis")
        canvas.drawText(text, rect.left, rect.bottom, textPaint)

        // retur

    }

    private fun languagesResult(result: ActivityResult?) {

        Log.d(TAG, "Language Result launching")
        if (result!!.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Language Result is OK")
            val data: Intent? = result.data
            if (data != null) {
                Log.d(TAG, "Data is not null")
                val side = data.getIntExtra("side", 0)

                binding.apply {

                    Log.d(TAG, "Side is $side")
                    Log.d(TAG, "Opening from languages")
                    changedLanguage = data.getStringExtra("language")!!

                    if (side == 1) {
                        fromTextLanguage.text = changedLanguage
                        fromCode = data.getStringExtra("from code")!!
                        // toTextLanguage.text = preferences.getString("toLang", "English")
                        Log.d(TAG, "Saved to lang is ${toTextLanguage.text}")
                        Log.d(
                            TAG,
                            "Side is 1, From code is $fromCode, to code is $toCode," +
                                    " from code change language is $changedLanguage"
                        )
                    } else if (side == 2) {
                        toTextLanguage.text = changedLanguage
                        //  fromTextLanguage.text = preferences.getString("fromLang", "Auto Detect")
                        toCode = data.getStringExtra("to code")!!
                    }
                    Log.d(TAG, "Saved from lang is ${fromTextLanguage.text}")
                    Log.d(
                        TAG, "Side is 2, From code is $fromCode, to code is $toCode," +
                                " to code chamge language is $changedLanguage"
                    )
                }
            } else {
                Log.d(TAG, "Data is null")
                Toast.makeText(
                    this,
                    "Data is null",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun launchLanguageActivity(side: Int) {

        Log.d(TAG, "Launching languages list with side $side")

        val intent = Intent(this@OCRTranslate, Languages::class.java)
        intent.putExtra("languageSide", side)
        languageResultLauncher.launch(intent)
    }

    private fun translateText(fromLanguageCode: String, toLanguageCode: String, text: String) {

        // Log.d(TAG,"Translate api language is ${Language.ENGLISH}, ml language is ${TranslateLanguage.ENGLISH} and ml code is $fromLanguageCode")
        Log.d(TAG, "ml code is ${TranslateLanguage.ENGLISH}")

        val translatingFragment = Translating()
        translatingFragment.show(supportFragmentManager, "Fragment")

        supportFragmentManager
            .beginTransaction()
            .remove(translatingFragment)
            .commit()

        if (checkInternetConnection()) {

            //If there is internet connection, get translate service and start translation:
            getTranslateService()
            translate(fromLanguageCode, toLanguageCode, text)

            supportFragmentManager
                .beginTransaction()
                .remove(translatingFragment)
                .commit()

        } else {

            Toast.makeText(
                applicationContext,
                "No connection",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun getTranslateService() {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            resources.openRawResource(R.raw.credentials).use { `is` ->
                val myCredentials = GoogleCredentials.fromStream(`is`)
                val translateOptions =
                    TranslateOptions.newBuilder().setCredentials(myCredentials).build()
                translate = translateOptions.service
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()

        }
    }

    private fun translate(text:String, fromLanguageCode: String, toLanguageCode: String) {

        Log.d(TAG, "Target language is ${Translate.TranslateOption.targetLanguage("tr")}")

        try {

            val translation = translate!!.translate(
                text,
                Translate
                    .TranslateOption
                    .targetLanguage(toLanguageCode),
                Translate
                    .TranslateOption
                    .sourceLanguage(fromLanguageCode),
                Translate.TranslateOption.model("base")
            )

            binding.textFromPhoto.text = translation.translatedText

        } catch (e: Exception) {
            Log.d(TAG, "Exception - $e")
            Toast.makeText(
                applicationContext,
                "${e.message}",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun checkInternetConnection(): Boolean {

        //Check internet connection:
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        //Means that we are connected to a network (mobile or wi-fi)
        return activeNetwork?.isConnected == true

    }

    override fun onBackPressed() {
        finish()
    }

}