package com.bangkit142.ebst.register

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
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
        binding.etNik.isEnabled = false
        binding.btnSubmit.isEnabled = false

        binding.btnSubmit.setOnClickListener {
            val user: MutableMap<String, String> = HashMap()
            user["NIK"] = binding.etNik.text.toString()
            user["nama"] = binding.etNama.text.toString()

            viewModel.submit(user)
        }

        binding.btnPhoto.setOnClickListener {
            dispatchTakePictureIntent()
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
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.uploadPhoto.observe(this) {
            when (it) {
                is Resource.Success -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.btnSubmit.isEnabled = true
                    binding.etNik.isEnabled = false
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

    @Suppress("DEPRECATION")
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.ivRumah.setImageBitmap(imageBitmap)
            viewModel.upload(imageBitmap, binding.etNik.text.toString())
        }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }
}