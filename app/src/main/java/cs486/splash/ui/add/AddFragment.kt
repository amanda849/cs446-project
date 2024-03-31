package cs486.splash.ui.add

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cs486.splash.R
import cs486.splash.databinding.FragmentAddBinding
import cs486.splash.shared.Colour
import cs486.splash.shared.colorsDef
import cs486.splash.shared.FactorTags
import cs486.splash.shared.SymptomTags
import cs486.splash.shared.Texture
import cs486.splash.viewmodels.BowelLogViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bowelLogViewModel =
            ViewModelProvider(this)[BowelLogViewModel::class.java]

        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        try {
            var colorInt: Int = 0
            binding.colourPicker.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    ColourPicker() {
                        colorInt = it
                        Log.d("AddLog", "Colour changed to: $it")
                    }
                }
            }

            var textureStr: String = "Pebbles"
            binding.texturePicker.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    TexturePicker() {
                        textureStr = it
                        Log.d("AddLog", "Texture changed to: $it")
                    }
                }
            }

            var symptoms: String = "fatigue: false, urgency: false, bloating: false, nausea: false, other: false, pain: false, cramps: false"
            binding.symptomPicker.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    SelectTags(symptomsDef) {
                        symptoms = mapToMapStr(it)
                        Log.d("AddLog", "Symptoms changed to: $symptoms")
                    }
                }
            }

            var factors: String = "fiber: false, other: false, diet: false, water: false, workout: false"
            binding.factorPicker.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    SelectTags(factorsDef) {
                        factors = mapToMapStr(it)
                        Log.d("AddLog", "Factors changed to: $factors")
                    }
                }
            }

            var pickedDate = Calendar.getInstance()
            binding.datePicker.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    DateTimePickerComponent(pickedDate){
                        pickedDate = it
                        Log.d("AddLog", "SelectedDate changed to: $it")
                    }
                }
            }

            var pickedTimeStart = Calendar.getInstance()
            var pickedTimeEnd = Calendar.getInstance()
            binding.timePickerStart.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    TimePickerSwitchable(pickedTimeStart,true, pickedTimeEnd){
                        pickedTimeStart = it
                        Log.d("AddLog", "SelectedDate changed to: $it")
                    }
                }
            }

            binding.timePickerEnd.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    TimePickerSwitchable(pickedTimeEnd, false, pickedTimeStart){
                        pickedTimeEnd = it
                        Log.d("AddLog", "SelectedDate changed to: $it")
                    }
                }
            }

            binding.addButton.setOnClickListener {
                // date things to pre-format
                var startTime = Date() // current day
                var endTime = Date()
                bowelLogViewModel.addNewBowelLog(
                    Colour.entries[colorInt],
                    Texture.valueOf(textureStr.uppercase()),
                    startTime.apply {
                        month = pickedDate.time.month
                        date = pickedDate.time.date
                        year = pickedDate.time.year
                        hours = pickedTimeStart.time.hours
                        minutes = pickedTimeStart.time.minutes
                    },
                    endTime.apply {
                        month = pickedDate.time.month
                        date = pickedDate.time.date
                        year = pickedDate.time.year
                        hours = pickedTimeEnd.time.hours
                        minutes = pickedTimeEnd.time.minutes
                    },
                    binding.location.text.toString(),
                    SymptomTags(symptoms),
                    FactorTags(factors)
                )
                Log.d("AddLog", "Added new BowelLog.")
                findNavController().navigate(R.id.action_navigation_add_to_navigation_calendar)
            }
        } catch (exception : Throwable) {
            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
                context
            )
            // set title
            alertDialogBuilder.setTitle("Error")

            // set dialog message
            alertDialogBuilder
                .setMessage(exception.message)
                .setCancelable(false)
                .setPositiveButton(
                    "Yes",
                    DialogInterface.OnClickListener { dialog, id -> // if this button is clicked, close
                        // current activity
                        dialog.dismiss()
                    })

            // create alert dialog
            val alertDialog: AlertDialog = alertDialogBuilder.create()

            // show it
            alertDialog.show()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// map of symptoms to str helpers (& vice versa)
fun mapstrToMap(str: String): Map<String, Boolean> {
    return str.split(",").map {
        val spl = it.split(": ")
        spl[0] to spl[1].toBoolean()
    }.toMap()
}

fun mapToMapStr(map: Map<String, Boolean>): String {
    return map.map {it.key + ": " + it.value.toString()}.joinToString()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColourPicker(
    colours: List<Color> = colorsDef,
    initial: Int = 0,
    onClick: (Int) -> Unit,
) {
    var colour by remember { mutableStateOf(initial) }
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        colours.forEachIndexed { index, color ->
            // Add padding between colors except for the last one
            if (index < colours.size) {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Button(
                onClick = {
                    onClick(index)
                    colour = index
                },
                shape = CircleShape,
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(1.dp),
                colors = ButtonColors(
                    color, Color.White,
                    color, color
                )
            ) {
                if (index == colour) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Favorite",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

val texturesDef: Map<Texture, Int> = mapOf(
    Texture.PEBBLES to R.drawable.poops_pebbles,
    Texture.LUMPY to R.drawable.poops_lumpy,
    Texture.SAUSAGE to R.drawable.poops_sausage,
    Texture.SMOOTH to R.drawable.poops_soft,
    Texture.BLOBS to R.drawable.poops_blobs,
    Texture.MUSHY to R.drawable.poops_mushy,
    Texture.LIQUID to R.drawable.poops_liquid
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TexturePicker(
    textures: Map<Texture, Int> = texturesDef,
    initial: String = "Pebbles",
    onClick: (String) -> Unit
) {
    var texture by remember { mutableStateOf(initial) }
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        textures.entries.forEachIndexed { index, entry ->
            if (index < textures.entries.size) {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color(0xFFEDE0D4),
                        shape = RoundedCornerShape(size = 8.dp)
                    )
                    .background(
                        color = if (texture == entry.key.toString()) Color(0xFFEDE0D4) else Color(0x00EDE0D4),
                        shape = RoundedCornerShape(size = 8.dp)
                    )
                    .padding(8.dp)
                    .clickable {
                        onClick(entry.key.toString())
                        texture = entry.key.toString()
                    }
            ) {
                Image(
                    painter = painterResource(id = entry.value),
                    contentDescription = null
                )
                Text(text = entry.key.toString())
            }
        }
    }
}

val symptomsDef: List<String> = listOf("bloating", "cramps", "pain", "nausea", "fatigue", "urgency")
val factorsDef: List<String> = listOf("workout", "water", "diet", "fiber")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectTags(
    tags: List<String> = symptomsDef,
    onClick: (Map<String, Boolean>) -> Unit
) {
    val asMap = tags.map { it to false }
    // sol'n to using snapshotstatemap was found on: https://stackoverflow.com/questions/74766643/how-can-i-see-a-change-in-the-state-of-a-map-if-i-cant-use-mutable-state-of-mut
    val selected: SnapshotStateMap<String, Boolean> = remember { mutableStateMapOf(*asMap.toTypedArray()) }

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for ((index, t) in tags.withIndex()) {
            if (index < tags.size) {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color(0xFFEDE0D4),
                        shape = RoundedCornerShape(size = 16.dp)
                    )
                    .background(
                        color = if (selected[t]!!) Color(0xFFEDE0D4) else Color(0x00EDE0D4),
                        shape = RoundedCornerShape(size = 8.dp)
                    )
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
                    .clickable {
                        selected[t] = !selected[t]!!
                        onClick(selected.toMap())
                    }
            ) {
                Text(text = t)
            }
        }
    }
}