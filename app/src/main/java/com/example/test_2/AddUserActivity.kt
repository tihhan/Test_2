package com.example.test_2

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddUserActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var daySpinner: Spinner
    private lateinit var monthSpinner: Spinner
    private lateinit var yearSpinner: Spinner
    private lateinit var addUserButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        // Initialize the toolbar and enable the back button
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Find views by their IDs
        nameEditText = findViewById(R.id.nameEditText)
        ageEditText = findViewById(R.id.ageEditText)
        daySpinner = findViewById(R.id.daySpinner)
        monthSpinner = findViewById(R.id.monthSpinner)
        yearSpinner = findViewById(R.id.yearSpinner)
        addUserButton = findViewById(R.id.addButton)

        // Get the SharedPreferences instance for user data storage
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Set up the date spinners
        val days = (1..31).map { it.toString() }
        val months = SimpleDateFormat("MMMM", Locale.US).let { dateFormat ->
            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
            }.run {
                (0..11).map {
                    add(Calendar.MONTH, it)
                    dateFormat.format(time)
                }
            }
        }
        val years = (Calendar.getInstance().get(Calendar.YEAR) downTo 1900).map { it.toString() }

        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)

        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        daySpinner.adapter = dayAdapter
        monthSpinner.adapter = monthAdapter
        yearSpinner.adapter = yearAdapter

        // Set click listener for the Add User button
        addUserButton.setOnClickListener {
            // Get user input data
            val name = nameEditText.text.toString()
            val age = ageEditText.text.toString().toIntOrNull()
            val day = daySpinner.selectedItem.toString().toInt()
            val month = monthSpinner.selectedItemPosition
            val year = yearSpinner.selectedItem.toString().toInt()

            // Validate user input
            if (name.isNotEmpty() && age != null) {
                // Create a new User object and save it
                val cal = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                val newUser = User(name, age)
                saveUser(newUser)
                Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Please enter valid name and age", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle the back button click event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Save the user data to SharedPreferences
    private fun saveUser(user: User) {
        val userList = retrieveUserList().toMutableList()
        userList.add(user)

        val editor = sharedPreferences.edit()
        val json = Gson().toJson(userList)
        editor.putString("user_list", json)
        editor.apply()
    }

    // Retrieve the user list from SharedPreferences
    private fun retrieveUserList(): List<User> {
        val json = sharedPreferences.getString("user_list", null)
        return if (json != null) {
            val typeToken = object : TypeToken<List<User>>() {}.type
            Gson().fromJson(json, typeToken)
        } else {
            emptyList()
        }
    }
}
