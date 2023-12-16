package com.dicoding.MyStoryApp.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.MyStoryApp.data.pref.UserModel
import com.dicoding.MyStoryApp.data.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun login(email: String, password: String) = userRepository.login(email, password)

    fun saveUserSession(authToken: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(authToken)
        }
    }

}
