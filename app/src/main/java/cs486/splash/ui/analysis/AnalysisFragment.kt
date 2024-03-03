package cs486.splash.ui.analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import cs486.splash.databinding.FragmentAnalysisBinding
import cs486.splash.ui.add.texturesDef


class AnalysisFragment : Fragment() {

    private var _binding: FragmentAnalysisBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val analysisViewModel =
            ViewModelProvider(this).get(AnalysisViewModel::class.java)

        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val tabLayout = binding.tabLayout
        val poopAvg = binding.poopAvg

        tabLayout.apply {
            addTab(tabLayout.newTab().setText("Last Week"))
            addTab(tabLayout.newTab().setText("Last Month"))
            addTab(tabLayout.newTab().setText("Last Year"))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Change TextView content based on selected tab
                when (tab?.position) {
                    0 -> poopAvg.text = "1.0"
                    1 -> poopAvg.text = "2.0"
                    2 -> poopAvg.text = "3.0"
                }
                // Update other TextViews similarly
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselection if needed
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselection if needed
            }
        })

        binding.textureDisplay.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TextureDisplay()
            }
        }

        binding.averageColour.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                RoundedRectangle(Color(0xFF9C6644), "Cape Palliser")
            }
        }

        binding.unusualColour.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                RoundedRectangle(Color(0xFF9C5444), "Costa Del Sol")
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

@Composable
fun RoundedRectangle(
    color: Color,
    label: String
) {
    Box(
        modifier = Modifier
            .background(color = color, shape = RoundedCornerShape(8.dp)) // Rounded corners
            .padding(16.dp), // Padding around the text inside the box
            contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            color = Color.White // Set text color that contrasts well with your background
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TextureDisplay(
    textures: Map<String, Int> = texturesDef,
    blobCount: List<Int> = listOf(5, 3, 1),
    percentageCount: List<Int> = listOf(55, 35, 10)
) {
    var index = 0;
    FlowRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            for (entry in textures) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    for (i in 0 until blobCount[index]) {
                        Column {
                            Image(
                                painter = painterResource(id = entry.value),
                                contentDescription = null
                            )
                        }
                    }
                    Column {
                        Text(percentageCount[index].toString() + "%")
                    }
                }
                index++
            }
        }
    }
}