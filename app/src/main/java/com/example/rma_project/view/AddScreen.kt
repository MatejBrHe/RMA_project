package com.example.rma_project.view

import android.os.Build
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rma_project.viewModel.MeetingViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.rma_project.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    screenTitle: String,
    navController: NavController,
    viewModel: MeetingViewModel = viewModel(),
    auth: FirebaseAuth
) {
    if (auth.currentUser == null) {
        navController.navigate("login")
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    val now = LocalDateTime.now()

    var selectedYear by rememberSaveable {
        mutableIntStateOf(now.year)
    }
    var selectedMonth by rememberSaveable {
        mutableIntStateOf(now.monthValue)
    }
    var selectedDay by rememberSaveable {
        mutableIntStateOf(now.dayOfMonth)
    }
    var selectedHour by rememberSaveable {
        mutableIntStateOf(now.hour)
    }
    var selectedMinute by rememberSaveable {
        mutableIntStateOf(now.minute)
    }

    var showDateTimePicker by rememberSaveable {
        mutableStateOf(false)
    }

    val selectedDateTime = LocalDateTime.of(
        selectedYear,
        selectedMonth,
        selectedDay,
        selectedHour,
        selectedMinute
    )

    val formattedDateTime = selectedDateTime.format(
        DateTimeFormatter.ofPattern(
            "dd MMM yyyy, HH:mm",
            Locale.getDefault()
        )
    )

    ModalNavigationDrawer(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                Text(stringResource(R.string.menu), modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.primary)
                HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                NavigationDrawerItem(
                    label = { Text(text = stringResource(R.string.homepage), color = MaterialTheme.colorScheme.primary) },
                    selected = false,
                    onClick = {
                        navController.navigate("home")
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = stringResource(R.string.my_meetings), color = MaterialTheme.colorScheme.primary) },
                    selected = false,
                    onClick = {
                        navController.navigate("myMeetings")
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = stringResource(R.string.joined_meetings), color = MaterialTheme.colorScheme.primary) },
                    selected = false,
                    onClick = {
                        navController.navigate("joinedMeetings")
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Topbar(
                    title = screenTitle,
                    scope = scope,
                    drawerState = drawerState,
                    onClick = { navController.navigate("profile") }
                )
            },
            bottomBar = {
                Button(
                    onClick = {
                        viewModel.addMeeting(
                            title = title,
                            time = selectedDateTime,
                            description = description,
                            auth = auth
                        )
                        navController.navigate("myMeetings")
                    },
                    enabled = title.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            disabledContentColor = MaterialTheme.colorScheme.surface
                        ),
                ) {
                    Text(stringResource(R.string.add_meeting), color = Color.White)
                }
            }
        ) { paddingValues ->
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                MeetingTitleInput(
                    title = title,
                    onTitleChange = {
                        title = it
                    }
                )

                MeetingDateTimeInput(
                    dateTime = formattedDateTime,
                    onClick = {
                        showDateTimePicker = true
                    }
                )

                MeetingDescriptionInput(
                    description = description,
                    onDescriptionChange = {
                        description = it
                    }
                )
            }
        }
    }

    if (showDateTimePicker) {
        MeetingDateTimePicker(
            initialDateTime = selectedDateTime,
            onDateTimeSelected = { dateTime ->
                selectedYear = dateTime.year
                selectedMonth = dateTime.monthValue
                selectedDay = dateTime.dayOfMonth
                selectedHour = dateTime.hour
                selectedMinute = dateTime.minute

                showDateTimePicker = false
            },
            onDismiss = {
                showDateTimePicker = false
            }
        )
    }

}

@Composable
fun MeetingTitleInput(
    title: String,
    onTitleChange: (String) -> Unit
) {
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        label = {
            Text(stringResource(R.string.title), color = MaterialTheme.colorScheme.secondary)
        },
        placeholder = {
            Text(stringResource(R.string.example_title), color = MaterialTheme.colorScheme.secondary)
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun MeetingDateTimeInput(
    dateTime: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.meeting_date_time),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = dateTime,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Change date and time"
            )
        }
    }
}


@Composable
fun MeetingDescriptionInput(
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        label = {
            Text(stringResource(R.string.description), color = MaterialTheme.colorScheme.secondary)
        },
        placeholder = {
            Text(stringResource(R.string.example_description), color = MaterialTheme.colorScheme.secondary)
        },
        minLines = 5,
        maxLines = 8,
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun NumberSpinner(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White
) {
    AndroidView(
        modifier = modifier.height(100.dp),
        factory = { context ->
            NumberPicker(context).apply {

                minValue = range.first
                maxValue = range.last
                this.value = value

                setTextColor(textColor.toArgb())

                setOnValueChangedListener { _, _, newValue ->
                    onValueChange(newValue)
                }
            }
        },
        update = { picker ->
            picker.minValue = range.first
            picker.maxValue = range.last

            if (picker.value != value) {
                picker.value = value
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MonthSpinner(
    value: Int,
    months: List<String>,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White
) {
    AndroidView(
        modifier = modifier.height(100.dp),
        factory = { context ->
            NumberPicker(context).apply {

                minValue = 1
                maxValue = 12
                displayedValues = months.toTypedArray()
                this.value = value

                setTextColor(textColor.toArgb())

                setOnValueChangedListener { _, _, newValue ->
                    onValueChange(newValue)
                }
            }
        },
        update = { picker ->

            picker.displayedValues = null
            picker.minValue = 1
            picker.maxValue = 12
            picker.displayedValues = months.toTypedArray()

            if (picker.value != value) {
                picker.value = value
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SpinnerDatePicker(
    year: Int,
    month: Int,
    day: Int,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit
) {
    val months = listOf(
        stringResource(R.string.january),
        stringResource(R.string.february),
        stringResource(R.string.march),
        stringResource(R.string.april),
        stringResource(R.string.may),
        stringResource(R.string.june),
        stringResource(R.string.july),
        stringResource(R.string.august),
        stringResource(R.string.september),
        stringResource(R.string.october),
        stringResource(R.string.november),
        stringResource(R.string.december)
    )

    val daysInMonth = YearMonth
        .of(year, month)
        .lengthOfMonth()

    val currentYear = LocalDate.now().year

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NumberSpinner(
            value = day,
            range = 1..daysInMonth,
            onValueChange = { newDay ->
                onDateSelected(year, month, newDay)
            },
            modifier = Modifier.width(70.dp),
            textColor = MaterialTheme.colorScheme.primary
        )
        MonthSpinner(
            value = month,
            months = months,
            onValueChange = { newMonth ->
                val newMaxDay = YearMonth
                    .of(year, newMonth)
                    .lengthOfMonth()

                val newDay = minOf(day, newMaxDay)

                onDateSelected(
                    year,
                    newMonth,
                    newDay
                )
            },
            modifier = Modifier.width(130.dp),
            textColor = MaterialTheme.colorScheme.primary
        )
        NumberSpinner(
            value = year,
            range = currentYear..(currentYear + 10),
            onValueChange = { newYear ->
                val newMaxDay = YearMonth
                    .of(newYear, month)
                    .lengthOfMonth()
                val newDay = minOf(day, newMaxDay)
                onDateSelected(
                    newYear,
                    month,
                    newDay
                )
            },
            modifier = Modifier.width(80.dp),
            textColor = MaterialTheme.colorScheme.primary
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingDateTimePicker(
    initialDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedYear by rememberSaveable {
        mutableIntStateOf(initialDateTime.year)
    }

    var selectedMonth by rememberSaveable {
        mutableIntStateOf(initialDateTime.monthValue)
    }

    var selectedDay by rememberSaveable {
        mutableIntStateOf(initialDateTime.dayOfMonth)
    }

    val timePickerState = rememberTimePickerState(
        initialHour = initialDateTime.hour,
        initialMinute = initialDateTime.minute,
        is24Hour = true
    )

    var selectedTab by rememberSaveable {
        mutableIntStateOf(0)
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = stringResource(R.string.select_meeting_date_time),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                TabRow(
                    selectedTabIndex = selectedTab
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(stringResource(R.string.date), color = MaterialTheme.colorScheme.primary)
                        }
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(stringResource(R.string.time), color = MaterialTheme.colorScheme.primary)
                        }
                    )
                }

                if (selectedTab == 0) {

                    SpinnerDatePicker(
                        year = selectedYear,
                        month = selectedMonth,
                        day = selectedDay,
                        onDateSelected = { year, month, day ->
                            selectedYear = year
                            selectedMonth = month
                            selectedDay = day
                        }
                    )

                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TimeInput(
                            state = timePickerState
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.primary)
                    }

                    TextButton(
                        onClick = {
                            val selectedDateTime = LocalDateTime.of(
                                selectedYear,
                                selectedMonth,
                                selectedDay,
                                timePickerState.hour,
                                timePickerState.minute
                            )
                            onDateTimeSelected(selectedDateTime)
                        }
                    ) {
                        Text(stringResource(R.string.ok), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
