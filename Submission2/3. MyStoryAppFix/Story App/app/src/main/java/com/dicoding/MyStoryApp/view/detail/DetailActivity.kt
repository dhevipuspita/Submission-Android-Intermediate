package com.dicoding.MyStoryApp.view.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.dicoding.MyStoryApp.databinding.ActivityDetailBinding
import com.dicoding.MyStoryApp.view.DateFormatter
import java.util.TimeZone

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //back_btn

        val title = intent.getStringExtra(EXTRA_TITLE)
        val pict = intent.getStringExtra(EXTRA_PICT)
        val desc = intent.getStringExtra(EXTRA_DESC)
        val date = intent.getStringExtra(EXTRA_DATE)

        binding.tvName.text = title
        Glide.with(this).load(pict).skipMemoryCache(true)
            .into(binding.imgUser)
        binding.tvDescription.text = desc
        binding.tvDate.text = DateFormatter.formatDate(date!!, TimeZone.getDefault().id)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_PICT = "extra_pict"
        const val EXTRA_DESC = "extra_desc"
        const val EXTRA_DATE = "extra_date"
    }
}