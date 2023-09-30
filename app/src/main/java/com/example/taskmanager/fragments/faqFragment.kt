package com.example.taskmanager.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentFaq2Binding
import com.example.taskmanager.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth


class faqFragment : Fragment() {
    private lateinit var binding: FragmentFaq2Binding
    private lateinit var navControl: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentFaq2Binding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
    }
    private fun init(view: View){
        navControl= Navigation.findNavController(view)
    }
    private fun registerEvents(){
        binding.backBtn.setOnClickListener{
            navControl.navigate(R.id.action_faqFragment_to_homeFragment)
        }
    }


}