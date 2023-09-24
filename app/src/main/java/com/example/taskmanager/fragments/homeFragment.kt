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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.databinding.FragmentHomeBinding
import com.example.taskmanager.utils.TaskAdapter
import com.example.taskmanager.utils.TaskData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue


class homeFragment : Fragment(), AddTaskPopFragment.DialogSaveBtnClickListener,
    TaskAdapter.TaskAdapterClickInterface {
    data class pushClass(
        var title: String,
        var description: String,
        var priority: String,
        var category: String,
        var date:String,
    )

    private lateinit var auth:FirebaseAuth
    private lateinit var databaseRef:DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding:FragmentHomeBinding
    private lateinit var popUpFragment: AddTaskPopFragment
    private lateinit var adapter: TaskAdapter
    private lateinit var mlist:MutableList<TaskData>
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
        getDataFromFirebase()
        registerEvents()
    }
    private fun init(view:View){
        navController=Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
        databaseRef=FirebaseDatabase.getInstance().reference
            .child("Tasks").child(auth.currentUser?.uid.toString())

        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager=LinearLayoutManager(context)
        mlist= mutableListOf()
        adapter= TaskAdapter(mlist)
        adapter.setListener(this)
        binding.recyclerview.adapter=adapter
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

    override fun onDeleteTaskBtnClicked(taskData: TaskData) {
        databaseRef.child(taskData.taskId).removeValue().addOnCompleteListener{
           if(it.isSuccessful){
               Toast.makeText(context,"Successfully deleted",Toast.LENGTH_SHORT).show()
           }else{
               Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
           }
        }
    }

    override fun onEditTaskBtnClicked(taskData: TaskData) {
        TODO("Not yet implemented")
    }

    private fun getDataFromFirebase(){
        databaseRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mlist.clear()
                for(taskSnapShot in snapshot.children){
                    val taskSnap=taskSnapShot.key?.let{
                        val pushObj=pushClass(
                            title=taskSnapShot.child("title").value.toString(),
                            description=taskSnapShot.child("description").value.toString(),
                            priority=taskSnapShot.child("priority").value.toString(),
                            category=taskSnapShot.child("category").value.toString(),
                            date=taskSnapShot.child("date").value.toString(),
                        )
                        TaskData(it,pushObj)
                        mlist.add(TaskData(it,pushObj))
                        val taskData=taskSnapShot.child("date").value

                        Log.d("debug snapshot",taskData.toString())
                    }
//                    if(taskSnap!=null){
//                       mlist.add(TaskData())
//                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }


}

