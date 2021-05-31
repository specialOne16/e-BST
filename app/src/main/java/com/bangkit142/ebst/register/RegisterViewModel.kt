package com.bangkit142.ebst.register

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit142.ebst.data.ApiConfig.provideApiService
import com.bangkit142.ebst.data.MLRequest
import com.bangkit142.ebst.data.MLResponse
import com.bangkit142.ebst.data.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class RegisterViewModel : ViewModel() {

    private val _submittedUser: MutableLiveData<Resource<String>> = MutableLiveData()
    val submittedUser: LiveData<Resource<String>> get() = _submittedUser

    private val _uploadPhoto: MutableLiveData<Resource<String>> = MutableLiveData()
    val uploadPhoto: LiveData<Resource<String>> get() = _uploadPhoto

    private val db = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    fun submit(nik: String, nama: String) {
        _submittedUser.value = Resource.Loading()

        if (nama.isEmpty() || mlResponse.isNullOrBlank()) {
            _submittedUser.value = Resource.Error("Check your name!", nik)
            return
        }

        if (mlResponse.isNullOrBlank()) {
            _submittedUser.value = Resource.Error("Check your photo!", nik)
            return
        }

        val user: MutableMap<String, String> = HashMap()
        user["NIK"] = nik
        user["nama"] = nama
        user["verdict"] = mlResponse!!

        db.collection("users").document(nik)
            .set(user)
            .addOnSuccessListener {
                _submittedUser.value = Resource.Success(nik)
            }
            .addOnFailureListener { e ->
                _submittedUser.value = Resource.Error("Internet connection error", nik)
            }

    }

    fun upload(photoRumah: Bitmap, nik: String) {
        _uploadPhoto.value = Resource.Loading()

        mlReceived = false
        fbUploaded = false
        mlResponse = null

        val imagesRef = storageRef.child("rumah/${nik}.jpg")

        val baos = ByteArrayOutputStream()
        photoRumah.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
            _uploadPhoto.value = Resource.Error(it.message.toString())
        }.addOnSuccessListener { taskSnapshot ->
            fbUploaded = true
            checkUploaded()
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        photoRumah.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encoded: String = Base64.encodeToString(byteArray, Base64.DEFAULT)

        val client = provideApiService().getVerdict(
            MLRequest(
                "${nik}.jpg",
                encoded
            )
        )
        client.enqueue(object : Callback<List<MLResponse>> {
            override fun onResponse(
                call: Call<List<MLResponse>>,
                response: Response<List<MLResponse>>
            ) {
                mlReceived = true
                mlResponse = response.body()?.get(0)?.verdict
                checkUploaded()
            }

            override fun onFailure(call: Call<List<MLResponse>>, t: Throwable) {
                _uploadPhoto.value = Resource.Error(t.message.toString())
            }

        })
    }

    private var mlReceived: Boolean = false
    private var fbUploaded: Boolean = false
    private var mlResponse: String? = null

    fun checkUploaded() {
        if (mlReceived && fbUploaded)
            _uploadPhoto.value =
                if (mlResponse.isNullOrBlank()) Resource.Error("Verdict error")
                else Resource.Success(mlResponse!!)
    }

}