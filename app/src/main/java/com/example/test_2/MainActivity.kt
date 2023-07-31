package com.example.test_2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val userList = mutableListOf<User>()
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the toolbar and navigation drawer
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        // Set navigation item click listener
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_add_user -> {
                    openAddUserActivity()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.menu_sorting -> {
                    showSortingDialog()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }

        // Set the home button in the toolbar to open the navigation drawer
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_add_user)
        }

        // Initialize SharedPreferences for storing user data
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Retrieve the user list from SharedPreferences
        userList.addAll(retrieveUserList())

        // Set up the RecyclerView and the UserAdapter
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(this, userList, { user ->
            showUserDetailsFragment(user)
        },
            { user -> showUserDetailsFragment(user) },
            { user -> showDeleteUserDialog(user) })

        recyclerView.adapter = adapter
    }

    // Save the user list to SharedPreferences
    private fun saveUserList(users: List<User>) {
        val json = Gson().toJson(users)
        val editor = sharedPreferences.edit()
        editor.putString("user_list", json)
        editor.apply()
    }


    // Handle the result from AddUserActivity
    @Deprecated("Deprecated in Java")
    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_USER && resultCode == Activity.RESULT_OK) {
            userList.clear()
            userList.addAll(retrieveUserList())
            adapter.notifyDataSetChanged()
            showUserAddedSuccessfullyMessage()
        }
    }

    // Show a Snackbar message when a user is added successfully
    private fun showUserAddedSuccessfullyMessage() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "User added successfully",
            Snackbar.LENGTH_SHORT
        ).show()
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

    // Show the UserDetailsFragment with the selected user
    private fun showUserDetailsFragment(user: User) {
        val fragment = UserDetailsFragment.newInstance(user)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Create the options menu (not used in this case)
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }



    // Handle options menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Open the navigation drawer when the home button is clicked
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            R.id.menu_add_user -> {
                // Open AddUserActivity when the "Add User" menu item is clicked
                openAddUserActivity()
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

            R.id.menu_sorting -> {
                // Show the sorting dialog when the "Sorting" menu item is clicked
                showSortingDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }




    // Show the sorting options dialog
    private fun showSortingDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_sorting, null)
        val builder = AlertDialog.Builder(this)
            .setTitle("Sorting Options")
            .setView(dialogView)

        val alertDialog = builder.create()

        val sortByNames = dialogView.findViewById<TextView>(R.id.sortByName)
        val sortByAge = dialogView.findViewById<TextView>(R.id.sortByAge)
        val sortByStudentStatus = dialogView.findViewById<TextView>(R.id.sortByStudentStatus)
        val sortByDescription = dialogView.findViewById<TextView>(R.id.sortByDescriptionLength)

        // Click listeners for sorting options
        sortByNames.setOnClickListener {
            sortAndRefreshList(SortingOption.NAME)
            alertDialog.dismiss()
        }

        sortByAge.setOnClickListener {
            sortAndRefreshList(SortingOption.AGE)
            alertDialog.dismiss()
        }

        sortByStudentStatus.setOnClickListener {
            sortAndRefreshList(SortingOption.STUDENT_STATUS)
            alertDialog.dismiss()
        }

        sortByDescription.setOnClickListener{
            sortAndRefreshList(SortingOption.DESCRIPTION)
            alertDialog.dismiss()
        }



        alertDialog.show()
    }

    // Sort the user list and refresh the RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    private fun sortAndRefreshList(sortingOption: SortingOption) {
        when (sortingOption) {
            SortingOption.NAME -> {
                userList.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
            }
            SortingOption.AGE -> userList.sortBy { it.age }
            SortingOption.STUDENT_STATUS -> {
                userList.sortWith(compareBy<User> { !it.isStudent }.thenBy { it.name })
            }
            SortingOption.DESCRIPTION -> {
                userList.sortBy { it.description?.length }
            }
        }

        adapter.notifyDataSetChanged()
    }






    // Show a dialog to confirm user deletion
    private fun showDeleteUserDialog(user: User) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("You want to delete this User")
        alertDialogBuilder.setMessage("Are you sure you want to delete this user?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            deleteUser(user)
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialogBuilder.show()
    }


    // Delete the selected user from the user list and update SharedPreferences
    private fun deleteUser(user: User) {
        userList.remove(user)
        saveUserList(userList)
        adapter.notifyDataSetChanged()
    }


    // Open AddUserActivity for adding a new user
    private fun openAddUserActivity() {
        val intent = Intent(this, AddUserActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_ADD_USER)
    }

    companion object {
        const val REQUEST_CODE_ADD_USER = 1001
    }
}

// Enumeration for sorting options
enum class SortingOption {
    NAME,
    AGE,
    STUDENT_STATUS,
    DESCRIPTION
}

