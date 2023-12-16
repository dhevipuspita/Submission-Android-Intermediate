package com.dicoding.MyStoryApp.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.MyStoryApp.MyAdapter
import com.dicoding.MyStoryApp.R
import com.dicoding.MyStoryApp.data.Result
import com.dicoding.MyStoryApp.data.response.ListStoryItem
import com.dicoding.MyStoryApp.databinding.ActivityMainBinding
import com.dicoding.MyStoryApp.view.ViewModelFactory
import com.dicoding.MyStoryApp.view.detail.DetailActivity
import com.dicoding.MyStoryApp.view.maps.MyMaps
import com.dicoding.MyStoryApp.view.story.NewStoryActivity
import com.dicoding.MyStoryApp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            val btnAddIntent = Intent(this@MainActivity, NewStoryActivity::class.java)
            startActivity(btnAddIntent)
        }

        supportActionBar?.show()

        val manageLayout = LinearLayoutManager(this)
        binding.rvStories.layoutManager = manageLayout

        val item = DividerItemDecoration(this, manageLayout.orientation)
        binding.rvStories.addItemDecoration(item)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                viewModel.getStories().observe(this) { item ->
                    if (item != null) {
                        when (item) {
                            is Result.Loading -> {
                                showLoading(true)
                            }

                            is Result.Success -> {
                                val dataStory = item.data
                                val adapter = MyAdapter(object : MyAdapter.OnItemClickCallBack {
                                    override fun onItemClicked(data: ListStoryItem) {
                                        val intent =
                                            Intent(
                                                this@MainActivity,
                                                DetailActivity::class.java
                                            )
                                        intent.putExtra(DetailActivity.EXTRA_TITLE, data.name)
                                        intent.putExtra(
                                            DetailActivity.EXTRA_PICT,
                                            data.photoUrl
                                        )
                                        intent.putExtra(
                                            DetailActivity.EXTRA_DESC,
                                            data.description
                                        )
                                        intent.putExtra(
                                            DetailActivity.EXTRA_DATE,
                                            data.createdAt
                                        )
                                        startActivity(intent)
                                    }
                                })

                                adapter.submitList(dataStory)
                                binding.rvStories.adapter = adapter
                                showLoading(false)
                            }

                            is Result.Error -> {
                                showLoading(false)
                                Toast.makeText(
                                    this,
                                    getString(R.string.unsuccess_text), Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                AlertDialog.Builder(this).apply {
                    setMessage(getString(R.string.logout_alert))
                    setPositiveButton(getString(R.string.btn_next)) { _, _ ->
                        // Tindakan yang diambil saat item "Logout" diklik
                        viewModel.logout()
                        finish()
                        val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                        startActivity(intent)
                    }
                    setNegativeButton(getString(R.string.btn_cancel)) { dialog, _ ->
                        // Tindakan yang diambil jika pengguna membatalkan logout
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
                return true
            }

            R.id.menu_maps -> {
                val intent = Intent(this@MainActivity, MyMaps::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStories()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}