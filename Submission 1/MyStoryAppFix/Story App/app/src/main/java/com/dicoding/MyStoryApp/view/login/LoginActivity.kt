package com.dicoding.MyStoryApp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.MyStoryApp.data.Result
import com.dicoding.MyStoryApp.data.pref.UserModel
import com.dicoding.MyStoryApp.databinding.ActivityLoginBinding
import com.dicoding.MyStoryApp.view.ViewModelFactory
import com.dicoding.MyStoryApp.view.main.MainActivity
import com.dicoding.MyStoryApp.view.signup.SignupActivity
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signIn()
        playAnimation()

        binding.textSignUp.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }

        supportActionBar?.hide()
    }

    private fun signIn() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            setUpAction(email, password)
        }
    }

    private fun setUpAction(email: String, password: String) {
        viewModel.login(email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                        Toast.makeText(this, "SignIn Success", Toast.LENGTH_SHORT).show()
                        val userData = UserModel(
                            result.data.loginResult.token,
                            result.data.loginResult.userId.toString(),
                            result.data.loginResult.name.toString(),
                            true
                        )
                        viewModel.saveUserSession(userData)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        exitProcess(0)
        super.onBackPressed()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val tvSignup = ObjectAnimator.ofFloat(binding.tvSignUp, View.ALPHA, 1f).setDuration(100)
        val textSignup = ObjectAnimator.ofFloat(binding.textSignUp, View.ALPHA, 1F).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(tvSignup, textSignup)
        }

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login,
                together
            )
            startDelay = 100
        }.start()
    }
}

