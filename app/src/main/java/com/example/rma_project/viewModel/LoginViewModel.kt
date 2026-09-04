package com.example.rma_project.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {
    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user

    fun loginUser(auth: FirebaseAuth, email: String, password: String, navController: NavController) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    _user.value = auth.currentUser
                    navController.navigate("home")
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    _user.value = null
                    navController.navigate("login")
                }
            }
    }

    fun logoutUser(auth: FirebaseAuth) {
        auth.signOut()
    }

    fun signUpUser(auth: FirebaseAuth, email: String, password: String, navController: NavController) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    _user.value = auth.currentUser
                    navController.navigate("home")
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    _user.value = null
                    navController.navigate("signUp")
                }
            }
    }
}