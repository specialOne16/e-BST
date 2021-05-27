package com.bangkit142.ebst.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit142.ebst.data.Resource
import com.bangkit142.ebst.databinding.ActivityRegisterBinding
import com.bangkit142.ebst.main.MainActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        binding.etNik.setText(intent.getStringExtra("NIK"))

        binding.btnSubmit.setOnClickListener {
            val user: MutableMap<String, String> = HashMap()
            user["NIK"] = binding.etNik.text.toString()
            user["nama"] = binding.etNama.text.toString()

            viewModel.submit(user)
        }

        viewModel.submittedUser.observe(this) {
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
                    Toast.makeText(this, "Check your connection!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}