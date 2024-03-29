package cs486.splash.ui.onboarding

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cs486.splash.MainActivity
import cs486.splash.R
import cs486.splash.databinding.FragmentSignInBinding
import cs486.splash.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {
    val TAG = "SIGN_IN_FRAGMENT"

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        userViewModel.user.observe(viewLifecycleOwner){
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

            lifecycleScope.launch {
                try {
                    userViewModel.signIn(email, pass)
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

        _binding!!.resetPassword.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, ResetPasswordFragment())
            fragmentTransaction.addToBackStack(null) // Optional: Add to back stack
            fragmentTransaction.commit()
        }

        return binding.root
    }
}