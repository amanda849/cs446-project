package cs486.splash.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cs486.splash.R
import cs486.splash.OnboardingActivity
import cs486.splash.databinding.FragmentProfileBinding
import cs486.splash.models.UserRepository
import cs486.splash.viewmodels.UserViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        userViewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                _binding!!.userEmail.text = it.email
            }
        }

        userViewModel.userProfile.observe(viewLifecycleOwner){
            if (it != null) {
                _binding!!.userName.text = it.name
                _binding!!.userBirthdate.text = it.birthDate
            }
        }

        // Edit profile button
        _binding!!.editProfileBtn.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(this.id, EditProfileFragment())
            fragmentTransaction.addToBackStack(null) // Optional: Add to back stack
            fragmentTransaction.commit()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signOutBtn: Button = view.findViewById(R.id.signOutBtn)
        signOutBtn.setOnClickListener {
            UserRepository.userSignOut()

            val intent = Intent(requireContext(), OnboardingActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}