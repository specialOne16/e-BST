package com.bangkit142.ebst.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit142.ebst.data.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel : ViewModel() {

    private val _user : MutableLiveData<Resource<DocumentSnapshot>> = MutableLiveData()
    val user : LiveData<Resource<DocumentSnapshot>> get() = _user

    private var db = FirebaseFirestore.getInstance()

    fun findUser(nik : String) {
        _user.value = Resource.Loading()
        db.collection("users").document(nik)
            .get()
            .addOnSuccessListener { document ->
                _user.value = Resource.Success(document)
            }.addOnFailureListener {
                _user.value = Resource.Error("User not found!")
            }
    }

}