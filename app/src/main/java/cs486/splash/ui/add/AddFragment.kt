package cs486.splash.ui.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cs486.splash.R
import cs486.splash.databinding.FragmentAddBinding
import cs486.splash.viewmodels.BowelLogViewModel

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addViewModel =
            ViewModelProvider(this).get(AddViewModel::class.java)
        val bowelLogViewModel =
            ViewModelProvider(this).get(BowelLogViewModel::class.java)

        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textAdd
        addViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        binding.colourPicker.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ColourPicker()
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun ColourPicker(colour: Int = 0) {
    val colors: List<Color> = listOf(
        Color(0xFF9C6644),
        Color(0xFF9C5444),
        Color(0xFF7F5539),
        Color(0xFF6E4428),
        Color(0xFF744627),
        Color(0xFF4C2206),
    )
    FlowRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (i in colors.indices) {
            // circle button code obtained from: https://stackoverflow.com/questions/74583523/android-jetpack-compose-how-to-create-a-button-with-an-icon-in-it
            Button(
                onClick = { /* ... */ },
                shape = CircleShape,
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(1.dp),
                colors = ButtonColors(
                    colors[i], Color.White,
                    colors[i], colors[i]
                )
            ) {
                if (i == colour) Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Favorite",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}