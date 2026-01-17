package com.gec.geccomplainttracker

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 🔹 Views
        val etName = findViewById<EditText>(R.id.etName)
        val etEnrollment = findViewById<EditText>(R.id.etEnrollment)
        val etDepartment = findViewById<EditText>(R.id.etDepartment)
        val etComplaint = findViewById<EditText>(R.id.etComplaint)
        val spinner = findViewById<Spinner>(R.id.spinnerDepartment)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnSelectImage = findViewById<Button>(R.id.btnSelectImage)

        // 🔹 Spinner data
        val authorities = arrayOf(
            "Select Authority",
            "IT cell",
            "Civil",
            "House keeping",
            "Hostel",
            "Library"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            authorities
        )
        spinner.adapter = adapter

        // 🔹 Firebase reference
        val database = FirebaseDatabase.getInstance()
        val complaintRef = database.getReference("complaints")

        // 🔹 Image button (MVP – no upload yet)
        btnSelectImage.setOnClickListener {
            Toast.makeText(this, "Image feature coming soon", Toast.LENGTH_SHORT).show()
        }

        // 🔹 Submit button
        btnSubmit.setOnClickListener {

            val name = etName.text.toString().trim()
            val enrollment = etEnrollment.text.toString().trim()
            val department = etDepartment.text.toString().trim()
            val complaint = etComplaint.text.toString().trim()
            val authority = spinner.selectedItem.toString()

            // 🔴 Validation
            if (name.isEmpty()) {
                etName.error = "Enter name"
                return@setOnClickListener
            }

            if (enrollment.isEmpty()) {
                etEnrollment.error = "Enter enrollment number"
                return@setOnClickListener
            }

            if (complaint.isEmpty()) {
                etComplaint.error = "Enter complaint"
                return@setOnClickListener
            }

            if (authority == "Select Authority") {
                Toast.makeText(this, "Please select authority", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Create unique ID
            val complaintId = complaintRef.push().key

            if (complaintId != null) {

                val complaintData = mapOf(
                    "studentName" to name,
                    "enrollmentNumber" to enrollment,
                    "department" to department,
                    "complaintText" to complaint,
                    "authority" to authority,
                    "status" to "Pending"
                )

                // 🔹 Save to Firebase
                complaintRef.child(complaintId).setValue(complaintData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Complaint submitted successfully", Toast.LENGTH_LONG).show()

                        // Clear fields
                        etName.text.clear()
                        etEnrollment.text.clear()
                        etDepartment.text.clear()
                        etComplaint.text.clear()
                        spinner.setSelection(0)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to submit complaint", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

}
