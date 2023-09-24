package com.example.taskmanager.fragments

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentAddTaskPopBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddTaskPopFragment : DialogFragment() {
    private lateinit var binding: FragmentAddTaskPopBinding
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var imageUri: Uri?=null
    private lateinit var listener: DialogSaveBtnClickListener

    fun setListener(listener: homeFragment){
        this.listener=listener
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentAddTaskPopBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
        registerEvents()
    }
    private fun registerEvents(){
        var prioritySpinner: String? = null
        var categorySpinner: String? = null
        var description: String? = null
        var tvDate: String? = null

        binding.saveBtn.setOnClickListener{
            Log.d("debug 3","Save button clicked")
            val title=binding.titleEt.text.toString()
            description=binding.descriptionTxt.text.toString()
            prioritySpinner = binding.prioritySpinner.selectedItem.toString()
            categorySpinner = binding.categorySpinner.selectedItem.toString()
            tvDate=binding.tvDate.text.toString()
            if(title.isNotEmpty()){
                uploadImage()

                listener.onSaveTask(title,binding.titleEt,
                    description!!,binding.descriptionTxt,
                    prioritySpinner!!,binding.prioritySpinner, categorySpinner!!,binding.categorySpinner,tvDate!!,binding.tvDate)
            }else{
                Toast.makeText(context,"Title cannot be empty",Toast.LENGTH_SHORT).show()
            }

        }
        binding.selectImageBtn.setOnClickListener{
            resultLauncher.launch("image/*")
        }

        binding.tvDate.setOnClickListener {
            val myCalendar=Calendar.getInstance()

            val datePicker=DatePickerDialog.OnDateSetListener{view,year,month,dayOfMonth->
                myCalendar.set(Calendar.YEAR,year)
                myCalendar.set(Calendar.MONTH,month)
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                updateLable(myCalendar)
            }
            DatePickerDialog(requireContext(),datePicker,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show()

        }

        binding.popUpClose.setOnClickListener{
            dismiss()
        }
    }
    private fun initVars(){
        storageRef= FirebaseStorage.getInstance().reference.child("Images")
        firebaseFirestore=FirebaseFirestore.getInstance()
    }

    private val resultLauncher=registerForActivityResult(
        ActivityResultContracts.GetContent()){
        imageUri=it
        binding.placeholdContainer.setImageURI(it)
    }
    private fun uploadImage(){
        Log.d("debug 3","Upload Image called")
        storageRef=storageRef.child(System.currentTimeMillis().toString())
        imageUri?.let{
            storageRef.putFile(it).addOnCompleteListener{task->
                if(task.isSuccessful){
                    storageRef.downloadUrl.addOnSuccessListener {uri->
                        val map=HashMap<String,Any>()
                        map["pic"]=uri.toString()
                        firebaseFirestore.collection("images").add(map).addOnCompleteListener {firestoreTask->
                            if(firestoreTask.isSuccessful){
                                Log.d("ImagePicker result",firestoreTask.result.toString())
                            }else{

                            }

                            binding.placeholdContainer.setImageResource(R.drawable.baseline_cloud_upload_24)
                        }

                    }
                }else{
                   //Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    private fun updateLable(myCalendar: Calendar){
        Log.d("debug 3","Upload Label called")
        val myFormat="dd-mm-yyyy"
        val sdf=SimpleDateFormat(myFormat, Locale.ENGLISH)
        binding.tvDate.setText(sdf.format(myCalendar.time))
    }

    interface DialogSaveBtnClickListener{
        fun onSaveTask(
            title:String,
            titleEt:TextInputEditText,
            description:String,
            descriptionTxt:TextInputEditText,
            priority:String,
            prioritySpinner: Spinner,
            category: String,
            categorySpinner:Spinner,
            date:String,
            tvDate: TextView
        )
    }
}