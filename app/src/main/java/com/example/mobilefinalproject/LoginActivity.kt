package com.example.mobilefinalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import android.content.Intent
import android.util.Log
import android.util.Patterns
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import android.app.ProgressDialog
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // ACTION BAR
//        val actionBar = supportActionBar
//        actionBar!!.title = "Sign In"

        // FIREBASE
        var auth: FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // SHARED PREFERENCE
        val sharedPreference:SharedPreference = SharedPreference(this)

        // CHECK CURRENT USER IS ALREADY SIGN IN
        var currentUser = auth.currentUser

        // SET IF USER ALREADY LOGIN OR NOT
        if (sharedPreference.getValueString("isLogin")!=null) {
            intent = Intent(applicationContext, MainLayout::class.java)
            startActivity(intent)
        }

        // COMPONENT
        val etEmail  = findViewById<EditText>(R.id.etEmail)
        val etPassword  = findViewById<EditText>(R.id.etPassword)
        val btnSignIn  = findViewById<Button>(R.id.btnSignIn)

        val tvSignUp  = findViewById<TextView>(R.id.tvSignUp)

        tvSignUp.setOnClickListener {
            intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnSignIn.setOnClickListener {
            val email       = etEmail.getText().toString();
            val password    = etPassword.getText().toString();

            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Fill email with a right format");
                etEmail.requestFocus();
            }else if (password.isEmpty()) {
                etPassword.requestFocus();
                etPassword.setError("Password is required");
            }else{

                val progress = ProgressDialog.show(this, "",
                    "Please Wait ...", false);

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {

                            progress.dismiss()

                            currentUser = auth.currentUser

                            intent = Intent(applicationContext, MainLayout::class.java)
                            startActivity(intent)

                            sharedPreference.save("isLogin", currentUser.toString())
                            sharedPreference.save("UID", currentUser!!.uid.toString()) // SET UID OF USER

                            Toast.makeText(applicationContext, "Sign In Success", Toast.LENGTH_SHORT).show()
                            Log.d("currentUser", "${auth.currentUser}")
                        }else{
                            progress.dismiss()
                            Toast.makeText(applicationContext, "Sign In Error, Email or Password Incorrect", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        progress.dismiss()
                        Log.d("Main", "Failed Login: ${it.message}")
                        Toast.makeText(this, "Sign In Error, Email or Password Incorrect", Toast.LENGTH_SHORT).show()
                    }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPreference:SharedPreference = SharedPreference(this)
        if (sharedPreference.getValueString("isLogin")!=null) {
            intent = Intent(applicationContext, MainLayout::class.java)
            startActivity(intent)
        }
    }
}
