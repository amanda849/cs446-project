package cs486.splash.ui.poopview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cs486.splash.databinding.FragmentPoopViewBinding

class PoopViewFragment : Fragment() {

    private var _binding: FragmentPoopViewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val poopViewViewModel =
            ViewModelProvider(this).get(PoopViewViewModel::class.java)

        _binding = FragmentPoopViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textView2
        poopViewViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}