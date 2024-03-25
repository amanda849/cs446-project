package cs486.splash.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import cs486.splash.MainActivity
import cs486.splash.R
import cs486.splash.databinding.FragmentSignInBinding
import cs486.splash.viewmodels.AuthViewModel

class SignInFragment : Fragment() {
    val TAG = "SIGN_IN_FRAGMENT"

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        authViewModel.user.observe(viewLifecycleOwner){
            if(it != null){
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
            }
        }

        _binding!!.signUpBtn.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, WelcomeFragment())
            fragmentTransaction.addToBackStack(null) // Optional: Add to back stack
            fragmentTransaction.commit()
        }

        _binding!!.signInBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val pass = binding.password.text.toString()

            authViewModel.signIn(email, pass)
        }

        return binding.root
    }
}