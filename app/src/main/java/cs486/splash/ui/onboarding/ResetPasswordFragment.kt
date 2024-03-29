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
import cs486.splash.databinding.FragmentResetPasswordBinding
import cs486.splash.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class ResetPasswordFragment : Fragment() {
    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        _binding!!.resetBtn.setOnClickListener {
            lifecycleScope.launch {
                try {
                    userViewModel.resetPassword(_binding!!.email.text.toString())
                    switchToSignInFrag()
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

        _binding!!.signInBtn.setOnClickListener {
            // Switch to Sign Fragment
            switchToSignInFrag()
        }

        return binding.root
    }

    private fun switchToSignInFrag(){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, SignInFragment())
        fragmentTransaction.addToBackStack(null) // Optional: Add to back stack
        fragmentTransaction.commit()
    }
}