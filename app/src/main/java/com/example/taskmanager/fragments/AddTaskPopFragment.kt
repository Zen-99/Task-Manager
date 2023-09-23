package com.example.taskmanager.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentAddTaskPopBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddTaskPopFragment : Fragment() {
    private lateinit var binding: FragmentAddTaskPopBinding
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var imageUri: Uri?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentAddTaskPopBinding.inflate(inflater,container,false)
        return inflater.inflate(R.layout.fragment_add_task_pop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerEvents()
    }
    private fun registerEvents(){
        var prioritySpinner: String? = null
        var categorySpinner: String? = null
        uploadImage()
        binding.saveBtn.setOnClickListener{
            val title=binding.titleEt.text.toString()
            val description=binding.descriptionTxt.toString()
            prioritySpinner = binding.prioritySpinner.selectedItem.toString()
            categorySpinner = binding.categorySpinner.selectedItem.toString()

        }
        binding.selectImageBtn.setOnClickListener{
            resultLauncher.launch("image/*")
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
        storageRef=storageRef.child(System.currentTimeMillis().toString())
        imageUri?.let{
            storageRef.putFile(it).addOnCompleteListener{task->
                if(task.isSuccessful){
                    storageRef.downloadUrl.addOnSuccessListener {uri->
                        val map=HashMap<String,Any>()
                        map["pic"]=uri.toString()
                        firebaseFirestore.collection("images").add(map).addOnCompleteListener {firestoreTask->
                            if(firestoreTask.isSuccessful){
                                println(firestoreTask.result)
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

}