package com.example.mvvm_koin_firebase_kotlin

data class UserModel(val id: String, val userName: String, val userDesignation: String){
    constructor():this("","",""){

    }
}