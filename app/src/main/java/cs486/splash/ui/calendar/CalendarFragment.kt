package cs486.splash.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
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
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import cs486.splash.viewmodels.BowelLogViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bowelLogViewModel =
            ViewModelProvider(this).get(BowelLogViewModel::class.java)

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.calendar.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CalendarPage(bowelLogViewModel)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
@OptIn(ExperimentalLayoutApi::class)
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
            else bowelLogViewModel.getBowelLogsOnDate(
                Date.from(selection?.date?.atStartOfDay(ZoneId.systemDefault())?.toInstant())).orEmpty()
        }
    }
    StatusBarColorUpdateEffect(color = colorResource(id = R.color.example_1_bg_light))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.example_1_bg_light))
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
        // Draw light content on dark background.
        CompositionLocalProvider(LocalContentColor provides darkColorScheme().onSurface) {
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
                    .background(colorResource(id = R.color.example_1_bg))
                    .testTag("Calendar"),
                state = state,
                dayContent = { day ->
                    Day(
                        day = day,
                        color = when {
                            logsInSelectedDate.value.isNotEmpty() ->
                                Color(logsInSelectedDate.value[0].color.toColorInt())
                            else -> null
                        },
                        isSelected = selection == day,
                        isToday = day.position == DayPosition.MonthDate && day.date == today,
                    ) { clicked ->
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
                },
            )
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
            .background(colorResource(id = R.color.example_1_bg_secondary))
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
            .background(colorResource(id = R.color.example_1_bg_secondary))
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {}
}

@Composable
private fun Day(
    day: CalendarDay,
    color: Color?,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: (CalendarDay) -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(RectangleShape)
            .background(
                color = when {
                    isSelected -> colorResource(R.color.example_1_selection_color)
                    isToday -> colorResource(id = R.color.white_light)
                    color != null -> color
                    else -> Color.Transparent
                },
            )
            // Disable clicks on inDates/outDates
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = { onClick(day) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        val textColor = when (day.position) {
            // Color.Unspecified will use the default text color from the current theme
            DayPosition.MonthDate -> if (isSelected) colorResource(R.color.example_1_bg) else Color.White
            DayPosition.InDate, DayPosition.OutDate -> colorResource(R.color.white_light)
        }
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontSize = 15.sp,
        )
    }
}
