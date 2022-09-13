package com.smart.translateapp.ocr.phototranslator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.smart.translateapp.ocr.phototranslator.databinding.ActivityOcrtranslateFailBinding
import com.smart.translateapp.ocr.phototranslator.databinding.FragmentTranslatingBinding

class Translating : DialogFragment() {

    private lateinit var binding: FragmentTranslatingBinding

    private val TAG = "Translating"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTranslatingBinding.inflate(inflater,container,false)

        return binding.root
    }
}