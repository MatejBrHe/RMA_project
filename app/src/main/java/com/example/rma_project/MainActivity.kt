package com.example.rma_project

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rma_project.ui.theme.RMA_projectTheme
import com.example.rma_project.view.AddScreen
import com.example.rma_project.view.InfoScreen
import com.example.rma_project.view.MainScreen
import com.example.rma_project.view.LoginScreen
import com.example.rma_project.view.ProfileScreen
import com.example.rma_project.view.SignUpScreen
import com.google.firebase.auth.auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.example.rma_project.viewModel.MeetingViewModel
import com.example.rma_project.viewModel.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public override fun onStart() {
        super.onStart()
        var startDestination : String
        if (auth.currentUser != null) {
            startDestination = "home"
        }
        else {
            startDestination = "login"
        }
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val meetingViewModel: MeetingViewModel = viewModel()
            val loginViewModel: LoginViewModel = viewModel()

            RMA_projectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = navController, startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginScreen(auth = auth, navController = navController, viewModel = loginViewModel)
                        }
                        composable("signUp") {
                            SignUpScreen(auth = auth, navController = navController, viewModel = loginViewModel)
                        }
                        composable("home") {
                            MainScreen(screenTitle = "Homepage", navController = navController, viewModel = meetingViewModel)
                        }
                        composable("myMeetings") {
                            MainScreen(screenTitle = "My coffee meetings", navController = navController, viewModel = meetingViewModel)
                        }
                        composable("joinedMeetings") {
                            MainScreen(screenTitle = "Joined meetings", navController = navController, viewModel = meetingViewModel)
                        }
                        composable("addNew") {
                            AddScreen(screenTitle = "Add meeting", navController = navController, viewModel = meetingViewModel)
                        }
                        composable("profile") {
                            ProfileScreen(auth = auth, navController = navController, viewModel = loginViewModel)
                        }
                        composable("info/{meetingId}") {backStackEntry ->

                            val meetingId = backStackEntry.arguments
                                ?.getString("meetingId")
                                ?.toIntOrNull()

                            if (meetingId != null) {
                                InfoScreen(
                                    navController = navController,
                                    meetingId = meetingId,
                                    onJoinClick = {
                                        println("Join clicked!")
                                    },
                                    viewModel = meetingViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}