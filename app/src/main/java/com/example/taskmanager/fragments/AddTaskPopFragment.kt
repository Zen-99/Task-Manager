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
import com.example.taskmanager.utils.TaskData
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
    private var taskData:TaskData?=null

    fun setListener(listener: homeFragment){
        this.listener=listener
    }
    companion object{
        const val TAG="AddTaskPopFragment"

        @JvmStatic
        fun newInstance(taskId:String,title:String,description: String,priority: String,category: String,date: String)=AddTaskPopFragment().apply{
            arguments=Bundle().apply{
                putString("taskId",taskId)
                putString("title",title)
                putString("description",description)
                putString("priority",priority)
                putString("category",category)
                putString("date",date)
            }
        }
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
        if(arguments!=null){

//            val pushObj= homeFragment.pushClass(
//                title = arguments?.getString("title").toString(),
//                description = arguments?.getString("description").toString(),
//                priority = arguments?.getString("priority").toString(),
//                category = arguments?.getString("category").toString(),
//                date = arguments?.getString("date").toString()
//            )
//            taskData= TaskData(arguments?.getString("taskId").toString(),pushObj)
//            binding.titleEt.setText(taskData?.taskObj?.title)
//            binding.descriptionTxt.setText(taskData?.taskObj?.description)
//            binding.prioritySpinner.set
        }
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
        Log.d("debug 3", "Update Label called")

        // Formatting date
        val dateFormat = "dd MMM yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.ENGLISH)

        binding.tvDate.text = sdf.format(myCalendar.time)
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