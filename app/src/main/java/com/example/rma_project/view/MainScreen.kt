package com.example.rma_project.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rma_project.R
import com.example.rma_project.model.Meeting
import com.example.rma_project.model.MockMeetings
import com.example.rma_project.viewModel.MeetingViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    screenTitle: String,
    navController: NavController,
    viewModel: MeetingViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val meetings by viewModel.meetings.collectAsState()

    /*LaunchedEffect(Unit) {
        viewModel.meetings.collectAsState()
    }*/

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                Text("Menu", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Add meeting") },
                    selected = false,
                    onClick = { navController.navigate("addNew") }
                )
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
            LazyColumn(
                modifier = Modifier
                    .padding(vertical = 10.dp)
            ) {
                items(meetings){ meeting ->
                    Card(
                        meeting = meeting,
                        onClick = {
                            navController.navigate("info/${meeting.id}")
                        }
                    )
                }
            }
        }

    }
}

@Composable
fun Topbar(
    title: String,
    scope: CoroutineScope,
    drawerState: DrawerState,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(5.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(50.dp))
        ) {
            drawRect(
                color = Color.LightGray
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                onClick = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "User image",
                    modifier = Modifier
                        .height(40.dp)
                        .width(40.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 0.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                onClick = onClick
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "User image",
                    modifier = Modifier
                        .height(40.dp)
                        .width(40.dp)
                )
            }
        }
    }
}

@Composable
fun Card(
    meeting: Meeting,
    onClick: () -> Unit
) {
    Button(
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .padding(all = 15.dp)
                .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(10.dp))
                .clip(shape = RoundedCornerShape(10.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = "User image",
                modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .padding(5.dp)
            )
            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Text(
                    text = meeting.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(text = meeting.owner.name)
            }
            Image(
                painter = painterResource(id = R.drawable.coffee),
                contentDescription = "User image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(150.dp)
            )
        }
    }
}
