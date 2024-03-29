package cs486.splash.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cs486.splash.databinding.FragmentEditProfileBinding
import cs486.splash.viewmodels.UserViewModel

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        userViewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                _binding!!.emailField.setText(it.email)
            }
        }

        userViewModel.userProfile.observe(viewLifecycleOwner){
            if (it != null) {
                _binding!!.nameField.setText(it.name)
                _binding!!.bdayField.setText(it.birthDate)
            }
        }

        // Save button
        _binding!!.saveBtn.setOnClickListener {
            // VIEW MODEL HANDLING HERE
            // **Note: some fields (password field) are empty by default, so maybe put a check
            //      for null string so it doesn't update the viewmodel with an empty string lol**

            // nameField = user's name
            // emailField = user's new email (default text is their original email)
            // confirmEmailField = email confirmation
            // passwordField = user's new password (default is empty)
            // confirmPasswordField = password confirmation
            // bdayField = user's birth date

            // Go back to profile after updating view model
            returnToProfile()
        }

        // Back to profile page button
        _binding!!.backBtn.setOnClickListener {
            returnToProfile()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Remove this fragment and display profile page fragment again
    private fun returnToProfile() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }
}