package com.example.test_2

import android.os.Parcel
import android.os.Parcelable

// Data class representing a user with name, age, and student status
data class User(val name: String, val age: Int, var isStudent: Boolean = false) : Parcelable {

    // Parcelable constructor to create a User object from a Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "", // Read the name from the Parcel or use an empty string if null
        parcel.readInt(), // Read the age from the Parcel
        parcel.readByte() != 0.toByte() // Read the student status from the Parcel as a boolean
    )

    // Function to write the User object to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name) // Write the name to the Parcel
        parcel.writeInt(age) // Write the age to the Parcel
        parcel.writeByte(if (isStudent) 1 else 0) // Write the student status as a byte to the Parcel (1 for true, 0 for false)
    }

    // Function to describe special objects about the User (not used in this case)
    override fun describeContents(): Int {
        return 0
    }

    // Companion object for creating instances of User from a Parcel
    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
