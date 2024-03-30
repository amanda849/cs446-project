package cs486.splash.ui.analysis

import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import cs486.splash.R
import cs486.splash.databinding.FragmentAnalysisBinding
import cs486.splash.shared.AnalysisData
import cs486.splash.shared.Colour
import cs486.splash.shared.Texture
import cs486.splash.ui.add.texturesDef
import cs486.splash.viewmodels.BowelLogViewModel
import java.io.File
import java.io.FileOutputStream


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
        genSymptomsDisplay(binding)
        genFactorsDisplay(binding)


        // Download PDF report functionality
        val reportButton = binding.downloadReportBtn
        reportButton.setOnClickListener{
            // Pop-up menu to select data for last week, month, or year
            val popup = PopupMenu(context, reportButton)
            popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                Toast.makeText(context, "Selected " + menuItem.title, Toast.LENGTH_SHORT).show()
                generatePDF(menuItem.title)
                true
            }

            popup.show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generatePDF(s: CharSequence?) {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(
            PDF_PAGE_WIDTH,
            PDF_PAGE_HEIGHT,
            PDF_PAGE_NUMBER
        ).create()

        val page = doc.startPage(pageInfo)
        val canvas = page.canvas
        val title = Paint()
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
        title.textSize = 15f

        canvas.drawText("Report for " + s.toString().lowercase(), 209F, 100F, title)

        doc.finishPage(page)

        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Plop Report.pdf")

        try {
            doc.writeTo(FileOutputStream(file))
            Toast.makeText(context, "PDF file generated...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to generate PDF..." + e.message, Toast.LENGTH_SHORT).show()
        }

        doc.close()
    }

    companion object {
        /**Dimension For A4 Size Paper (1 inch = 72 points)**/
        private const val PDF_PAGE_WIDTH = 595 //8.26 Inch
        private const val PDF_PAGE_HEIGHT = 842 //11.69 Inch
        private const val PDF_PAGE_NUMBER = 1
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
        Texture.SOLID to Pair(0, "0.00%"),
        Texture.SOFT to Pair(0, "0.00%"),
        Texture.PEBBLES to Pair(0, "0.00%")
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

fun genSymptomsDisplay(
    binding: FragmentAnalysisBinding,
    factors: Map<String, String> = mapOf(
        "bloating" to "0.00%",
        "cramps" to "0.00%",
        "pain" to "0.00%",
        "nausea" to "0.00%",
        "fatigue" to "0.00%",
        "urgency" to "0.00%"
    )
) {
    binding.symptoms.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            FactorOrSymptomDisplay(factors)
        }
    }
}

fun genFactorsDisplay(
    binding: FragmentAnalysisBinding,
    symptoms: Map<String, String> = mapOf(
        "workout" to "0.00%",
        "water" to "0.00%",
        "diet" to "0.00%",
        "fiber" to "0.00%"
    )
) {
    binding.factors.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            FactorOrSymptomDisplay(symptoms)
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
    genSymptomsDisplay(binding, data.getPercentageSymptoms())
    genFactorsDisplay(binding, data.getPercentageFactors())
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
    mostCommonColours: List<Colour>
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FactorOrSymptomDisplay(
    factOrSymp : Map<String, String>
) {
    FlowRow(
        modifier = Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            for (entry in factOrSymp) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = entry.key + ": " + entry.value,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
