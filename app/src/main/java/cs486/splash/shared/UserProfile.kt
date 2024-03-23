package cs486.splash.shared

import com.google.firebase.Timestamp
import java.util.Date

class UserProfile(
    var name: String,
    var birthDate: Date,
    var familyDoctorName: String = "",
    var familyDoctorPhone: String = "",
    var familyDoctorEmail: String = ""
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "name" to name,
            "birthDate" to Timestamp(birthDate),
            "familyDoctorName" to familyDoctorName,
            "familyDoctorPhone" to familyDoctorPhone,
            "familyDoctorEmail" to familyDoctorEmail,
        )
    }
}