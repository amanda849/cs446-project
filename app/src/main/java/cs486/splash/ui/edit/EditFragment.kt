package cs486.splash.ui.edit

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import cs486.splash.R
import cs486.splash.databinding.FragmentEditBinding
import cs486.splash.models.BowelLog
import cs486.splash.shared.Colour
import cs486.splash.shared.FactorTags
import cs486.splash.shared.SymptomTags
import cs486.splash.shared.Texture
import cs486.splash.ui.add.ColourPicker
import cs486.splash.ui.add.TexturePicker
import cs486.splash.ui.add.TimePickerSwitchable
import cs486.splash.ui.calendar.findActivity
import cs486.splash.viewmodels.BowelLogViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editViewModel =
            ViewModelProvider(this).get(EditViewModel::class.java)

        val bowelLogViewModel = ViewModelProvider(requireActivity()).get(BowelLogViewModel::class.java)

        val poopId = arguments?.getString("poopId") ?: throw IllegalStateException("PoopId must be passed to EditFragment")

        _binding = FragmentEditBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val updateLogsObserver = Observer<List<BowelLog>> { listOfLogs ->
            binding.edit.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    EditContents(poopId, bowelLogViewModel)
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
fun EditContents(
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

    var colorInt by remember {mutableStateOf(0)}
    var textureStr by remember {mutableStateOf(poop.texture.toString())}
    var pickedTimeStart by remember { mutableStateOf(Calendar.getInstance().apply { time = poop.timeStarted }) }
    var pickedTimeEnd by remember { mutableStateOf(Calendar.getInstance().apply { time = poop.timeEnded }) }
    var location by remember { mutableStateOf(poop.location) }

    val symptomsMap = poop.symptoms.toString().split(", ")
        .map {
            it.split(": ").let { pair ->
                pair[0] to pair[1].toBoolean()
            }
        }.toMap()

    var symptomsList by remember { mutableStateOf(symptomsMap.toList()) }


    val factorsMap = poop.factors.toString().split(", ")
        .map {
            it.split(": ").let { pair ->
                pair[0] to pair[1].toBoolean()
            }
        }.toMap()

    var factorsList by remember { mutableStateOf(factorsMap.toList()) }

    Box(
        Modifier
            .fillMaxWidth()
            .testTag("EditContents")
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

        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(10.dp))
                    .padding(16.dp)
                    .clickable {
                        val poopId = poopId
                        val bundle = Bundle().apply {
                            putString("poopId", poopId)
                        }
                        navController.navigate(R.id.navigation_poop_view, bundle)
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Cancel",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cancel",
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                }
            }

            Box(
                modifier = Modifier
                    .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(10.dp))
                    .padding(16.dp)
                    .clickable {
                        bowelLogViewModel.editBowelLog(
                            id = poopId,
                            color = Colour.entries[colorInt],
                            texture = Texture.valueOf(textureStr.uppercase()),
                            timeStarted = pickedTimeStart.time,
                            timeEnded = pickedTimeEnd.time,
                            location = location,
                            symptomTags = SymptomTags(symptomsList.joinToString(separator = ", ") {
                            "${it.first}: ${it.second}"
                            }),
                            factorTags = FactorTags(factorsList.joinToString(separator = ", ") {
                                "${it.first}: ${it.second}"
                            }),
                            )
                        val poopId = poopId
                        val bundle = Bundle().apply {
                            putString("poopId", poopId)
                        }
                        navController.navigate(R.id.navigation_poop_view, bundle)
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Save",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save",
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                }
            }
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            ColourPicker(
                initial = Colour.entries.indexOfFirst { it == poop.color },
                onClick = { it ->
                    colorInt = it
                    Log.d("Edit", "Colour changed to: $it")
                })
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            TexturePicker(initial = poop.texture.toString(), onClick = { it ->
                textureStr = it
                Log.d("AddLog", "Texture changed to: $it")
            })
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Box {
                TimePickerSwitchable(currTime = pickedTimeStart, onClick = { newTime ->
                    pickedTimeStart = Calendar.getInstance().apply { time = newTime.time }
                    Log.d("Edit", "SelectedDate changed to: $newTime")
                })
            }


            Box {
                TimePickerSwitchable(currTime = pickedTimeEnd, onClick = { newTime ->
                    pickedTimeEnd = Calendar.getInstance().apply { time = newTime.time }
                    Log.d("Edit", "SelectedDate changed to: $newTime")
                })
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

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
            symptomsList.forEach { symptom ->
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
                        .clickable {
                            symptomsList = symptomsList.map { it ->
                                if (it.first == symptom.first) it.first to !it.second else it
                            }
                        }

                ) {
                    Image(
                        painter = painterResource(id = if (symptom.second!!) R.drawable.ic_baseline_check_circle_24 else R.drawable.ic_outline_circle_24),
                        contentDescription = null
                    )
                    Text(text = "${symptom.first}")
                }
            }
        }

            Spacer(modifier = Modifier.height(20.dp))

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
                factorsList.forEach { factor ->
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
                            .clickable {
                                factorsList = factorsList.map { it ->
                                    if (it.first == factor.first) it.first to !it.second else it
                                }
                            }

                    ) {
                        Image(
                            painter = painterResource(id = if (factor.second!!) R.drawable.ic_baseline_check_circle_24 else R.drawable.ic_outline_circle_24),
                            contentDescription = null
                        )
                        Text(text = "${factor.first}")
                    }
                }
            }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .border(width = 2.dp, color = Color.Red, shape = RoundedCornerShape(10.dp))
                .padding(16.dp)
                .fillMaxWidth()
                .clickable {
                    Log.d("Edit", "deleting poop with id $poopId")
                    bowelLogViewModel.deleteBowelLog(
                        poopId
                    )
                    navController.navigate(R.id.navigation_calendar)
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Delete entry",
                    textAlign = TextAlign.Left,
                    color = Color.Red
                )
            }
        }

    }

    }

}