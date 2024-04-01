package cs486.splash.ui.analysis

import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.*


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

        suspend fun updateAnalysis() = analysisViewModel.updateAnalysisData()

        CoroutineScope(Dispatchers.Main).launch(CoroutineExceptionHandler {
            _, throwable -> Log.e("Analysis", "Error in: ${throwable.localizedMessage}")
        }) {
            val res = async { updateAnalysis() }
            res.await()
            val analysisDataWeek = analysisViewModel.getAnalysisDataWeek()
            // display weekly analysis by default on load
            updateAnalysisDisplay(binding, analysisDataWeek)
            Log.d("Analysis", "Weekly analysis data loaded: ${analysisDataWeek.hasSufficientData()}")
        }

        val analysisDataWeek = analysisViewModel.getAnalysisDataWeek()
        val analysisDataMonth = analysisViewModel.getAnalysisDataMonth()
        val analysisDataYear = analysisViewModel.getAnalysisDataYear()

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

        // Download PDF report functionality
        val reportButton = binding.downloadReportBtn
        reportButton.setOnClickListener{
            // Pop-up menu to select data for last week, month, or year
            val popup = PopupMenu(context, reportButton)
            popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                generatePDF(menuItem.title, analysisDataWeek, analysisDataMonth)
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

    /** Helper function for PDF formatting */
    private fun colourToString(colour: Colour): String {
        return colour.toString().replace("_", " ")
    }

    /** Sets colour of poop colour string for PDF formatting */
    private fun colourToSpannableString(colour: Colour): SpannableString {
        var spannable = SpannableString(colourToString(colour)) // Set text of spannable string as colour

        when (colour) {
            Colour.BROWN1, Colour.BROWN2, Colour.BROWN3 ->  {
                spannable = SpannableString("BROWN")
                spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.poop_brown)), 0, spannable.length, 0)
            }
            Colour.PALE_BROWN -> {
                spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.poop_pale_brown)), 0, spannable.length, 0)
            }
            Colour.YELLOW_BROWN -> {
                spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.poop_yellow_brown)), 0, spannable.length, 0)
            }
            Colour.LIGHT_BROWN -> {
                spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.poop_light_brown)), 0, spannable.length, 0)
            }
            Colour.PINKISH_BROWN -> {
                spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.poop_pinkish_brown)), 0, spannable.length, 0)
            }
            Colour.DARK_BROWN -> {
                spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.poop_dark_brown)), 0, spannable.length, 0)
            }
            Colour.BLACK -> {
                spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.poop_black)), 0, spannable.length, 0)
            }
            Colour.GREEN -> {
                spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.poop_green)), 0, spannable.length, 0)
            }
        }
        return spannable
    }

    /** Updates pdf_template.xml with current analysis values */
    private fun updatePdfView(view: View, data: AnalysisData) {
        view.findViewById<TextView>(R.id.poop_total).text = data.getTimesTotal()

        view.findViewById<TextView>(R.id.poop_avg).text = data.getAverageTimesPerDay()

        val textureData = data.getPercentageTextures()
        val textureString = textureData.map { it.key.toString().lowercase() + ": ${it.value.second}" }.joinToString("\n")
        view.findViewById<TextView>(R.id.textures).text = textureString

        val colourData = data.getMostCommonColours()
        val builder = SpannableStringBuilder()
        val iterator = colourData.listIterator()
        while (iterator.hasNext()) {
            val colour = iterator.next()
            val colourString = colourToSpannableString(colour)
            builder.append(colourString)
            if (iterator.hasNext()) {
                builder.append("\n")
            }
        }

        view.findViewById<TextView>(R.id.poop_colours).setText(builder, TextView.BufferType.SPANNABLE)

        view.findViewById<TextView>(R.id.unusual_colour_num).text = data.getNumUnusualColours()

        view.findViewById<TextView>(R.id.average_duration).text = data.getAverageDuration()

        view.findViewById<TextView>(R.id.most_logged_hours).text = data.getMostLogsHours()

        val symptomData = data.getPercentageSymptoms()
        val symptomString = symptomData.map { "${it.key}: ${it.value}" }.joinToString("\n")
        view.findViewById<TextView>(R.id.symptoms).text = symptomString

        val factorData = data.getPercentageFactors()
        val factorString = factorData.map { "${it.key}: ${it.value}" }.joinToString("\n")
        view.findViewById<TextView>(R.id.factors).text = factorString

        view.findViewById<TextView>(R.id.poop_location_num).text = data.getNumLocations()
    }

    private fun generatePDF(menuItem: CharSequence?, weekData: AnalysisData, monthData: AnalysisData) {
        val inflater = LayoutInflater.from(context)
        val pdfView: View = inflater.inflate(R.layout.pdf_template, null)

        var filename = ""
        val title = pdfView.findViewById<TextView>(R.id.pdf_title)
        when (menuItem.toString()) {
            "Last week" -> {
                filename = "Splash_Report_Week_of_${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}.pdf"
                title.text = "Your Analysis for the Week of ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}"
                updatePdfView(pdfView, weekData)
            }
            "Last month" -> {
                filename = "Splash_Report_Month_of_${SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())}.pdf"
                title.text = "Your Analysis for the Month of ${SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())}"
                updatePdfView(pdfView, monthData)
            }
        }

        // Sizing stuff
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        pdfView.measure(View.MeasureSpec.makeMeasureSpec(PDF_PAGE_WIDTH, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(PDF_PAGE_HEIGHT, View.MeasureSpec.AT_MOST))
        pdfView.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)

        // Creating document and drawing to it
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(
            PDF_PAGE_WIDTH,
            PDF_PAGE_HEIGHT,
            PDF_PAGE_NUMBER
        ).create()
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas
        pdfView.draw(canvas)

        doc.finishPage(page)

        // Saving file to user's files
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)

        try {
            val outputStream = FileOutputStream(file)
            doc.writeTo(outputStream)
            outputStream.close()
            Toast.makeText(context, "PDF file saved to " + file.absolutePath, Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to generate PDF: " + e.message, Toast.LENGTH_SHORT)
                .show()
        }

        doc.close()
    }

    /** Dimensions for PDF page **/
    companion object Dimensions {
        private const val PDF_PAGE_WIDTH = 1080 //8.26 Inch
        private const val PDF_PAGE_HEIGHT = 1920 //11.69 Inch
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
    textureValues: Map<Texture, Pair<Float, String>> = mapOf(
        Texture.PEBBLES to Pair(0f, "0.00%"),
        Texture.LUMPY to Pair(0f, "0.00%"),
        Texture.SAUSAGE to Pair(0f, "0.00%"),
        Texture.SMOOTH to Pair(0f, "0.00%"),
        Texture.BLOBS to Pair(0f, "0.00%"),
        Texture.MUSHY to Pair(0f, "0.00%"),
        Texture.LIQUID to Pair(0f, "0.00%")
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
    textureValues: Map<Texture, Pair<Float, String>>
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
                    val numBlobs = textureValues[entry.key]!!.first.toInt()
                    val remainder = textureValues[entry.key]!!.first - numBlobs
                    for (i in 0 until numBlobs) {
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
                    if (remainder != 0f) {
                        Log.d("Analysis", remainder.toString())
                        val imgSize = (remainder * 32).toInt()
                        Column {
                            Image(
                                painter = painterResource(id = entry.value),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(imgSize.dp)
                                    .height(32.dp)
                                    .padding(end = 3.dp),
                                contentScale = ContentScale.FillHeight,
                                alignment = Alignment.CenterStart
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(start = 2.dp)) {
                        Text(
                            textureValues[entry.key]!!.second,
                            color = Color(0xff757575)
                        )
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
                        fontWeight = FontWeight.Bold,
                        color = Color(0xff757575)
                    )
                }
            }
        }
    }
}
