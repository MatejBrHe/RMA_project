package com.example.rma_project.view

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rma_project.R
import com.example.rma_project.model.Meeting
import com.example.rma_project.viewModel.MeetingViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
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
    val meetings by viewModel.meetings.collectAsState()

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (screenTitle == "Homepage") {
            viewModel.getAllMeetings() {
                isLoading = false
            }
        }
        if (screenTitle == "My meetings") {
            viewModel.getMyMeetings(auth.currentUser!!.uid) {
                isLoading = false
            }
        }
        if (screenTitle == "Joined meetings") {
            viewModel.getJoinedMeetings(auth.currentUser!!.uid) {
                isLoading = false
            }
        }
    }

    ModalNavigationDrawer(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                Text("Menu", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.primary)
                HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                NavigationDrawerItem(
                    label = { Text(text = stringResource(R.string.add_meeting), color = MaterialTheme.colorScheme.primary) },
                    selected = false,
                    onClick = {
                        navController.navigate("addNew")
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
                if (screenTitle != "Homepage") {
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
                }
                if (screenTitle != "My meetings") {
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
                }
                if (screenTitle != "Joined meetings") {
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
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            Topbar(title = screenTitle, scope = scope, drawerState = drawerState, onClick = { navController.navigate("profile") })
            if(isLoading) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }
            else {
                if (!meetings.isEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(vertical = 10.dp),
                        contentPadding = PaddingValues(bottom = 5.dp)
                    ) {
                        items(meetings) { meeting ->
                            Card(
                                meeting = meeting,
                                onClick = {
                                    navController.navigate("info/${meeting.id}")
                                }
                            )
                        }
                    }
                }
                else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = stringResource(R.string.no_meetings),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
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
        var topbarTitle = ""
        if (title == "Homepage") {
            topbarTitle = stringResource(R.string.homepage)
        }
        if (title == "My meetings") {
            topbarTitle = stringResource(R.string.my_meetings)
        }
        if (title == "Joined meetings") {
            topbarTitle = stringResource(R.string.joined_meetings)
        }
        if (title == "Add meeting") {
            topbarTitle = stringResource(R.string.add_meeting)
        }
        val color = MaterialTheme.colorScheme.surface
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(50.dp))
        ) {
            drawRect(
                color = color
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
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .height(40.dp)
                        .width(40.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = topbarTitle,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 0.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                onClick = onClick
            ) {
                ProfilePictureSmall(null)
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
                .padding(all = 5.dp)
                .border(width = 2.dp, color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(60.dp))
                .clip(shape = RoundedCornerShape(60.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            ProfilePictureBig(null)
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .width(125.dp)
            ) {
                Text(
                    text = meeting.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text = meeting.owner.name, color = MaterialTheme.colorScheme.primary)
            }
            Image(
                painter = painterResource(id = meeting.imageId),
                contentDescription = "User image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(150.dp)
            )
        }
    }
}

@Composable
fun ProfilePictureSmall(url: Uri?){
    if (url == null) {
        Box() {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User image",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .height(40.dp)
                    .width(40.dp)
            )
        }
    } else {
        AsyncImage(
            model = url,
            contentDescription = "User image",
            modifier = Modifier
                .height(40.dp)
                .width(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ProfilePictureBig(url: Uri?){
    if (url == null) {
        Box() {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User image",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(10.dp)
                    .height(60.dp)
                    .width(60.dp)
            )
        }
    } else {
        AsyncImage(
            model = url,
            contentDescription = "User image",
            modifier = Modifier
                .padding(10.dp)
                .height(60.dp)
                .width(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
