package com.example.rma_project.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
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

    fun signUpUser(auth: FirebaseAuth, email: String, username: String, password: String, navController: NavController) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val profileUpdate = UserProfileChangeRequest.Builder().setDisplayName(username).build()
                    auth.currentUser?.updateProfile(profileUpdate)
                    Log.d(TAG, "createUserWithEmail:success")
                    _user.value = auth.currentUser

                    val newUser = hashMapOf(
                        "name" to username
                    )

                    db.collection("Users")
                        .document(auth.currentUser?.uid.toString())
                        .set(newUser)
                        .addOnFailureListener {exception ->
                            Log.e("FIRESTORE", exception.message, exception)
                        }

                    navController.navigate("home")
                } else {
                    Log.e(TAG, "createUserWithEmail:failure", task.exception)
                    _user.value = null
                    navController.navigate("signUp")
                }
            }
    }
}