package com.example.rma_project.view

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rma_project.R
import com.example.rma_project.viewModel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(auth: FirebaseAuth, navController: NavController, viewModel: LoginViewModel = viewModel()) {
    if (auth.currentUser == null) {
        navController.navigate("login")
    }

    val user = auth.currentUser

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    viewModel.logoutUser(auth = auth)
                    navController.navigate("login")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
            ) {
                Text(text = stringResource(R.string.logout), color = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            ProfilePicture(user?.photoUrl)
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier.padding(start = 30.dp)
            ) {
                Text(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary, text = "${stringResource(R.string.email)}: ")
                user?.email?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            }
            Row(
                modifier = Modifier.padding(start = 30.dp)
            ) {
                Text(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary, text = "${stringResource(R.string.username)}: ")
                user?.displayName?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            }
            Spacer(modifier = Modifier.weight(1f))
        }

    }
}

@Composable
fun ProfilePicture(url: Uri?){
    Row() {
        Spacer(modifier = Modifier.weight(1f))
        if (url == null) {
            Box() {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User image",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp)
                )
            }
        } else {
            AsyncImage(
                model = url,
                contentDescription = "User image",
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}