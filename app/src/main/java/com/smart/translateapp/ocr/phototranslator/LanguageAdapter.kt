package com.smart.translateapp.ocr.phototranslator

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.smart.translateapp.ocr.phototranslator.databinding.LanguageListItemBinding
import kotlinx.coroutines.*
import kotlin.collections.ArrayList

class LanguageAdapter(val context: Context, val side:Int, private val tappedLanguage: (Language) -> Unit)
    : RecyclerView.Adapter<LanguageViewHolder>()  {

    private val languageList = ArrayList<Language>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = LanguageListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding,context)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(languageList[position],tappedLanguage, side)
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    fun setList(languages:List<Language>){
        languageList.clear()
        languageList.addAll(languages)
    }
}

class LanguageViewHolder(private val binding: LanguageListItemBinding, private val context: Context)
    : RecyclerView.ViewHolder(binding.root){

    val TAG="LangaugeVH"
    val preferences: SharedPreferences = context.getSharedPreferences("sp", Context.MODE_PRIVATE)

    fun bind(language: Language, tappedLanguage: (Language) -> Unit, side: Int){

        Log.d(TAG,"In bind")
        binding.apply {

            languageName.text=language.languageName

            if(side==1){
                checkmark.isVisible = language.languageName == preferences.getString("fromLang","Auto Detect")
            }else{
                checkmark.isVisible = language.languageName == preferences.getString("toLang","English")
            }

            if(checkmark.isVisible){
                languageName.setTextColor(context.resources.getColor(R.color.blue_light)) }

            languageBtn.setOnClickListener {
                if(side==1){
                    val oldLanguage = preferences.getString("fromCode","en")!!
                    manageFromModel(oldLanguage,language.code)
                }else{
                    val oldLanguage = preferences.getString("toCode","en")!!
                    manageToModel(oldLanguage,language.code)
                }
                tappedLanguage(language)
            }
        }

    }

    private fun manageToModel(toDelete:String, toDownload:String){

        Log.d(TAG,"In manage to model")

        val modelManager = RemoteModelManager.getInstance()

        val toModelToDelete = TranslateRemoteModel
            .Builder(toDelete)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            modelManager.deleteDownloadedModel(toModelToDelete)
                .addOnSuccessListener {
                    Log.d(TAG,"$toDelete is deleted")
                }
                .addOnFailureListener {
                    Log.d(TAG,"error in deleting $toDelete model")
                }
        }

        val toModelToDownload = TranslateRemoteModel
            .Builder(toDownload)
            .build()
        val conditions2 = DownloadConditions
            .Builder()
            .build()
        CoroutineScope(Dispatchers.IO).launch {
            modelManager.download(toModelToDownload, conditions2)
                .addOnSuccessListener {
                    Log.d(TAG, "$toDownload model is downloaded")
                }
                .addOnFailureListener {
                    Log.d(TAG, "Error in downloading $toDownload model")
                }
        }

    }

    private fun manageFromModel(fromDelete:String, fromDownload:String){

        Log.d(TAG,"In manage from model")

        val modelManager = RemoteModelManager.getInstance()

        val fromModelToDelete = TranslateRemoteModel
            .Builder(fromDelete)
            .build()
        CoroutineScope(Dispatchers.IO).launch {
            modelManager.deleteDownloadedModel(fromModelToDelete)
                .addOnSuccessListener {
                    Log.d(TAG, "$fromDelete is deleted")
                }
                .addOnFailureListener {
                    Log.d(TAG, "error in deleting $fromDelete model")
                }
        }


        val fromModelToDownload = TranslateRemoteModel
            .Builder(fromDownload)
            .build()
        val conditions2 = DownloadConditions
            .Builder()
            .build()
        CoroutineScope(Dispatchers.IO).launch {
            modelManager.download(fromModelToDownload, conditions2)
                .addOnSuccessListener {
                    Log.d(TAG, "$fromDownload model is downloaded")
                }
                .addOnFailureListener {
                    Log.d(TAG, "Error in downloading $fromDownload model")
                }
        }
    }

}