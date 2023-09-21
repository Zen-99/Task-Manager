package com.example.taskmanager.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentLoginBinding
import com.example.taskmanager.databinding.FragmentSignupBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }
    private fun init(view: View){
        navControl= Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
    }
    private fun registerEvents(){

        binding.signUpTxtBtn.setOnClickListener {
            navControl.navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.signInBtn.setOnClickListener{
            val email=binding.emailTxt.text.toString().trim()
            val password=binding.passwordTxt.text.toString().trim()

            if(email.isNotEmpty() && password.isNotEmpty()){
                binding.progressBar.visibility=View.VISIBLE
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                    OnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(context,"Successfully Logged In", Toast.LENGTH_SHORT).show()
                            navControl.navigate(R.id.action_loginFragment_to_homeFragment)
                        }else{
                            Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                        binding.progressBar.visibility=View.GONE
                    }
                )

            }
        }
    }
}