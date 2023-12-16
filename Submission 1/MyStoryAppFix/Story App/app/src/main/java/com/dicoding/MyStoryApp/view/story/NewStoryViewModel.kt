package com.dicoding.MyStoryApp.view.story

import androidx.lifecycle.ViewModel
import com.dicoding.MyStoryApp.data.UserRepository
import java.io.File

class NewStoryViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun uploadImage(imageFile: File, description: String) = userRepository.uploadImage(imageFile, description)
}