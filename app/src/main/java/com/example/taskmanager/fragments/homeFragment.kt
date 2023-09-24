package com.example.taskmanager.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.taskmanager.databinding.FragmentHomeBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class homeFragment : Fragment(), AddTaskPopFragment.DialogSaveBtnClickListener {
    data class pushClass(
        var title: String,
        var description: String,
        var priority: String,
        var category: String,
        var date:String,
    )

    private lateinit var auth:FirebaseAuth
    private lateinit var  databaseRef:DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding:FragmentHomeBinding
    private lateinit var popUpFragment: AddTaskPopFragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
    }
    private fun init(view:View){
        navController=Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
        databaseRef=FirebaseDatabase.getInstance().reference
            .child("Tasks").child(auth.currentUser?.uid.toString())

    }
    private fun registerEvents(){
        Log.d("debug 3","debug line passed 0.5")
        binding.addBtn.setOnClickListener{
            Log.d("debug 3","debug line passed 1")
            popUpFragment=AddTaskPopFragment()
            Log.d("debug 3","debug line passed 1.2")
            popUpFragment.setListener(this)
            Log.d("debug 3","debug line passed 2")
            popUpFragment.show(
                childFragmentManager,
                "AddTaskPopFragment"
            )

        }
    }

    override fun onSaveTask(
        title: String,
        titleEt: TextInputEditText,
        description: String,
        descriptionTxt: TextInputEditText,
        priority: String,
        prioritySpinner: Spinner,
        category: String,
        categorySpinner: Spinner,
        date: String,
        tvDate: TextView
    ) {
        val pushObj=pushClass(
            title=title,
            description=description,
            priority=priority,
            category=category,
            date=date
            )
        databaseRef.push().setValue(pushObj).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context,"Successfully saved",Toast.LENGTH_SHORT).show()
                titleEt.text=null
            }else{
                Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
            }
            popUpFragment.dismiss()
        }
    }


}