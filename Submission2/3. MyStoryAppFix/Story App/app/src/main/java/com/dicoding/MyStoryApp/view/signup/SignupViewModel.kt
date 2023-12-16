package com.dicoding.MyStoryApp.view.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.MyStoryApp.data.UserRepository
import kotlinx.coroutines.launch

class SignupViewModel(private val userRepository: UserRepository): ViewModel() {
    fun register(username: String, email: String, password: String) = userRepository.register(username, email, password)

    fun getSession() {
        viewModelScope.launch {
            userRepository.getSession()
        }
    }
}