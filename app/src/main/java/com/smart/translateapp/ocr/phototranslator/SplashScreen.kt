package com.smart.translateapp.ocr.phototranslator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.smart.translateapp.ocr.phototranslator.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    val TAG = "Splash Screen"
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            Log.d(TAG, "In splash screen")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()}, 2000)

    }
}