package com.smart.translateapp.ocr.phototranslator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.smart.translateapp.ocr.phototranslator.databinding.ActivityOcrtranslateBinding
import com.smart.translateapp.ocr.phototranslator.databinding.ActivityOcrtranslateFailBinding

class OCRTranslateFail : AppCompatActivity() {
    private lateinit var binding: ActivityOcrtranslateFailBinding

    private val TAG = "OCRTranslateFail"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOcrtranslateFailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            backBtn.setOnClickListener { finish() }
            tryAgainBtn.setOnClickListener { tryAgain() }
            textTranslateBtn.setOnClickListener { goToTextTranslation() }
            vpnBtn.setOnClickListener { goToVpn() }

        }

    }

    private fun goToVpn() {
        TODO("Not yet implemented")
    }

    private fun goToTextTranslation() {
        val intent = Intent(this@OCRTranslateFail, TextTranslate::class.java)
        startActivity(intent)
    }

    private fun tryAgain() {
        val intent = Intent(this@OCRTranslateFail, OCRTranslate::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}