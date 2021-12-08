package com.example.mynotes

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.mynotes.database.NotesDatabase
import com.example.mynotes.entities.Notes
import com.example.mynotes.util.NoteBottomSheetFragment
import kotlinx.android.synthetic.main.fragment_create_note.*
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*


class CreateNoteFragment : BaseFragment(){
    var selectedColor = "#202734"
    var currentDate:String? = null
    private var webLink = ""
    private var noteId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = requireArguments().getInt("noteId",-1)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_note, container, false)
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            CreateNoteFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (noteId != -1){
            launch {
                context?.let {
                    var notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)
                    colorView.setBackgroundColor(Color.parseColor(notes.color))
                    selectedColor= notes.color.toString()
                    etNoteTitle.setText(notes.title)
                    etNoteSubTitle.setText(notes.subTitle)
                    etNoteDesc.setText(notes.noteText)

                    if (notes.webLink != ""){
                        webLink = notes.webLink!!
                        tvWebLink.text = notes.webLink
                        layoutWebUrl.visibility = View.VISIBLE
                        etWebLink.setText(notes.webLink)
                        imgUrlDelete.visibility = View.VISIBLE
                    }else{
                        imgUrlDelete.visibility = View.GONE
                        layoutWebUrl.visibility = View.GONE
                    }
                }
            }
        }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            BroadcastReceiver, IntentFilter("bottom_sheet_action")
        )

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")

        currentDate = sdf.format(Date())
        colorView.setBackgroundColor(Color.parseColor(selectedColor))

        tvDateTime.text = currentDate

        imgDone.setOnClickListener {
            if (noteId != -1){
                updateNote()
            }else{
                saveNote()
            }
        }

        imgBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        imgMore.setOnClickListener{
            var noteBottomSheetFragment = NoteBottomSheetFragment.newInstance(noteId)
            noteBottomSheetFragment.show(requireActivity().supportFragmentManager,"Note Bottom Sheet Fragment")
        }


        btnOk.setOnClickListener {
            if (etWebLink.text.toString().trim().isNotEmpty()){
                checkWebUrl()
            }else
                Toast.makeText(requireContext(),resources.getString(R.string.url_req),Toast.LENGTH_SHORT).show()
        }
        btnCancel.setOnClickListener {
            if (noteId != -1){
                tvWebLink.visibility = View.VISIBLE
                layoutWebUrl.visibility = View.GONE
            }else
                layoutWebUrl.visibility = View.GONE

        }
        imgUrlDelete.setOnClickListener {
            webLink = ""
            tvWebLink.visibility = View.GONE
            imgUrlDelete.visibility = View.GONE
            layoutWebUrl.visibility = View.GONE
        }
        tvWebLink.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse(etWebLink.text.toString()))
            startActivity(intent)
        }

    }


    private fun updateNote(){
        launch {

            context?.let {
                var notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)

                notes.title = etNoteTitle.text.toString()
                notes.subTitle = etNoteSubTitle.text.toString()
                notes.noteText = etNoteDesc.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.webLink = webLink

                NotesDatabase.getDatabase(it).noteDao().updateNote(notes)
                etNoteTitle.setText("")
                etNoteSubTitle.setText("")
                etNoteDesc.setText("")
                tvWebLink.visibility = View.GONE
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }
    private fun saveNote(){
        if (etNoteTitle.text.isNullOrEmpty()){
            Toast.makeText(context,resources.getString(R.string.title_req),Toast.LENGTH_SHORT).show()
        }
        else if (etNoteSubTitle.text.isNullOrEmpty()){

            Toast.makeText(context,resources.getString(R.string.subtitle_req),Toast.LENGTH_SHORT).show()
        }
        else if (etNoteDesc.text.isNullOrEmpty()){

            Toast.makeText(context,resources.getString(R.string.text_req),Toast.LENGTH_SHORT).show()
        }
        else{
            launch {
                var notes = Notes()
                notes.title = etNoteTitle.text.toString()
                notes.subTitle = etNoteSubTitle.text.toString()
                notes.noteText = etNoteDesc.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.webLink = webLink
                context?.let {
                    NotesDatabase.getDatabase(it).noteDao().insertNotes(notes)
                    etNoteTitle.setText("")
                    etNoteSubTitle.setText("")
                    etNoteDesc.setText("")
                    tvWebLink.visibility = View.GONE
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }

    }

    private fun deleteNote(){

        launch {
            context?.let {
                NotesDatabase.getDatabase(it).noteDao().deleteSpecificNote(noteId)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun checkWebUrl(){
        if (Patterns.WEB_URL.matcher(etWebLink.text.toString()).matches()){
            layoutWebUrl.visibility = View.GONE
            etWebLink.isEnabled = false
            webLink = etWebLink.text.toString()
            tvWebLink.visibility = View.VISIBLE
            tvWebLink.text = etWebLink.text.toString()
        }else{
            Toast.makeText(requireContext(),resources.getString(R.string.url_not_valid),Toast.LENGTH_SHORT).show()
        }
    }


    private val BroadcastReceiver : BroadcastReceiver = object :BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {

            var actionColor = p1!!.getStringExtra("action")

            when(actionColor!!){

                "Blue" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    print(selectedColor)
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }

                "Yellow" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }


                "Purple" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }


                "Green" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }

                "Orange" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }

                "Black" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }

                "WebUrl" ->{
                    layoutWebUrl.visibility = View.VISIBLE
                }
                "DeleteNote" -> {
                    deleteNote()
                }

                else -> {
                    layoutWebUrl.visibility = View.GONE
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
            }
        }

    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(BroadcastReceiver)
        super.onDestroy()
    }
}