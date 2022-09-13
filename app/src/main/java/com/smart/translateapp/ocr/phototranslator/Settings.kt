package com.smart.translateapp.ocr.phototranslator

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.smart.translateapp.ocr.phototranslator.databinding.ActivityLanguageBinding
import com.smart.translateapp.ocr.phototranslator.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val TAG="Settings"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            backBtn.setOnClickListener { finish() }
            privacyPolicyBtn.setOnClickListener { privacyPolicy() }
            rateBtn.setOnClickListener { rate() }
            shareBtn.setOnClickListener { share() }

        }
    }

    private fun rate(){
        Log.d(TAG, "Going to rate app")
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    private fun share(){
        Log.d(TAG, "Going to share")
        val sharingIntent = Intent(Intent.ACTION_SEND)

        // type of the content to be shared
        sharingIntent.type = "text/plain"
        // Body of the content
        val shareBody =
            "Check out this awesome app to translate text in images. " +
                    "https://play.google.com/store/apps/details?id=$packageName"
        // subject of the content. you can share anything
        val shareSubject = resources.getString(R.string.app_name)
        // passing body of the content
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        // passing subject of the content
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
        startActivity(Intent.createChooser(sharingIntent, "Share using"))

    }

    private fun privacyPolicy(){
        Log.d(TAG, "Going to privacy policy")
        val intent = Intent(this, PrivacyPolicy::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }




}