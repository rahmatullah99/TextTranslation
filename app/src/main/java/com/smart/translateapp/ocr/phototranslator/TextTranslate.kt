package com.smart.translateapp.ocr.phototranslator

import android.R.attr.label
import android.content.*
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.*
import com.smart.translateapp.ocr.phototranslator.databinding.ActivityTextTranslateBinding
import kotlinx.coroutines.*


class TextTranslate : AppCompatActivity() {

    private lateinit var binding: ActivityTextTranslateBinding

    private val TAG="TextTranslate"

    private var changedLanguage = ""
    private var fromCode=""
    private var toCode=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextTranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val side = intent.getIntExtra("side",0)

        val preferences: SharedPreferences = getSharedPreferences("sp", Context.MODE_PRIVATE)

        binding.apply {

            if(side!=0) {

                Log.d(TAG,"Side is $side")
                Log.d(TAG, "Opening from languages")
                changedLanguage = intent.getStringExtra("language")!!

                if (side == 1) {
                    fromTextLanguage.text = changedLanguage
                    fromCode = intent.getStringExtra("from code")!!
                    toTextLanguage.text = preferences.getString("toLang","English")
                    Log.d(TAG, "Saved to lang is ${toTextLanguage.text}")
                    Log.d(TAG, "Side is 1, From code is $fromCode, to code is $toCode , from code change language is $changedLanguage")

                }else if(side == 2){
                    toTextLanguage.text=changedLanguage
                    fromTextLanguage.text = preferences.getString("fromLang","Auto Detect")
                    toCode = intent.getStringExtra("to code")!! }
                    Log.d(TAG, "Saved from lang is ${fromTextLanguage.text}")
                    Log.d(TAG, "Side is 2, From code is $fromCode, to code is $toCode , to code chamge language is $changedLanguage")

            }else{

                Log.d(TAG,"Opening from main")
                    fromTextLanguage.text = preferences.getString("fromLang","Auto Detect")
                    fromCode = preferences.getString("fromCode","en")!!

                    toTextLanguage.text = preferences.getString("toLang","English")
                    toCode = preferences.getString("toCode","en")!! }

            backBtn.setOnClickListener { finish() }

            changeFromTextLanguageBtn.setOnClickListener { changeFromTextLanguage() }
            deleteTextBtn.setOnClickListener { deleteFromText() }

            interChangeLanguageBtn.setOnClickListener { interchangeLanguage() }
            changeToLanguageBtn.setOnClickListener { changeToTextLanguage() }
            copyToText.setOnClickListener { copyToText() }

            fromTextOnChange()

            translateBtn.setOnClickListener { translateText(fromCode, toCode, fromTextField.text.toString()) }

        }

    }

    private fun fromTextOnChange(){
        val fromTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.deleteTextBtn.isVisible = count>0
            }

            override fun afterTextChanged(s: Editable) {}
        }

        binding.fromTextField.addTextChangedListener(fromTextWatcher)
    }


    private fun translateText(fromLanguageCode:String, toLanguageCode:String, text:String) {

        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Inside coroutine scope")

            val options = TranslatorOptions
                .Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build()

            val translator = Translation
                .getClient(options)

            val conditions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.d(TAG, "Build version is greater than N")
                DownloadConditions
                    .Builder()
                    .requireCharging()
                    .build()

            } else {
                Log.d(TAG, "Build version is less than N")
                DownloadConditions
                    .Builder()
                    .build()
            }

            val downloadingFragment = Downloading()
            downloadingFragment.show(supportFragmentManager, "Fragment")

            translator
                .downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    Log.d(TAG, "Download is success. Now begining translating.")
                    supportFragmentManager
                        .beginTransaction()
                        .remove(downloadingFragment)
                        .commit()

                    val translatingFragment = Translating()
                    translatingFragment.show(supportFragmentManager, "Fragment")

                    translator.translate(text)
                        .addOnSuccessListener(OnSuccessListener<String>() {
                            supportFragmentManager
                                .beginTransaction()
                                .remove(translatingFragment)
                                .commit()

                                Log.d(TAG, "Translation is success. Translated text is $it")
                                binding.toTextField.text=it.toString()
                        })

                        .addOnFailureListener(OnFailureListener {
                            supportFragmentManager
                                .beginTransaction()
                                .remove(translatingFragment)
                                .commit()
                                Log.d(TAG, "Translation failed. Exception - $it") }) }

                .addOnFailureListener {
                    Log.d(TAG, "Download failed. Exception - $it")
                    supportFragmentManager
                        .beginTransaction()
                        .remove(downloadingFragment)
                        .commit() }


        }

    }

    private fun copyToText(){
        val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
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

    private fun changeToTextLanguage() {
        val intent = Intent(this@TextTranslate, Languages::class.java)
        intent.putExtra("languageSide",2)
        startActivity(intent)
    }

    private fun changeFromTextLanguage() {
        val intent = Intent(this@TextTranslate, Languages::class.java)
        intent.putExtra("languageSide",1)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}