package com.dicoding.MyStoryApp.view.maps

import androidx.lifecycle.ViewModel
import com.dicoding.MyStoryApp.data.UserRepository

class MapsViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getStoryLocation() = userRepository.getStoriesLocation()

}