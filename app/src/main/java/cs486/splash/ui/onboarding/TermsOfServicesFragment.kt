package cs486.splash.ui.onboarding

import android.R
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cs486.splash.databinding.FragmentTermsOfServicesBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TermsOfServicesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TermsOfServicesFragment : Fragment() {
    private var _binding: FragmentTermsOfServicesBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermsOfServicesBinding.inflate(inflater, container, false)

        val termsAndServicesContent = getString(cs486.splash.R.string.terms_and_services_content)
        _binding!!.termsAndSevices.text = Html.fromHtml(termsAndServicesContent)

        _binding!!.agreeButton.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(cs486.splash.R.id.fragmentContainerView, SignUpFragment())
            fragmentTransaction.addToBackStack(null) // Optional: Add to back stack
            fragmentTransaction.commit()
        }

        _binding!!.disagreeButton.setOnClickListener {
            // change to welcome fragment
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(cs486.splash.R.id.fragmentContainerView, WelcomeFragment())
            fragmentTransaction.addToBackStack(null) // Optional: Add to back stack
            fragmentTransaction.commit()
        }

        return binding.root
    }
}