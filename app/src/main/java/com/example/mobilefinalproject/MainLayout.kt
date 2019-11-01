package com.example.mobilefinalproject

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.example.mobilefinalproject.Fragments.EditProfileFragment
import com.example.mobilefinalproject.Fragments.HomeFragment
import com.example.mobilefinalproject.Fragments.ShopFragment
import com.example.mobilefinalproject.Fragments.SupplierFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.android.synthetic.main.app_bar_home.*

class MainLayout : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        setSupportActionBar(toolbar)

        // ACTION BAR
        val actionBar = supportActionBar
        actionBar!!.title = "Home"


        // FIREBASE
        var auth: FirebaseAuth
        val db = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser

        // SHARED PREFERENCE
        val sharedPreference:SharedPreference = SharedPreference(this)

        // SET IF USER ALREADY LOGIN OR NOT
        if (sharedPreference.getValueString("isLogin")==null) {
            intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

        // ---COMPONENT
            // HEADER SIDEBAR COMPONENT
            val navigationView = findViewById(R.id.nav_view) as NavigationView
            val header = navigationView.getHeaderView(0)

            val ivAvatar  = header.findViewById<CircleImageView>(R.id.ivAvatar)
            val tvName  = header.findViewById<TextView>(R.id.tvName)
            val tvEmail  = header.findViewById<TextView>(R.id.tvEmail)

        // COLLECT USER DATA
        val docRef = db.collection("users").document(sharedPreference.getValueString("UID")!!)
        // Source can be CACHE, SERVER, or DEFAULT.
        val source = Source.DEFAULT
        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                // SET DATA TO SIDEBAR
//                if (document?.getString("profile_picture") == null){
                    //                    ivAvatar.setImageResource(R.drawable.placeholder_avatar)
//                }else{
//                    Picasso.get(this)
//                        .load("http://static1.gamespot.com/uploads/original/1550/15507091/2867734-7512874458-CAK00.jpg")
//                        .placeholder(R.drawable.placeholder_avatar)
//                        .into(ivAvatar)
//                }

                tvEmail.setText(document?.getString("email"))

                Log.d("DATA", "Cached document data: ${document?.data}")
            } else {
                Log.d("DATA", "Cached get failed: ", task.exception)
            }
        }

        // SET DEFAULT FRAGMENT
        supportFragmentManager.beginTransaction().replace(R.id.fragment_content,
            HomeFragment.newInstance()).commit()
        navigationView.setCheckedItem(R.id.nav_home);

        // TOOGLE BAR
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the MainLayout/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // FIREBASE
        var auth: FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // SHARED PREFERENCE
        val sharedPreference:SharedPreference = SharedPreference(this)

        val navigationView = findViewById(R.id.nav_view) as NavigationView

        // ACTION BAR
        val actionBar = supportActionBar

        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_content,
                    HomeFragment.newInstance()).commit()
                navigationView.setCheckedItem(R.id.nav_home);

                actionBar!!.title = "Home"
            }
            R.id.nav_supplier -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_content,
                    SupplierFragment.newInstance()).commit()
                navigationView.setCheckedItem(R.id.nav_supplier);

                actionBar!!.title = "Supplier"
            }
            R.id.nav_edit_profile -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_content,
                    EditProfileFragment.newInstance()).commit()
                navigationView.setCheckedItem(R.id.nav_edit_profile);

                actionBar!!.title = "Edit Profile"
            }
            R.id.nav_sign_out -> {

                // Initialize a new instance of
                val builder = AlertDialog.Builder(this)

                // Set the alert dialog title
                builder.setTitle("Sign Out")

                // Display a message on alert dialog
                builder.setMessage("Are you sure want to Sign Out ?")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("Yes"){dialog, which ->
                    auth.signOut()
                    intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)

                    sharedPreference.clearSharedPreference()

                    Log.d("currentUser", "${auth.currentUser}")
                }

                // Display a negative button on alert dialog
                builder.setNegativeButton("No"){dialog, which ->
                    dialog.dismiss();
                }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
