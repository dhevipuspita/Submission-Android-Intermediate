package com.dicoding.MyStoryApp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.MyStoryApp.data.UserRepository
import com.dicoding.MyStoryApp.data.pref.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun getStories() = userRepository.getStories()

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

}