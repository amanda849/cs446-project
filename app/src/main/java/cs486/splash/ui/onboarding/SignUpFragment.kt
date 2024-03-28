package cs486.splash.ui.onboarding

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cs486.splash.R
import cs486.splash.databinding.FragmentSignUpBinding
import cs486.splash.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        authViewModel.user.observe(viewLifecycleOwner){
            if(it != null){
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragmentContainerView, ProfileSetupFragment())
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }

        _binding!!.signInBtn.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, SignInFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        _binding!!.signUpBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val pass = binding.password.text.toString()
            val confirmPass = binding.confirmPassword.text.toString()

            lifecycleScope.launch {
                try {
                    authViewModel.signUp(email, pass, confirmPass)
                } catch (e: Exception) {
                    // Show AlertDialog when exception is caught
                    AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage("${e.message}")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()

                    Log.e("FRAG", "Exception caught ${e.message}")
                }
            }
        }

        return binding.root
    }
}