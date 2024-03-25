package cs486.splash.shared

import com.google.firebase.Timestamp

class UserProfile(
    var name: String,
    var birthDate: String,
    var familyDoctorName: String = "",
    var familyDoctorPhone: String = "",
    var familyDoctorEmail: String = ""
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "name" to name,
            "birthDate" to birthDate,
            "familyDoctorName" to familyDoctorName,
            "familyDoctorPhone" to familyDoctorPhone,
            "familyDoctorEmail" to familyDoctorEmail,
        )
    }
}