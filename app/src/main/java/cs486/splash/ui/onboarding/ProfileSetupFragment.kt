package cs486.splash.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import cs486.splash.MainActivity
import cs486.splash.databinding.FragmentProfileSetupBinding
import cs486.splash.viewmodels.AuthViewModel

class ProfileSetupFragment : Fragment() {
    val TAG = "PROFILE_SETUP_FRAGMENT"

    private var _binding: FragmentProfileSetupBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileSetupBinding.inflate(inflater, container, false)
        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        _binding!!.skipBtn.setOnClickListener {
            //start main activity
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        _binding!!.getStartedBtn.setOnClickListener {
            // Set user profile
            val name = _binding!!.name.text.toString()
            val birthDate = _binding!!.birthDate.text.toString()
            val familyDoctorName = _binding!!.familyDoctorName.text.toString()
            val familyDoctorPhone = _binding!!.familyDoctorPhone.text.toString()
            val familyDoctorEmail = _binding!!.familyDoctorEmail.text.toString()

            authViewModel.setUserProfile(
                name,
                birthDate,
                familyDoctorName,
                familyDoctorPhone,
                familyDoctorEmail
            )

            // start main activity
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
}