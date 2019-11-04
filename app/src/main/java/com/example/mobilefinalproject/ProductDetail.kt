package com.example.mobilefinalproject

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import java.text.NumberFormat

class ProductDetail : AppCompatActivity() {

    lateinit var iv_product: ImageView
    lateinit var tv_product_name: TextView
    lateinit var tv_product_price: TextView
    lateinit var tv_product_description: TextView
    lateinit var tv_sup_name: TextView
    lateinit var tv_sup_email: TextView
    lateinit var btnOrder: Button

    //EMAIL
    var emailto : String? = null
    var emailsubject : String? = null
    var emailmessage : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        iv_product = findViewById(R.id.iv_product)
        tv_product_name = findViewById(R.id.tv_product_name)
        tv_product_price = findViewById(R.id.tv_product_price)
        tv_product_description = findViewById(R.id.tv_product_description)
        tv_sup_name = findViewById(R.id.tv_sup_name)
        tv_sup_email = findViewById(R.id.tv_sup_email)
        btnOrder = findViewById(R.id.btnOrder)

        //GET ID
        val product_id = intent.getStringExtra("id")


        // ACTION BAR
        val actionBar = supportActionBar
        actionBar!!.title = "Product Detail"
        actionBar.setDisplayHomeAsUpEnabled(true);

        // FIREBASE
        val db = FirebaseFirestore.getInstance()
        val storageReference = FirebaseStorage.getInstance().getReference()


        val docRef = db.collection("products").document(
            product_id)
        // Source can be CACHE, SERVER, or DEFAULT.
        val source = Source.DEFAULT
        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                Glide
                    .with(applicationContext)
                    .load(document?.getString("product_image"))
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_product)
                    .into(iv_product)

                val price = Integer.valueOf(document?.getString("product_price")!!)

                actionBar!!.title = document?.getString("product_name")
                tv_product_name.setText(document?.getString("product_name"))
                tv_product_price.setText("Rp. " + NumberFormat.getInstance().format(price))

                if(document?.getString("product_description")==""){
                    tv_product_description.setText("This Product Don't have any description!")
                }else{
                    tv_product_description.setText(document?.getString("product_description"))
                }

                // GET SUPPLIER
                val docRefSupplier = db.collection("users").document(
                    document?.getString("user_id")!!
                )
                // GET SUPPLIER DATA
                docRefSupplier.get().addOnCompleteListener{supTask ->
                    val documentSupplier = supTask.result
                    if(supTask.isSuccessful){
                        tv_sup_name.setText(documentSupplier?.getString("shop_name"))
                        tv_sup_email.setText(documentSupplier?.getString("email"))



                        val docRefUser = db.collection("users").document(
                            FirebaseAuth.getInstance().currentUser!!.uid.toString())

                        docRefUser.get(source).addOnCompleteListener { userTask ->
                            if (task.isSuccessful) {
                                val documentUser = userTask.result

                                // SET EMAIL
                                emailto = documentSupplier?.getString("email")
                                emailsubject = "ARSTRO - ORDER ${document?.getString("product_name")!!.toUpperCase()}"
                                emailmessage =
                                    "Dear ${documentSupplier?.getString("shop_name")} \n" +
                                            "I want to order ${document?.getString("product_name")} \n" +
                                            "Here is the detail of my contact : \n" +
                                            "Name : ${documentUser?.getString("name")} \n" +
                                            "Email : ${documentUser?.getString("email")} \n" +
                                            "Phone : ${documentUser?.getString("phone")} \n" +
                                            "Address : ${documentUser?.getString("address")}"

                                btnOrder.setEnabled(true)

                                Log.d("DATA", "Cached document data: ${document?.data}")
                            } else {
                                Log.d("DATA", "Cached get failed: ", task.exception)
                            }
                        }

                    }
                }

                Log.d("DATA", "Cached document data: ${document?.data}")
            } else {
                Log.d("DATA", "Cached get failed: ", task.exception)
            }
        }

        // BTN ORDER
        btnOrder.setOnClickListener {
            //get input from EditTexts and save in variables
            val recipient = emailto.toString()
            val subject = emailsubject.toString()
            val message = emailmessage.toString()

            //method call for email intent with these inputs as parameters
            sendEmail(recipient, subject, message)
        }

    }

    private fun sendEmail(recipient: String, subject: String, message: String) {
        /*ACTION_SEND action to launch an email client installed on your Android device.*/
        val mIntent = Intent(Intent.ACTION_SENDTO)
        /*To send an email you need to specify mailto: as URI using setData() method
        and data type will be to text/plain using setType() method*/
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        // put recipient email in intent
        /* recipient is put as array because you may wanna send email to multiple emails
           so enter comma(,) separated emails, it will be stored in array*/
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        //put the Subject in the intent
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        //put the message in the intent
        mIntent.putExtra(Intent.EXTRA_TEXT, message)


        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }
}
