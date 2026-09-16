package com.example.rma_project

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import com.example.rma_project.view.SplashScreen


class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    private fun requestPermission() {
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS ) !=
                PackageManager.PERMISSION_GRANTED ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        enableEdgeToEdge()
        setContent {
            RMA_projectTheme {
                requestPermission()
                RMAApp(auth = auth)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RMAApp(auth: FirebaseAuth) {
    var showSplash by rememberSaveable { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(
            onFinished = {
                showSplash = false
            }
        )
    } else {
        AppNavigation(auth = auth)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    auth: FirebaseAuth
) {
    val applicationContext = LocalContext.current.applicationContext
    val navController = rememberNavController()
    val meetingViewModel: MeetingViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                if (modelClass.isAssignableFrom(MeetingViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MeetingViewModel(applicationContext) as T
                }

                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )
    val loginViewModel: LoginViewModel = viewModel()
    val startDestination =
        if (auth.currentUser != null) {
            "home"
        } else {
            "login"
        }
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(
                innerPadding
            )
        ) {
            composable("login") {
                LoginScreen(
                    auth = auth,
                    navController = navController,
                    viewModel = loginViewModel
                )
            }
            composable("signUp") {
                SignUpScreen(
                    auth = auth,
                    navController = navController,
                    viewModel = loginViewModel
                )
            }
            composable("home") {
                MainScreen(
                    screenTitle = "Homepage",
                    navController = navController,
                    viewModel = meetingViewModel,
                    auth = auth
                )
            }
            composable("myMeetings") {
                MainScreen(
                    screenTitle = "My meetings",
                    navController = navController,
                    viewModel = meetingViewModel,
                    auth = auth
                )
            }
            composable("joinedMeetings") {
                MainScreen(
                    screenTitle = "Joined meetings",
                    navController = navController,
                    viewModel = meetingViewModel,
                    auth = auth
                )
            }
            composable("addNew") {
                AddScreen(
                    screenTitle = "Add meeting",
                    navController = navController,
                    viewModel = meetingViewModel,
                    auth = auth
                )
            }
            composable("profile") {
                ProfileScreen(
                    auth = auth,
                    navController = navController,
                    viewModel = loginViewModel
                )
            }
            composable(
                "info/{meetingId}"
            ) { backStackEntry ->
                val meetingId =
                    backStackEntry.arguments
                        ?.getString("meetingId")
                if (meetingId != null) {
                    InfoScreen(
                        navController = navController,
                        meetingId = meetingId,
                        viewModel = meetingViewModel,
                        auth = auth
                    )
                }
            }
        }
    }
}