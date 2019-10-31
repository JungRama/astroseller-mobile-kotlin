package com.example.mobilefinalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import android.util.Patterns
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // ACTION BAR
        val actionBar = supportActionBar
        actionBar!!.title = "Sign Up"

        // FIREBASE
        var auth: FirebaseAuth
        val db = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()

        // CHECK CURRENT USER IS ALREADY SIGN IN
        var currentUser = auth.currentUser

        // SHARED PREFERENCE
        val sharedPreference:SharedPreference = SharedPreference(this)

        // SET IF USER ALREADY LOGIN OR NOT
        if (sharedPreference.getValueString("isLogin")!=null) {
            intent = Intent(applicationContext, MainLayout::class.java)
            startActivity(intent)
        }

        // COMPONENT
        val etName  = findViewById<EditText>(R.id.etName)
        val etEmail  = findViewById<EditText>(R.id.etEmail)
        val etPassword  = findViewById<EditText>(R.id.etPassword)
        val btnSignIn  = findViewById<Button>(R.id.btnSignIn)

        val pbBar = findViewById<ProgressBar>(R.id.pbBar)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)

        tvSignIn.setOnClickListener {
            intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

        // BTN SIGN IN CLICKED
        btnSignIn.setOnClickListener {
            val name        = etName.getText().toString()
            val email       = etEmail.getText().toString()
            val password    = etPassword.getText().toString()

            // VALIDATION
            if (name.isEmpty()) {
                etName.setError("Name is required")
                etName.requestFocus()
            }else if (email.isEmpty()) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Fill email with a right format");
                etEmail.requestFocus();
            }else if (password.isEmpty()) {
                etPassword.requestFocus();
                etPassword.setError("Password is required");
            }else if (password.length < 6) {
                etPassword.requestFocus();
                etPassword.setError("Password Min 6 Character");
            }else{
                pbBar.setVisibility(View.VISIBLE)

                // DATA
                var user = hashMapOf(
                    "name" to name,
                    "shop_name" to null,
                    "shop_description" to null,
                    "email" to email,
                    "profile_picture" to null,
                    "supplier_status" to false,
                    "phone" to null,
                    "address" to null
                )
                // REGISTER USER
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {
                        if(it.isSuccessful){
                            // SET TO DATABASE
                            db.collection("users")
                                .document(FirebaseAuth.getInstance().currentUser!!.uid.toString())
                                .set(user as Map<String, Any>)
                                .addOnSuccessListener { documentReference ->
                                    pbBar.setVisibility(View.GONE)

                                    val toast = Toast.makeText(applicationContext, "Account success created, Sign in now", Toast.LENGTH_SHORT)
                                    toast.show()

                                    intent = Intent(applicationContext, LoginActivity::class.java)
                                    startActivity(intent)

                                    Log.d("currentUser", "${auth.currentUser}")
                                }
                                .addOnFailureListener { e ->
                                    Log.d("Main", "DocumentSnapshot added with ID: ${e}")
                                }
                        }else{
                            pbBar.setVisibility(View.GONE)
                            val toast = Toast.makeText(applicationContext, "Failed to create an account", Toast.LENGTH_SHORT)
                            toast.show()
                        }
                    }
                    .addOnFailureListener{
                        pbBar.setVisibility(View.GONE)
                        Log.d("Main", "Failed Login: ${it.message}")
                        Toast.makeText(this, "Email Already Used", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
