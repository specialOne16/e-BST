package com.bangkit142.ebst.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit142.ebst.data.Resource
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel : ViewModel() {

    private val _submittedUser : MutableLiveData<Resource<String>> = MutableLiveData()
    val submittedUser : LiveData<Resource<String>> get() = _submittedUser

    private var db = FirebaseFirestore.getInstance()

    fun submit(user : Map<String, String>) {
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

}