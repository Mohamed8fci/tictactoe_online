package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*


class Login : AppCompatActivity() {
    private var mAuth:FirebaseAuth?=null
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
    }
    fun bu_Login(view: View){
       connecttodatobase(etEmail.text.toString(), etPassword.text.toString())
    }

    fun connecttodatobase(Email: String, Password: String){
        mAuth!!.createUserWithEmailAndPassword(Email, Password)
            .addOnCompleteListener(this) { task->
                if (task.isSuccessful){
                    Toast.makeText(this, "successful login", Toast.LENGTH_LONG).show()
                    var currentuser = mAuth!!.currentUser
                    if (currentuser != null) {
                        myRef.child("Users").child(splitString(currentuser.email.toString())).child("Request").setValue(currentuser.uid)

                        LoadMain()
                    }
                }else{
                    Toast.makeText(this, "fail login", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        LoadMain()
    }

    fun LoadMain() {
        var currentuser = mAuth!!.currentUser

        if (currentuser != null) {
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentuser.email)
            intent.putExtra("uid", currentuser.uid)
            startActivity(intent)
        }
    }
        fun splitString(str: String):String{
            var split = str.split("@")
            return split[0]
        }
    }
