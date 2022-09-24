package com.smart.translateapp.ocr.phototranslator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.smart.translateapp.ocr.phototranslator.databinding.ActivityLanguageBinding

class Languages : AppCompatActivity() {

    private lateinit var adapter:LanguageAdapter
    private lateinit var binding: ActivityLanguageBinding
    private var languageList = ArrayList<Language>()

    private var languageSide:Int=0

    private val FROM_LANGUAGE_SIDE=1
    private val TO_LANGUAGE_SIDE=2

    private val LANGUAGE_ACTIVITY=5

    private val TAG="Languages"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent

        languageSide = intent.getIntExtra("languageSide",0)

        binding.apply {
            backBtn.setOnClickListener { finish() }
            autoDetectRadioBtn.setOnClickListener { selectAutoDetect() } }

        if(languageSide==1){
            languageList.add(Language("Auto Translate","Auto Translate",false)) }

        setLanguageList()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun selectAutoDetect() {
        binding.autoDetectRadioBtn.isChecked=true
    }

    private fun initRecyclerView(){
        Log.d(TAG, "Init recycler view")
        binding.languageRecyclerView.layoutManager = LinearLayoutManager(this)

        if(languageSide==1){
            adapter = LanguageAdapter(this,FROM_LANGUAGE_SIDE)
            {selectedLanguage:Language->tappedLanguage(selectedLanguage)}
        }else{
            adapter = LanguageAdapter(this,TO_LANGUAGE_SIDE)
            {selectedLanguage:Language->tappedLanguage(selectedLanguage)}
        }
        binding.languageRecyclerView.adapter = adapter
        adapter.setList(languageList)
        adapter.notifyDataSetChanged()
    }

    private fun setLanguageList() {
        Log.d(TAG, "Displaying knowledge")

        languageList.add(Language("Afrikaans",
             TranslateLanguage.AFRIKAANS,false))
        languageList.add(Language("Albanian",
            TranslateLanguage.ALBANIAN,false))
        languageList.add(Language("Arabic",
             TranslateLanguage.ARABIC,false))
        languageList.add(Language("Belarusian",
             TranslateLanguage.BELARUSIAN,false))
        languageList.add(Language("Bengali",
             TranslateLanguage.BENGALI,false))
        languageList.add(Language("Bulgarian",
             TranslateLanguage.BULGARIAN,false))
        languageList.add(Language("Catalan",
             TranslateLanguage.CATALAN,false))
        languageList.add(Language("Chinese",
             TranslateLanguage.CHINESE,false))
        languageList.add(Language("Croatian",
             TranslateLanguage.CROATIAN,false))
        languageList.add(Language("Czech",
             TranslateLanguage.CZECH,false))
        languageList.add(Language("Danish",
             TranslateLanguage.DANISH,false))
        languageList.add(Language("Dutch",
             TranslateLanguage.DUTCH,false))
        languageList.add(Language("English",
             TranslateLanguage.ENGLISH,false))
        languageList.add(Language("Estonian",
             TranslateLanguage.ESTONIAN,false))
        languageList.add(Language("Finnish",
             TranslateLanguage.FINNISH,false))
        languageList.add(Language("French",
             TranslateLanguage.FRENCH,false))
        languageList.add(Language("Georgian",
             TranslateLanguage.GEORGIAN,false))
        languageList.add(Language("German",
             TranslateLanguage.GREEK,false))
        languageList.add(Language("Greek",
             TranslateLanguage.GUJARATI,false))
        languageList.add(Language("Gujarati",
             TranslateLanguage.GUJARATI,false))
        languageList.add(Language("Haitian",
             TranslateLanguage.HAITIAN_CREOLE,false))
        languageList.add(Language("Hebrew",
             TranslateLanguage.HEBREW,false))
        languageList.add(Language("Hindi",
             TranslateLanguage.HINDI,false))
        languageList.add(Language("Hungarian",
             TranslateLanguage.HUNGARIAN,false))
        languageList.add(Language("Icelandic",
             TranslateLanguage.ICELANDIC,false))
        languageList.add(Language("Irish",
             TranslateLanguage.ICELANDIC,false))
        languageList.add(Language("Italian",
             TranslateLanguage.ITALIAN,false))
        languageList.add(Language("Japanese",
             TranslateLanguage.JAPANESE,false))
        languageList.add(Language("Kannada",
             TranslateLanguage.KANNADA,false))
        languageList.add(Language("Korean",
             TranslateLanguage.KOREAN,false))
        languageList.add(Language("Latvian",
             TranslateLanguage.LATVIAN,false))
        languageList.add(Language("Lithuanian",
             TranslateLanguage.LITHUANIAN,false))
        languageList.add(Language("Macedonian",
             TranslateLanguage.MACEDONIAN,false))
        languageList.add(Language("Malay",
             TranslateLanguage.MALAY,false))
        languageList.add(Language("Maltese",
             TranslateLanguage.MALTESE,false))
        languageList.add(Language("Marathi",
             TranslateLanguage.MARATHI,false))
        languageList.add(Language("Norwegian",
             TranslateLanguage.NORWEGIAN,false))
        languageList.add(Language("Persian",
             TranslateLanguage.PERSIAN,false))
        languageList.add(Language("Polish",
             TranslateLanguage.POLISH,false))
        languageList.add(Language("Portuguese",
             TranslateLanguage.PORTUGUESE,false))
        languageList.add(Language("Romanian",
             TranslateLanguage.ROMANIAN,false))
        languageList.add(Language("Russian",
             TranslateLanguage.RUSSIAN,false))
        languageList.add(Language("Slovak",
             TranslateLanguage.SLOVAK,false))
        languageList.add(Language("Slovenian",
             TranslateLanguage.SLOVENIAN,false))
        languageList.add(Language("Spanish",
             TranslateLanguage.SPANISH,false))
        languageList.add(Language("Swahili",
             TranslateLanguage.SWAHILI,false))
        languageList.add(Language("Swedish",
             TranslateLanguage.SWEDISH,false))
        languageList.add(Language("Tamil",
             TranslateLanguage.TAMIL,false))
        languageList.add(Language("Telugu",
             TranslateLanguage.TELUGU,false))
        languageList.add(Language("Thai",
             TranslateLanguage.THAI,false))
        languageList.add(Language("Turkish",
             TranslateLanguage.TURKISH,false))
        languageList.add(Language("Ukrainian",
             TranslateLanguage.UKRAINIAN,false))
        languageList.add(Language("Urdu",
             TranslateLanguage.URDU,false))
        languageList.add(Language("Vietnamese",
             TranslateLanguage.VIETNAMESE,false))
        languageList.add(Language("Welsh",
             TranslateLanguage.WELSH,false))

        initRecyclerView()

    }

    private fun tappedLanguage(language:Language){
        binding.apply {

            val intent = Intent(this@Languages, TextTranslate::class.java)
            intent.putExtra("language",language.languageName)
            intent.putExtra("activity",LANGUAGE_ACTIVITY)

            val preferences = getSharedPreferences("sp", Context.MODE_PRIVATE)
            val editor = preferences.edit()

            if(languageSide==1){
                Log.d(TAG,"Side is from")
                editor.apply {
                    putString("fromLang",language.languageName)
                    putString("fromCode",language.code)
                    apply() }
                intent.putExtra("from code",language.code)
                intent.putExtra("side",1)
            }else{
                Log.d(TAG,"Side is to")
                editor.apply {
                    putString("toLang",language.languageName)
                    putString("toCode",language.code)
                    apply() }
                intent.putExtra("to code",language.code)
                intent.putExtra("side",2) }
            setResult(Activity.RESULT_OK, intent)
            finish()

        }
    }

}