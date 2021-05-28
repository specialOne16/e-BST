package com.bangkit142.ebst.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit142.ebst.data.Resource
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel : ViewModel() {

    private val _nikExist : MutableLiveData<Resource<String>> = MutableLiveData()
    val nikExist : LiveData<Resource<String>> get() = _nikExist

    private var db = FirebaseFirestore.getInstance()

    fun check(nik : String) {
        _nikExist.value = Resource.Loading()
        if (nik.length != 16){
            _nikExist.value = Resource.Error("NIK not valid!")
            return
        }
        db.collection("users").document(nik)
            .get()
            .addOnSuccessListener { document ->
                if (document.data != null)
                    _nikExist.value = Resource.Success(nik)
                else {
                    _nikExist.value = Resource.Error("data not found", nik)
                }
            }.addOnFailureListener {
                _nikExist.value = Resource.Error("Internet connection error")
            }
    }

}