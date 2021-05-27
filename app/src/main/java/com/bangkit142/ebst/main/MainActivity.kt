package com.bangkit142.ebst.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit142.ebst.data.Resource
import com.bangkit142.ebst.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        intent.getStringExtra("NIK")?.let {
            viewModel.findUser(it)
        } ?: finish()

        viewModel.user.observe(this) {
            when (it) {
                is Resource.Success -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.tvNik.text = it.data?.get("NIK")?.toString() ?: ""
                    binding.tvNama.text = it.data?.get("nama")?.toString() ?: ""
                }
                is Resource.Loading -> {
                    binding.progressCircular.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progressCircular.visibility = View.GONE
                    Toast.makeText(this, "Check your connection!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }


    }
}