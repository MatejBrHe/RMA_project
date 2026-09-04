package com.example.rma_project.view

import android.content.res.Resources
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
    val user = auth.currentUser

    Column() {
        Spacer(modifier = Modifier.height(40.dp))
        ProfilePicture(user?.photoUrl)
        Spacer(modifier = Modifier.height(40.dp))
        Row(
            modifier = Modifier.padding(start = 30.dp)
        ) {
            Text(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, text = "E-mail: ")
            user?.email?.let { Text(it) }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row() {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier.width(150.dp),
                onClick = {
                    viewModel.logoutUser(auth = auth)
                    navController.navigate("login")
                }
            ) {
                Text(text = "Logout")
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ProfilePicture(url: Uri?){
    Row() {
        Spacer(modifier = Modifier.weight(1f))
        if (url == null) {
            Box() {
                Canvas(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(shape = RoundedCornerShape(100.dp))
                ) {
                    drawRect(
                        color = Color.LightGray
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "User image",
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
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}