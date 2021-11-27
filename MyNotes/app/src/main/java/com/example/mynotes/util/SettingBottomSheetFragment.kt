package com.example.mynotes.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_notes_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import com.example.mynotes.*


class SettingBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(): SettingBottomSheetFragment{
            val args = Bundle()
            val fragment = SettingBottomSheetFragment()
            fragment.arguments = args
            return fragment
        }
    }


    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_settings,null)
        dialog.setContentView(view)
        val param = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = param.behavior
        if (behavior is BottomSheetBehavior<*>){
            behavior.setBottomSheetCallback(object  : BottomSheetBehavior.BottomSheetCallback(){
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    TODO("Not yet implemented")
                }
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    var state = ""
                    when (newState) {
                        BottomSheetBehavior.STATE_DRAGGING -> {
                            state = "DRAGGING"
                        }
                        BottomSheetBehavior.STATE_SETTLING -> {
                            state = "SETTLING"

                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            state = "EXPANDED"
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            state = "COLLAPSED"
                        }

                        BottomSheetBehavior.STATE_HIDDEN -> {
                            state = "HIDDEN"
                            dismiss()
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lang= langus
        println(lang)
        if(lang=="en"){
            checkEn.visibility = View.VISIBLE
            checkRu.visibility = View.INVISIBLE

        }else{
            checkEn.visibility = View.INVISIBLE
            checkRu.visibility = View.VISIBLE
        }
        setListener()
    }

    private fun setListener(){
        langEng.setOnClickListener{
            if (langus!="en") {
                changeLanguage("en")
                saveLanguage()
                activity?.recreate()
            }
        }
        langRus.setOnClickListener{
            if (langus!="ru") {
                changeLanguage("ru")
                saveLanguage()
                activity?.recreate()
            }
        }
    }
    fun saveLanguage(){
        val sharedPreferences = context?.getSharedPreferences("sharedPrefs",Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.apply {
            putString("STRING_KEY", langus)
        }?.apply()
    }

    fun changeLanguage(lang:String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        langus=Locale.getDefault().language
    }


}