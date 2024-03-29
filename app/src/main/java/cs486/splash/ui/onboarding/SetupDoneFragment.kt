package cs486.splash.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cs486.splash.MainActivity
import cs486.splash.R
import cs486.splash.databinding.FragmentSetupDoneBinding
import cs486.splash.databinding.FragmentSetupWelcomeBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SetupDoneFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetupDoneFragment : Fragment() {
    private var _binding: FragmentSetupDoneBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupDoneBinding.inflate(inflater, container, false)

        _binding!!.getStartedBtn.setOnClickListener {
            //start main activity
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
}