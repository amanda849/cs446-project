package cs486.splash.ui.poopview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import cs486.splash.R
import cs486.splash.databinding.FragmentPoopViewBinding
import cs486.splash.models.BowelLog
import cs486.splash.ui.add.texturesDef
import cs486.splash.ui.calendar.findActivity
import cs486.splash.viewmodels.BowelLogViewModel
import java.text.SimpleDateFormat
import java.util.Locale

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

        val bowelLogViewModel = ViewModelProvider(requireActivity()).get(BowelLogViewModel::class.java)

        val poopId = arguments?.getString("poopId") ?: throw IllegalStateException("PoopId must be passed to PoopViewFragment")

        _binding = FragmentPoopViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val updateLogsObserver = Observer<List<BowelLog>> { listOfLogs ->
            binding.pvContents.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    PoopViewContents(poopId, bowelLogViewModel)
                }
            }
        }

        bowelLogViewModel.bowelLogs.observe(viewLifecycleOwner, updateLogsObserver)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PoopViewContents(
    poopId: String,
    bowelLogViewModel: BowelLogViewModel
) {
    val context = LocalContext.current
    val navController = remember {
        val activity = context.findActivity() // Extension function to find activity from context
        Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
    }

    val poop = bowelLogViewModel.getBowelLog(poopId)
        ?: throw IllegalStateException("poopId ${poopId} not valid")

    Box(
        Modifier
            .fillMaxWidth()
            .testTag("PoopViewContents")
            .background(colorResource(id = R.color.white))
            .padding(10.dp),
    ) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFF9C6644),
                    shape = RoundedCornerShape(8.dp)
                ) // Rounded corners
                .padding(16.dp), // Padding around the text inside the box
            contentAlignment = Alignment.Center
        ) {
            val sdf = SimpleDateFormat("hh:mm a", Locale.CANADA)
            Text(
                text = "Log at ${sdf.format(poop.timeStarted ?: "Log")}",
                textAlign = TextAlign.Center,
                color = Color.White // Set text color that contrasts well with your background
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text("Time: ", style = TextStyle(fontWeight = FontWeight.Bold))
            val sdf = SimpleDateFormat("hh:mm a", Locale.CANADA)
            Text("${sdf.format(poop.timeStarted)} - ${sdf.format(poop.timeEnded)}")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text("Location: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text("${poop.location}")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text("Texture: ", style = TextStyle(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row (modifier = Modifier.align(Alignment.CenterHorizontally)){
            Column {
                Image(
                    painter = painterResource(id = texturesDef[poop.texture.toString()] ?: R.drawable.poops_solid),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text("${poop.texture}")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val symptomsMap = poop.symptoms.toString().split(", ")
            .map {
                it.split(": ").let { pair ->
                    pair[0] to pair[1].toBoolean()
                }
            }.toMap()

        val trueSymptoms = symptomsMap.filter { it.value }.keys.toList()

        if (trueSymptoms.isNotEmpty()) {

            Row {
                Text(
                    "Symptoms: ", style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FlowRow (
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (symptom in trueSymptoms) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color(0xFFEDE0D4),
                                shape = RoundedCornerShape(size = 16.dp)
                            )
                            .background(
                                color = Color(0xFFEDE0D4),
                                shape = RoundedCornerShape(size = 8.dp)
                            )
                            .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)

                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_baseline_check_circle_24),
                            contentDescription = null
                        )
                        Text(text = "${symptom}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        val factorsMap = poop.factors.toString().split(", ")
            .map {
                it.split(": ").let { pair ->
                    pair[0] to pair[1].toBoolean()
                }
            }.toMap()

        val trueFactors = factorsMap.filter { it.value }.keys.toList()

        if (trueFactors.isNotEmpty()) {
            Row {
                Text(
                    "Factors: ", style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FlowRow (
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (factor in trueFactors) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color(0xFFEDE0D4),
                                shape = RoundedCornerShape(size = 16.dp)
                            )
                            .background(
                                color = Color(0xFFEDE0D4),
                                shape = RoundedCornerShape(size = 8.dp)
                            )
                            .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)

                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_baseline_check_circle_24),
                            contentDescription = null
                        )
                        Text(text = "${factor}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .border(width = 2.dp, color = Color.Red, shape = RoundedCornerShape(10.dp))
                .padding(16.dp)
                .clickable {
                    Log.d("PoopView", "deleting poop with id $poopId")
                    bowelLogViewModel.deleteBowelLog(
                        poopId
                    )
                    navController.navigate(R.id.navigation_calendar)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Delete entry",
                textAlign = TextAlign.Center,
                color = Color.Red
            )
        }

    }

    }

}