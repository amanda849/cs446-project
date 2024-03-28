package cs486.splash.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cs486.splash.MainActivity
import cs486.splash.R
import cs486.splash.databinding.FragmentSetupWelcomeBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SetupWelcomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetupWelcomeFragment : Fragment() {
    private var _binding: FragmentSetupWelcomeBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupWelcomeBinding.inflate(inflater, container, false)

        _binding!!.skipBtn.setOnClickListener {
            //start main activity
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        _binding!!.setupBtn.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, NameSetupFragment())
            fragmentTransaction.addToBackStack(null) // Optional: Add to back stack
            fragmentTransaction.commit()
        }
        // Inflate the layout for this fragment
        return binding.root
    }
}