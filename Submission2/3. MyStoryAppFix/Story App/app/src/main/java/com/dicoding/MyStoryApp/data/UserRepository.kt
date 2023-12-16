package com.dicoding.MyStoryApp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.MyStoryApp.data.network.ApiConfig
import com.dicoding.MyStoryApp.data.network.ApiService
import com.dicoding.MyStoryApp.data.pref.UserModel
import com.dicoding.MyStoryApp.data.pref.UserPreference
import com.dicoding.MyStoryApp.data.response.AddNewStoryResponse
import com.dicoding.MyStoryApp.data.response.ListStoryItem
import com.dicoding.MyStoryApp.data.response.LoginResponse
import com.dicoding.MyStoryApp.data.response.RegisterResponse
import com.dicoding.MyStoryApp.data.response.StoryResponse
import com.dicoding.MyStoryApp.data.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun register(username: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            //get success message
            val messageRegister = apiService.register(username, email, password)
            if (messageRegister.error == false) {
                emit(Result.Success(messageRegister))
            } else {
                emit(Result.Error(messageRegister.message ?: "An error occurred"))
            }
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
            val errorMessage = errorBody?.message ?: "An error occurred"
            emit(Result.Error("Registration failed: ${errorMessage}"))
        }catch (e: Exception){
            emit(Result.Error("Internet Issues"))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val messageLogin = apiService.login(email, password)
                if (messageLogin.error == false){
                    val token = UserModel(
                        token = messageLogin.loginResult?.token ?: "",
                        userId = messageLogin.loginResult?.userId ?: "",
                        name = messageLogin.loginResult?.name ?: "",
                        isLogin = true
                    )
                    ApiConfig.token = messageLogin.loginResult?.token ?: ""
                    userPreference.saveSession(token)
                    emit(Result.Success(messageLogin))
                } else{
                    emit(Result.Error(messageLogin.message ?: "Error"))
                }
            } catch (e : HttpException){
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
                val errorMessage = errorBody?.message ?: "An error occurred"
                emit(Result.Error("Login failed: $errorMessage"))
            }catch (e: Exception){
                emit(Result.Error("Internet Issues"))
            }
        }

    fun getStories() : LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = ApiConfig.getApiService(userPreference.getSession().first().token)

            val messageStory = response.getStories()
            val story = messageStory.listStory
            val storyList = story.map { data ->
                ListStoryItem(
                    data?.photoUrl,
                    data?.createdAt,
                    data?.name,
                    data?.description,
                    data?.lon,
                    data?.id,
                    data?.lat
                )
            }
            if (messageStory.error == false) {
                emit(Result.Success(storyList))
            } else {
                emit(Result.Error(messageStory.message ?: "Error"))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, StoryResponse::class.java)
            val errorMessage = errorBody?.message ?: "An error occurred"
            emit(Result.Error("Load failed: $errorMessage"))
        } catch (e: Exception){
            emit(Result.Error("Internet Issues"))
        }
    }

    fun uploadImage(imageFile: File, description: String) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.upStory(multipartBody, requestBody)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, AddNewStoryResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }

    fun getStoriesLocation(): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val getToken = userPreference.getSession().first()
            val apiService = ApiConfig.getApiService(getToken.token)
            val locationStoryResponse = apiService.getStoriesLocation()
            emit(Result.Success(locationStoryResponse))
        }  catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.removeTokenAuth()
    }

    companion object {
        private var instance: UserRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): UserRepository {
            if (instance == null) {
                instance = UserRepository(apiService,userPreference)
            }
            return instance!!
        }
    }
}