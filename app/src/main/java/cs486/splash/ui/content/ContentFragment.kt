package cs486.splash.ui.content

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cs486.splash.R
import cs486.splash.databinding.FragmentContentBinding

class ContentFragment : Fragment() {

    private var _binding: FragmentContentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val contentViewModel =
            ViewModelProvider(this).get(ContentViewModel::class.java)

        _binding = FragmentContentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textContent
        contentViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}