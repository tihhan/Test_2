package com.example.test_2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.test_2.R
import com.example.test_2.User

// Fragment for displaying the details of a selected user
class UserDetailsFragment : Fragment() {

    companion object {
        private const val ARG_USER = "user"

        // Static method to create a new instance of UserDetailsFragment and pass the User object as arguments
        fun newInstance(user: User): UserDetailsFragment {
            val fragment = UserDetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_USER, user)
            fragment.arguments = args
            return fragment
        }
    }

    // Called when the fragment view is created
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_detail, container, false)

        // Retrieve the User object from the fragment arguments
        val user = arguments?.getParcelable<User>(ARG_USER)

        // Check if the User object is not null
        user?.let {
            // Find the TextViews in the layout
            val nameTextView: TextView = view.findViewById(R.id.nameTextView)
            val ageTextView: TextView = view.findViewById(R.id.ageTextView)
            val isStudentTextView: TextView = view.findViewById(R.id.isStudentTextView)

            // Set the user details to the TextViews
            nameTextView.text = it.name
            ageTextView.text = it.age.toString()
            isStudentTextView.text = if (it.isStudent) "Student" else "Not a student"
        }

        return view
    }
}
