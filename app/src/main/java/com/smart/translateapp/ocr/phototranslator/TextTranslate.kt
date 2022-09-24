package com.smart.translateapp.ocr.phototranslator

import android.R.attr.label
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.mlkit.nl.translate.*
import com.smart.translateapp.ocr.phototranslator.databinding.ActivityTextTranslateBinding
import kotlinx.coroutines.*
import java.io.IOException


class TextTranslate : AppCompatActivity() {

    private lateinit var binding: ActivityTextTranslateBinding

    private val TAG = "TextTranslate"

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
        binding = ActivityTextTranslateBinding.inflate(layoutInflater)
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

            changeFromTextLanguageBtn.setOnClickListener { launchLanguageActivity(1) }
            deleteTextBtn.setOnClickListener { deleteFromText() }

            interChangeLanguageBtn.setOnClickListener { interchangeLanguage() }
            changeToLanguageBtn.setOnClickListener { launchLanguageActivity(2) }
            copyToText.setOnClickListener { copyToText() }

            fromTextOnChange()

            translateBtn.setOnClickListener {
                translateText(
                    fromCode,
                    toCode,
                    fromTextField.text.toString()
                )
            }

        }
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
                    Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun launchLanguageActivity(side: Int) {

        Log.d(TAG, "Launching languages list with side $side")

        val intent = Intent(this@TextTranslate, Languages::class.java)
        intent.putExtra("languageSide", side)
        languageResultLauncher.launch(intent)
    }

    private fun fromTextOnChange() {
        val fromTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.deleteTextBtn.isVisible = count > 0
            }

            override fun afterTextChanged(s: Editable) {}
        }

        binding.fromTextField.addTextChangedListener(fromTextWatcher)
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
            translate(fromLanguageCode, toLanguageCode)

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

    private fun copyToText() {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label.toString(), binding.toTextLanguage.text)
        clipboard.setPrimaryClip(clip)
    }

    private fun interchangeLanguage() {
        TODO("Not yet implemented")
    }

    private fun deleteFromText() {
        binding.fromTextField.setText("")
        binding.toTextField.text = ""
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
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

    private fun translate(fromLanguageCode: String, toLanguageCode: String) {

        val originalText: String = binding.fromTextField.text.toString()

        Log.d(TAG, "Target language is ${Translate.TranslateOption.targetLanguage("tr")}")

        try {

            val translation = translate!!.translate(
                originalText,
                Translate
                    .TranslateOption
                    .targetLanguage(toLanguageCode),
                Translate
                    .TranslateOption
                    .sourceLanguage(fromLanguageCode),
                Translate.TranslateOption.model("base")
            )

            binding.toTextField.text = translation.translatedText

        } catch (e: Exception) {
            Log.d(TAG, "Exception - $e")
            Toast.makeText(
                applicationContext,
                "${e.message}",
                Toast.LENGTH_LONG
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

}
