package com.example.absencemanagementapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.absencemanagementapp.models.Student
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var register_tv: TextView
    private lateinit var email_et: TextInputEditText
    private lateinit var password_et: TextInputEditText
    private lateinit var login_btn: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //initiate views
        initViews()

        register_tv.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        login_btn.setOnClickListener {
            val email = email_et.text.toString().trim().uppercase(Locale.getDefault())
            val password = password_et.text.toString().trim()

            if (validateInputs()) {
                login(email, password)
            }
        }
    }

    //on start
    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            val user_id = auth.currentUser!!.uid
            if (isStudent(user_id)) {
                Intent(this, StudentActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            } else {
                Intent(this, TeacherActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        } else {
            Log.d("TAG", "onStart: user is null")
        }
    }

    //check if user is a teacher or a student
    private fun isStudent(user_id: String): Boolean {
        var isStudent = false
        val ref = database.getReference("students")

        ref.child(user_id).get().addOnSuccessListener {
            if (it.exists()) {
                isStudent = true
            }
        }.addOnFailureListener {
            Log.d("TAG", "isStudent: ${it.message}")
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
        return isStudent
    }

    private fun login(email: String, password: String) {
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        //log in the user
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user = auth.currentUser
                //check if logged in user is a student or a teacher
                database.getReference("students").child(user!!.uid).get().addOnSuccessListener {
                    if (it.exists()) {
                        //user is a student
                        val student = it.getValue(Student::class.java)
                        Intent(this, StudentActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    } else {
                        //user is a teacher
                        Intent(this, TeacherActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    }
                }
            } else {
                // If sign in fails, display a message to the user
                Toast.makeText(
                    baseContext, "Authentication failed. Please try again.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initViews() {
        register_tv = findViewById(R.id.register_tv)
        email_et = findViewById(R.id.email_et)
        password_et = findViewById(R.id.password_et)
        login_btn = findViewById(R.id.login_btn)
    }

    private fun validateInputs(): Boolean {
        val email = email_et.text.toString()
        val password = password_et.text.toString()
        return when {
            email.isEmpty() -> {
                email_et.error = "Email is required"
                false
            }
            password.isEmpty() -> {
                password_et.error = "Password is required"
                false
            }
            else -> true
        }
    }
}