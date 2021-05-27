package com.bangkit142.ebst.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit142.ebst.data.Resource
import com.bangkit142.ebst.databinding.ActivityLoginBinding
import com.bangkit142.ebst.main.MainActivity
import com.bangkit142.ebst.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.btnCheckNik.setOnClickListener {
            viewModel.check(binding.etNik.text.toString())
        }

        viewModel.nikExist.observe(this) {
            when (it) {
                is Resource.Success -> {
                    binding.progressCircular.visibility = View.GONE
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        this.putExtra("NIK", it.data)
                    })
                }
                is Resource.Loading -> {
                    binding.progressCircular.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progressCircular.visibility = View.GONE
                    if (it.data != null) {
                        startActivity(Intent(this, RegisterActivity::class.java).apply {
                            this.putExtra("NIK", it.data)
                        })
                    } else {
                        Toast.makeText(this, "Check your connection!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}