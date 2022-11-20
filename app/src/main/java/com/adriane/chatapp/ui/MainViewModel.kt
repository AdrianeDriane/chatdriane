package com.adriane.chatapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.adriane.chatapp.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private var auth: FirebaseAuth = Firebase.auth
    private val _state = MutableStateFlow(
        MainState(
            isLoggedIn = null,
            error = null,
            chatList = emptyList()
        )
    )

    private val db = Firebase.firestore

    val state = _state.asStateFlow()

    init {
        checkAuth()
    }

    fun checkAuth() {
        if (auth.currentUser == null) {
            _state.value = _state.value.copy(
                isLoggedIn = false
            )
        } else {
            _state.value = _state.value.copy(
                isLoggedIn = true
            )
            observeChat()
        }
    }

    fun logIn(username: String, password: String) {
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    _state.value = _state.value.copy(
                        isLoggedIn = true
                    )
                    observeChat()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidUserException) {
                        signUp(username, password)
                    } else {
                        _state.value = _state.value.copy(error = task.exception)
                    }
                }
            }
    }

    private fun signUp(username: String, password: String) {
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    _state.value = _state.value.copy(isLoggedIn = true)
                    observeChat()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    _state.value = _state.value.copy(error = task.exception)
                }
            }
    }

    fun sendMessage(message: String) {
        val chat = hashMapOf(
            "username" to auth.currentUser?.email,
            "message" to message,
            "created_at" to System.currentTimeMillis(),
        )
        db.collection("chats").add(chat)
            .addOnSuccessListener {
                Log.d(TAG, "sendMessage: ")
            }
            .addOnFailureListener {
                Log.w(TAG, "sendMessage: ", it)
            }
    }

    private fun observeChat() {
        db.collection("chats")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                value?.let {
                    val chatListFromFireStore = mutableListOf<Chat>()

                    for (doc in value) {
                        chatListFromFireStore.add(
                            Chat(
                                username = doc.getString("username")!!,
                                message = doc.getString("message")!!,
                                isMine = doc.getString("username") == auth.currentUser!!.email
                            )
                        )
                    }

                    _state.value = _state.value.copy(
                        chatList = chatListFromFireStore
                    )
                }
            }
    }
}