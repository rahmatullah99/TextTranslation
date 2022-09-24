package com.smart.translateapp.ocr.phototranslator

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.smart.translateapp.ocr.phototranslator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.apply {
            textTranslateBtn.setOnClickListener { goToTextTranslation() }
            photoTranslateBtn.setOnClickListener { goToPhotoTranslation() }
            vpnBtn.setOnClickListener { goToVpn() }
            settingsBtn.setOnClickListener { goToSettings() }
        }
    }

    private fun goToSettings() {
        val intent = Intent(this@MainActivity,Settings::class.java)
        startActivity(intent)
    }


    private fun goToVpn() {
        val intent = Intent(this@MainActivity,TextTranslate::class.java)
        startActivity(intent)
    }

    private fun goToPhotoTranslation() {
        val intent = Intent(this@MainActivity, OCRTranslate::class.java)
        startActivity(intent)
    }

    private fun goToTextTranslation() {
        val intent = Intent(this@MainActivity, TextTranslate::class.java)
        startActivity(intent)
    }

}