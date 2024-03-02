package cs486.splash.ui.add

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cs486.splash.R
import cs486.splash.databinding.FragmentAddBinding
import cs486.splash.models.SymptomTags
import cs486.splash.viewmodels.BowelLogViewModel

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bowelLogViewModel =
            ViewModelProvider(this).get(BowelLogViewModel::class.java)

        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var colorInt: Int
        binding.colourPicker.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ColourPicker() {
                    colorInt = it
                    Log.d("AddLog", "Colour changed to: $it")
                }
            }
        }

        var textureStr: String
        binding.texturePicker.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TexturePicker() {
                    textureStr = it
                    Log.d("AddLog", "Texture changed to: $it")
                }
            }
        }

        var symptoms: String
        binding.symptomPicker.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SelectTags(symptomsDef) {
                    symptoms = mapToMapStr(it)
                    Log.d("AddLog", "Symptoms changed to: $symptoms")
                }
            }
        }

        var factors: String
        binding.factorPicker.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SelectTags(factorsDef) {
                    factors = mapToMapStr(it)
                    Log.d("AddLog", "Factors changed to: $factors")
                }
            }
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



val colorsDef: List<Color> = listOf(
    Color(0xFF9C6644),
    Color(0xFF9C5444),
    Color(0xFF7F5539),
    Color(0xFF6E4428),
    Color(0xFF744627),
    Color(0xFF4C2206),
)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColourPicker(
    colours: List<Color> = colorsDef,
    onClick: (Int) -> Unit
) {
    var colour by remember { mutableStateOf(0) }
    FlowRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (i in colours.indices) {
            // circle button code obtained from: https://stackoverflow.com/questions/74583523/android-jetpack-compose-how-to-create-a-button-with-an-icon-in-it
            Button(
                onClick = {
                    onClick(i)
                    colour = i
                },
                shape = CircleShape,
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(1.dp),
                colors = ButtonColors(
                    colours[i], Color.White,
                    colours[i], colours[i]
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

val texturesDef: Map<String, Int> = mapOf(
    "Solid" to R.drawable.poops_solid,
    "Soft" to R.drawable.poops_soft,
    "Pebbles" to R.drawable.poops_pebbles
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TexturePicker(
    textures: Map<String, Int> = texturesDef,
    onClick: (String) -> Unit
) {
    var texture by remember { mutableStateOf("Solid") }
    FlowRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        for (entry in textures) {
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
                        color = if (texture == entry.key) Color(0xFFEDE0D4) else Color(0x00EDE0D4),
                        shape = RoundedCornerShape(size = 8.dp)
                    )
                    .padding(8.dp)
                    .clickable {
                        onClick(entry.key)
                        texture = entry.key
                    }

            ) {
                Image(
                    painter = painterResource(id = entry.value),
                    contentDescription = null
                )
                Text(text = entry.key)
            }
        }
    }
}

val symptomsDef: List<String> = listOf("bloating", "cramps", "pain", "nausea", "fatigue", "urgency", "other")
val factorsDef: List<String> = listOf("workout", "water", "diet", "fiber", "other")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectTags(
    tags: List<String> = symptomsDef,
    onClick: (Map<String, Boolean>) -> Unit
) {
    val asMap = tags.map { it to false }
    // sol'n to using snapshotstatemap was found on: https://stackoverflow.com/questions/74766643/how-can-i-see-a-change-in-the-state-of-a-map-if-i-cant-use-mutable-state-of-mut
    val selected: SnapshotStateMap<String, Boolean> = remember { mutableStateMapOf(*asMap.toTypedArray()) }
    FlowRow (
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (t in tags) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
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
                Image(
                    painter = painterResource(id = if (selected[t]!!) R.drawable.ic_baseline_check_circle_24 else R.drawable.ic_outline_circle_24),
                    contentDescription = null
                )
                Text (text = t)
            }
        }
    }
}