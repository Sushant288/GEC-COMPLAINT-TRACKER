package com.gec.geccomplainttracker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    private val IMAGE_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val name = findViewById<EditText>(R.id.etName)
        val dept = findViewById<EditText>(R.id.etDepartment)
        val complaint = findViewById<EditText>(R.id.etComplaint)
        val spinner = findViewById<Spinner>(R.id.spinnerDepartment)
        val selectImage = findViewById<Button>(R.id.btnSelectImage)
        val submit = findViewById<Button>(R.id.btnSubmit)
        val imageView = findViewById<ImageView>(R.id.imgPreview)

        // Spinner data
        val departments = arrayOf(
            "Select Authority",
            "Electrical Department",
            "Plumbing Department",
            "IT Cell",
            "Housekeeping",
            "Civil / Infrastructure"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            departments
        )
        spinner.adapter = adapter

        val dbRef = FirebaseDatabase.getInstance().getReference("complaints")

        selectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_REQUEST)
        }

        submit.setOnClickListener {

            if (spinner.selectedItemPosition == 0) {
                Toast.makeText(this, "Please select authority", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = HashMap<String, String>()
            data["name"] = name.text.toString()
            data["department"] = dept.text.toString()
            data["complaint"] = complaint.text.toString()
            data["addressedTo"] = spinner.selectedItem.toString()
            data["imageSelected"] = if (imageUri != null) "Yes" else "No"
            data["status"] = "Pending"

            dbRef.push().setValue(data)

            Toast.makeText(this, "Complaint Submitted", Toast.LENGTH_SHORT).show()

            name.text.clear()
            dept.text.clear()
            complaint.text.clear()
            spinner.setSelection(0)
            imageView.visibility = ImageView.GONE
            imageUri = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            val imageView = findViewById<ImageView>(R.id.imgPreview)
            imageView.setImageURI(imageUri)
            imageView.visibility = ImageView.VISIBLE
        }
    }
}
