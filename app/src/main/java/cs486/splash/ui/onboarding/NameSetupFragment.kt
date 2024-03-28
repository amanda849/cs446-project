package cs486.splash.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import cs486.splash.R
import cs486.splash.databinding.FragmentNameSetupBinding
import cs486.splash.viewmodels.UserViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [NameSetupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NameSetupFragment : Fragment() {
    private var _binding: FragmentNameSetupBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNameSetupBinding.inflate(inflater, container, false)
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        _binding!!.nextBtn.setOnClickListener {
            userViewModel.setUserName(_binding!!.name.text.toString())

            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, BirthdaySetupFragment())
            fragmentTransaction.addToBackStack(null) // Optional: Add to back stack
            fragmentTransaction.commit()
        }
        return binding.root
    }
}