package com.example.rma_project.view

import android.os.Build
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
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
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale
import androidx.compose.material3.DatePicker
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import java.time.Instant
import java.time.YearMonth
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    screenTitle: String,
    navController: NavController,
    viewModel: MeetingViewModel = viewModel()
) {
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
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                Text("Menu", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                if (screenTitle != "Add meeting") {
                    NavigationDrawerItem(
                        label = { Text(text = "Add meeting") },
                        selected = false,
                        onClick = { navController.navigate("addNew") }
                    )
                }
                if (screenTitle != "Homepage") {
                    NavigationDrawerItem(
                        label = { Text(text = "Homepage") },
                        selected = false,
                        onClick = { navController.navigate("home") }
                    )
                }
                if (screenTitle != "My coffee meetings") {
                    NavigationDrawerItem(
                        label = { Text(text = "My coffee meetings") },
                        selected = false,
                        onClick = { navController.navigate("myMeetings") }
                    )
                }
                if (screenTitle != "Joined meetings") {
                    NavigationDrawerItem(
                        label = { Text(text = "Joined meetings") },
                        selected = false,
                        onClick = { navController.navigate("joinedMeetings") }
                    )
                }
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Topbar(title = screenTitle, scope = scope, drawerState = drawerState, onClick = { navController.navigate("profile") })

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

            Button(
                onClick = {
                    viewModel.addMeeting(
                        title = title,
                        time = selectedDateTime,
                        description = description
                    )

                    navController.navigate("home")
                },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp)
            ) {
                Text("Add meeting")
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
            Text("Title")
        },
        placeholder = {
            Text("e.g. Coffee with Alex")
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun MeetingDateTimeInput(
    dateTime: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
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
                    text = "Meeting date & time",
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
            Text("Description")
        },
        placeholder = {
            Text("What would you like to discuss?")
        },
        minLines = 5,
        maxLines = 8,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun NumberSpinner(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.height(100.dp),
        factory = { context ->
            NumberPicker(context).apply {

                minValue = range.first
                maxValue = range.last
                this.value = value

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

@Composable
fun MonthSpinner(
    value: Int,
    months: List<String>,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.height(100.dp),
        factory = { context ->
            NumberPicker(context).apply {

                minValue = 1
                maxValue = 12
                displayedValues = months.toTypedArray()
                this.value = value

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
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
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
            modifier = Modifier.width(70.dp)
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
            modifier = Modifier.width(130.dp)
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
            modifier = Modifier.width(80.dp)
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
                    text = "Select meeting date & time",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                TabRow(
                    selectedTabIndex = selectedTab
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text("Date")
                        }
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text("Time")
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
                        Text("Cancel")
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
                        Text("OK")
                    }
                }
            }
        }
    }
}
