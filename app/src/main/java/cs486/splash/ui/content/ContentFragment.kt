package cs486.splash.ui.content

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import cs486.splash.R
import cs486.splash.databinding.FragmentContentBinding
import cs486.splash.models.Content
import cs486.splash.viewmodels.ContentViewModel

class ContentFragment : Fragment() {

    private var _binding: FragmentContentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val contentViewModel =
            ViewModelProvider(this).get(ContentViewModel::class.java)

        _binding = FragmentContentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        contentViewModel.getContentsList().observe(viewLifecycleOwner) {
            binding.composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                        modifier = Modifier.padding(32.dp)
                    ) {
                            it.forEach { c ->
                                Log.i("ContentFrag", "content: $c")
                                ContentCard(c)
                            }
                        }
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

val dummyContent = Content(
    "title",
    "http://google.com",
    "source",
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec purus metus, vulputate id placerat ac, interdum sed felis. Ut imperdiet sagittis facilisis. Cras non augue metus. Donec nulla quam, tincidunt id sem a, malesuada ullamcorper lacus. Phasellus eu lectus diam. In et neque euismod, maximus nunc vitae, bibendum justo. Nulla viverra, sem vel tempor consectetur, ante purus ornare ex, sit amet porta elit purus maximus orci. Phasellus bibendum aliquam est viverra tincidunt.",
    "https://images.pexels.com/photos/17528288/pexels-photo-17528288/free-photo-of-jaguar-lying-on-tree-log.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
    "date published"
)
@Preview
@Composable
fun ContentCard(content: Content = dummyContent) {
    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color(0xFFEDE0D4), shape = RoundedCornerShape(size = 16.dp))
    ) {
        AsyncImage(
            model = content.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp)
                .background(
                    color = Color(0xFFE6CCB2),
                    shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
                )
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
                )
                .padding(12.dp)
        ) {
            Text(content.title)
            Text("by ${content.source}")
        }
    }
}