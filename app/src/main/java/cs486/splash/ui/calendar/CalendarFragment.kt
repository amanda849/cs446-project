package cs486.splash.ui.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.ContentHeightMode
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.core.yearMonth
import cs486.splash.R
import cs486.splash.databinding.FragmentCalendarBinding
import cs486.splash.models.BowelLog
import cs486.splash.viewmodels.BowelLogViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bowelLogViewModel = ViewModelProvider(requireActivity()).get(BowelLogViewModel::class.java)

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val updateLogsByDayObserver = Observer<List<BowelLog>> { listOfLogs ->
            binding.calendar.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    CalendarPage(bowelLogViewModel)
                }
            }
        }

        bowelLogViewModel.bowelLogs.observe(viewLifecycleOwner, updateLogsByDayObserver)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CalendarPage(bowelLogViewModel : BowelLogViewModel) {
    val today = remember { LocalDate.now() }
    val currentMonth = remember(today) { today.yearMonth }
    val startMonth = remember { currentMonth.minusMonths(500) }
    val endMonth = remember { currentMonth.plusMonths(500) }
    var selection by remember { mutableStateOf<CalendarDay?>(null) }
    val daysOfWeek = remember { daysOfWeek() }
    val logsInSelectedDate = remember {
        derivedStateOf {
            val date = selection?.date
            if (date == null) emptyList()
            else bowelLogViewModel.getBowelLogsOnLocalDate(date).orEmpty()
        }
    }
    StatusBarColorUpdateEffect(color = colorResource(id = R.color.white))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
            .padding(top = 20.dp),
    ) {
        val state = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = daysOfWeek.first(),
            outDateStyle = OutDateStyle.EndOfGrid,
        )
        val coroutineScope = rememberCoroutineScope()
        val visibleMonth = rememberFirstVisibleMonthAfterScroll(state)
        LaunchedEffect(visibleMonth) {
            // Clear selection if we scroll to a new month.
            selection = null
        }
        // Draw dark content on light background.
        CompositionLocalProvider(LocalContentColor provides lightColorScheme().onSurface) {
            SimpleCalendarTitle(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                currentMonth = visibleMonth.yearMonth,
                goToPrevious = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                    }
                },
                goToNext = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                    }
                },
            )
            FullScreenCalendar(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.white))
                    .testTag("Calendar"),
                state = state,
                dayContent = { day ->
                    val logsOnDate = bowelLogViewModel.getBowelLogsOnLocalDate(day.date).orEmpty()
                    val color = if (logsOnDate.isNotEmpty()) {
                        Color(logsOnDate[0].color.toColorLong())
                    } else {
                        Color.Transparent
                    }
                    Day(
                        day = day,
                        poopColor = color,
                        isSelected = selection == day,
                        isToday = day.position == DayPosition.MonthDate && day.date == today,
                    ) { clicked: CalendarDay ->
                        selection = clicked
                    }
                },
                // The month body is only needed for ui test tag.
                monthBody = { _, content ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("MonthBody"),
                    ) {
                        content()
                    }
                },
                monthHeader = {
                    MonthHeader(daysOfWeek = daysOfWeek)
                },
                monthFooter = {
                    MonthFooter()
                }
            )
            Log.d("Calendar", "the selection $selection")
            if(selection != null) {
                Log.d("Calendar", "the selection IN THE IF $selection")
                ModalBottomSheet(onDismissRequest = { selection = null }) {
                    ListView(logs = logsInSelectedDate.value)
                }
            }
        }
    }
}

@Composable
private fun ListView (
 logs: List<BowelLog>
) {
    Log.d("Calendar", "in the list view")
    val context = LocalContext.current
    val navController = remember {
        val activity = context.findActivity() // Extension function to find activity from context
        Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main)
    }
    Box(
        Modifier
            .fillMaxWidth()
            .testTag("ListView")
            .background(colorResource(id = R.color.white))
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column (modifier = Modifier.padding(16.dp)) {
            if (logs.isEmpty()) {
                Log.d("Calendar", "no poops to display")
                Text("No poops to display")
            } else {
                for (i in logs.indices) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(logs[i].color.toColorLong()),
                                shape = RoundedCornerShape(8.dp)
                            ) // Rounded corners
                            .padding(16.dp) // Padding around the text inside the box
                            .clickable {
                                val poopId = logs[i].id
                                val bundle = Bundle().apply {
                                    putString("poopId", poopId)
                                }
                                navController.navigate(R.id.navigation_poop_view, bundle)
                            }
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val sdf = SimpleDateFormat("hh:mm a", Locale.CANADA)
                        Text(
                            text = "Log at ${sdf.format(logs[i].timeStarted)}",
                            textAlign = TextAlign.Center,
                            color = Color.White // Set text color that contrasts well with your background
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun FullScreenCalendar(
    modifier: Modifier,
    state: CalendarState,
    dayContent: @Composable BoxScope.(CalendarDay) -> Unit,
    monthHeader: @Composable ColumnScope.(CalendarMonth) -> Unit,
    monthBody: @Composable ColumnScope.(CalendarMonth, content: @Composable () -> Unit) -> Unit,
    monthFooter: @Composable ColumnScope.(CalendarMonth) -> Unit,
) {
    HorizontalCalendar(
        modifier = modifier,
        state = state,
        calendarScrollPaged = true,
        contentHeightMode = ContentHeightMode.Fill,
        dayContent = dayContent,
        monthBody = monthBody,
        monthHeader = monthHeader,
        monthFooter = monthFooter,
    )
}

@Composable
private fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        Modifier
            .fillMaxWidth()
            .testTag("MonthHeader")
            .background(Color.White)
            .padding(vertical = 8.dp),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                text = dayOfWeek.displayText(),
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MonthFooter() {
    Box(
        Modifier
            .fillMaxWidth()
            .testTag("MonthFooter")
            .background(Color.White)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {}
}

@Composable
private fun Day(
    day: CalendarDay,
    poopColor: Color,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: (CalendarDay) -> Unit,
) {
    val bgColor = when (day.position) {
        DayPosition.MonthDate ->
            if (isSelected) {
                colorResource(R.color.example_1_selection_color)
            } else if (isToday && poopColor == Color.Transparent) {
                colorResource(id = R.color.off_white)
            } else {
                poopColor
            }
        else -> Color.White
    }
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .border(
                width = 2.dp,
                color = Color.White,
                shape = RoundedCornerShape(13.dp, 13.dp, 13.dp, 13.dp)
            )
            .clip(RectangleShape)
            .clip(RoundedCornerShape(13.dp, 13.dp, 13.dp, 13.dp))
            .background(
                color = bgColor,
            )
            // Disable clicks on inDates/outDates
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = { onClick(day) },
            ),
        contentAlignment = Alignment.TopEnd,
    ) {
        val textColor = when (day.position) {
            // Color.Unspecified will use the default text color from the current theme
            DayPosition.MonthDate -> if (isSelected)
                textColorSelector(colorResource(R.color.example_1_selection_color)) else
                    textColorSelector(poopColor)
            DayPosition.InDate, DayPosition.OutDate -> Color.Gray
        }
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontSize = 18.sp,
            modifier = Modifier.padding(10.dp)
        )
    }
}
