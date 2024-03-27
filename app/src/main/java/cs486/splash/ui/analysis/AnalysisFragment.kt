package cs486.splash.ui.analysis

import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import cs486.splash.shared.AnalysisData
import cs486.splash.shared.Colour
import cs486.splash.shared.Texture
import cs486.splash.ui.add.texturesDef
import cs486.splash.viewmodels.BowelLogViewModel


class AnalysisFragment : Fragment() {

    private var _binding: FragmentAnalysisBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val analysisViewModel =
            ViewModelProvider(this).get(BowelLogViewModel::class.java)

        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val tabLayout = binding.tabLayout

        val analysisDataWeek = analysisViewModel.getAnalysisDataWeek()
        val analysisDataMonth = analysisViewModel.getAnalysisDataMonth()
        val analysisDataYear = analysisViewModel.getAnalysisDataYear()

        Log.d("Analysis", "Loaded analysis data " + analysisDataWeek.hasSufficientData().toString())

        tabLayout.apply {
            addTab(tabLayout.newTab().setText("Last Week"))
            addTab(tabLayout.newTab().setText("Last Month"))
            addTab(tabLayout.newTab().setText("Last Year"))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Change TextView content based on selected tab
                when (tab?.position) {
                    0 -> updateAnalysisDisplay(binding, analysisDataWeek)
                    1 -> updateAnalysisDisplay(binding, analysisDataMonth)
                    2 -> updateAnalysisDisplay(binding, analysisDataYear)
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

        // Default displays on initial load
        genTextureDisplay(binding)
        genColourDisplay(binding)

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

fun genTextureDisplay(
    binding: FragmentAnalysisBinding,
    textureValues: Map<Texture, Pair<Int, String>> = mapOf(
        Texture.SOLID to Pair(0, "0"),
        Texture.SOFT to Pair(0, "0"),
        Texture.PEBBLES to Pair(0, "0")
    )
) {
    binding.textureDisplay.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            TextureDisplay(textureValues = textureValues)
        }
    }
}

fun genColourDisplay(
    binding: FragmentAnalysisBinding,
    mostCommonColours: List<Colour> = listOf()
) {
    binding.mostCommonColours.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            ColourDisplay(mostCommonColours)
        }
    }
}

fun updateAnalysisDisplay(binding: FragmentAnalysisBinding, data: AnalysisData) {
    binding.poopTotal.text = data.getTimesTotal()
    binding.poopAvg.text = data.getAverageTimesPerDay()
    genTextureDisplay(binding, data.getPercentageTextures())
    genColourDisplay(binding, data.getMostCommonColours())
    binding.unusualColourNum.text = data.getNumUnusualColours()
    binding.averageDuration.text = data.getAverageDuration()
    binding.mostLoggedHours.text = data.getMostLogsHours()
    binding.poopLocationNum.text = data.getNumLocations()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TextureDisplay(
    textures: Map<Texture, Int> = texturesDef,
    textureValues: Map<Texture, Pair<Int, String>>
) {
    var index = 0;
    FlowRow(
        modifier = Modifier.padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            for (entry in textures) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    for (i in 0 until textureValues[entry.key]!!.first) {
                        Column {
                            Image(
                                painter = painterResource(id = entry.value),
                                contentDescription = null,
                                modifier = Modifier
                                    .requiredSize(32.dp)
                                    .padding(end = 3.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.padding(start = 2.dp)) {
                        Text(textureValues[entry.key]!!.second)
                    }
                }
                index++
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColourDisplay(
    mostCommonColours: List<Colour> = listOf()
) {
    FlowRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (i in mostCommonColours.indices) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(mostCommonColours[i].toColorLong()), CircleShape)
            )
        }
    }
}
