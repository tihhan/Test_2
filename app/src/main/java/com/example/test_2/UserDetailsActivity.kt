package com.example.test_2

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.test_2.R
import com.example.test_2.User

// Activity for displaying the details of a selected user
class UserDetailsActivity : AppCompatActivity() {

    // Called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        // Get the User object passed from the calling activity via Intent
        val user = intent.getParcelableExtra<User>("user")

        // Check if the User object is not null
        user?.let {
            // Find the TextViews in the layout
            val nameTextView: TextView = findViewById(R.id.nameTextView)
            val ageTextView: TextView = findViewById(R.id.ageTextView)
            val isStudentTextView: TextView = findViewById(R.id.isStudentTextView)

            // Set the user details to the TextViews
            nameTextView.text = it.name
            ageTextView.text = it.age.toString()
            isStudentTextView.text = if (it.isStudent) "Student" else "Not a student"
        }
    }
}
