package com.bangkit142.ebst.register

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit142.ebst.data.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class RegisterViewModel : ViewModel() {

    private val _submittedUser : MutableLiveData<Resource<String>> = MutableLiveData()
    val submittedUser : LiveData<Resource<String>> get() = _submittedUser

    private val _uploadPhoto : MutableLiveData<Resource<UploadTask.TaskSnapshot>> = MutableLiveData()
    val uploadPhoto : LiveData<Resource<UploadTask.TaskSnapshot>> get() = _uploadPhoto

    private val db = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    fun submit(user: Map<String, String>) {
        _submittedUser.value = Resource.Loading()

        db.collection("users").document(user["NIK"]!!)
            .set(user)
            .addOnSuccessListener {
                _submittedUser.value = Resource.Success(user["NIK"]!!)
            }
            .addOnFailureListener { e ->
                _submittedUser.value = Resource.Error("Internet connection error", user["NIK"]!!)
            }

    }

    fun upload(photoRumah: Bitmap, nik: String){
        _uploadPhoto.value = Resource.Loading()

        val imagesRef = storageRef.child("rumah/${nik}.jpg")

        val baos = ByteArrayOutputStream()
        photoRumah.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
            _uploadPhoto.value = Resource.Error(it.message.toString())
        }.addOnSuccessListener { taskSnapshot ->
            _uploadPhoto.value = Resource.Success(taskSnapshot)
        }
    }

}