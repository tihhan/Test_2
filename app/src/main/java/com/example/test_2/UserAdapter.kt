package com.example.test_2

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Adapter class for displaying a list of users in a RecyclerView
class UserAdapter(
    private val context: Context,
    private val users: List<User>,
    private val onClickListener: (User) -> Unit,          // Click listener for item clicks
    private val onLongClickListener: (User) -> Unit,      // Long click listener for item long clicks
    private val showDeleteUserDialog: (User) -> Unit      // Function to show a delete confirmation dialog for a user
) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val switchStateMap = mutableMapOf<String, Boolean>()

    // Called when a new ViewHolder is needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item_layout, parent, false)
        return UserViewHolder(itemView)
    }

    // Called when binding data to a ViewHolder
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)

        // Set click listener for the item
        holder.itemView.setOnClickListener {
            onClickListener(user)
        }


        // Set long click listener for the item
        holder.itemView.setOnLongClickListener {
            showDeleteUserDialog(user) // Show delete confirmation dialog when long clicked
            true
        }


        holder.studentSwitch.setOnCheckedChangeListener(null)
        holder.studentSwitch.isChecked = switchStateMap[user.name] ?: user.isStudent

        // Set switch change listener for student status
        holder.studentSwitch.setOnCheckedChangeListener { _, isChecked ->
            user.isStudent = isChecked // Update student status when the switch is toggled
            saveUser(user) // Save the updated user data to SharedPreferences
        }
    }


    // Returns the number of items in the list
    override fun getItemCount(): Int {
        return users.size
    }

    // ViewHolder class for holding views for a single user item in the RecyclerView
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val ageTextView: TextView = itemView.findViewById(R.id.ageTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val studentSwitch: Switch = itemView.findViewById(R.id.studentSwitch)

        // Bind data to the ViewHolder views
        fun bind(user: User) {
            nameTextView.text = user.name
            ageTextView.text = user.age.toString()
            studentSwitch.isChecked = user.isStudent
            descriptionTextView.text = if (user.description.length <= 70) {
                user.description
            } else {
                user.description.substring(0, 70) + "..."
            }
            studentSwitch.setOnCheckedChangeListener { _, isChecked ->
                user.isStudent = isChecked
                saveUser(user)
            }
        }
    }

    // Function to save user data to SharedPreferences
    private fun saveUser(user: User) {
        val userList = retrieveUserList().toMutableList()
        val existingUser = userList.find { it.id == user.id }
        if (existingUser != null) {
            userList.remove(existingUser)
        }
        userList.add(user)

        val editor = getSharedPreferencesEditor()
        val json = Gson().toJson(userList)
        editor.putString("user_list", json)
        editor.apply()
    }


    // Function to retrieve the list of users from SharedPreferences
    private fun retrieveUserList(): List<User> {
        val json = getSharedPreferences().getString("user_list", null) // Retrieve the JSON string from SharedPreferences
        return if (json != null) {
            val typeToken = object : TypeToken<List<User>>() {}.type // Create a TypeToken for deserializing JSON to List<User>
            Gson().fromJson(json, typeToken) // Deserialize the JSON string to a List<User> using Gson
        } else {
            emptyList() // Return an empty list if the JSON string is null or not found
        }
    }

    // Function to get SharedPreferences instance
    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    }

    // Function to get SharedPreferences Editor instance
    private fun getSharedPreferencesEditor(): SharedPreferences.Editor {
        return context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit()
    }
}
