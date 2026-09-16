package com.example.rma_project.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rma_project.R
import com.example.rma_project.model.User
import com.example.rma_project.viewModel.MeetingViewModel
import com.google.firebase.auth.FirebaseAuth
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InfoScreen(
    navController: NavController,
    meetingId: String,
    viewModel: MeetingViewModel = viewModel(),
    auth: FirebaseAuth
) {
    if (auth.currentUser == null) {
        navController.navigate("login")
    }

    val currentUser = auth.currentUser?.let { User(id = it.uid, name = auth.currentUser?.displayName.toString()) }
    val meeting by viewModel.meeting.collectAsState()

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(meetingId) {
        viewModel.getMeeting(meetingId) {
            isLoading = false
        }
    }

    if (meeting == null) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if(isLoading){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }
    else {
        Scaffold(
            topBar = {
                Banner(picture = meeting!!.imageId, title = meeting!!.title)
            },
            bottomBar = {
                if (currentUser?.id == meeting!!.owner.id) {
                    Button(
                        onClick = {
                            if (currentUser.id == meeting!!.owner.id) {
                                viewModel.deleteMeeting(meeting!!, currentUser.id)
                            }
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    ) {
                        Text(text = stringResource(R.string.delete), color = Color.White)
                    }
                } else if (meeting!!.participants.contains(currentUser)) {
                    Button(
                        onClick = {
                            viewModel.leaveMeeting(currentUser!!, meeting!!)
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    ) {
                        Text(text = stringResource(R.string.leave), color = Color.White)
                    }
                } else if (meeting!!.participants.count() >= 4) {
                    Text(text = stringResource(R.string.meeting_is_full), color = MaterialTheme.colorScheme.primary)
                } else {
                    Button(
                        onClick = {
                            viewModel.joinMeeting(currentUser!!, meeting!!)
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    ) {
                        Text(text = stringResource(R.string.join), color = Color.White)
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Description(meeting!!.description)
                }

                item {
                    Time(meeting!!.time.format(
                            DateTimeFormatter.ofPattern(
                                "dd MMM yyyy, HH:mm",
                                Locale.getDefault()
                            )
                        )
                    )
                }

                item {
                    Text(
                        text = "${stringResource(R.string.joined)} (${meeting!!.participants.size + 1})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 8.dp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Row() {
                        Owner(meeting!!.owner)
                    }
                }

                items(meeting!!.participants) { user ->
                    Participant(user)
                }
            }
        }
    }
}

@Composable
fun Banner(picture: Int, title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        Image(
            painter = painterResource(picture),
            contentDescription = "Coffee meeting",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        )
    }
}

@Composable
fun Description(description: String) {
    Text(
        text = description,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(
            start = 20.dp,
            end = 20.dp,
            top = 20.dp
        )
    )
}

@Composable
fun Time(time: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            horizontal = 20.dp,
            vertical = 20.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = stringResource(R.string.`when`),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = time,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun Participant(user: User) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 8.dp
            )
    ) {
        ProfilePictureSmall(null)

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = user.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun Owner(user: User) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 8.dp
            )
    ) {
        ProfilePictureSmall(null)

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "${user.name} (${stringResource(R.string.owner)})",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}