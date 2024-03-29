package cs486.splash.shared

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import cs486.splash.models.Content

class UserProfile(
    var name: String,
    var birthDate: String,
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "name" to name,
            "birthDate" to birthDate,
        )
    }

    companion object {
        private const val TAG = "PROFILE_CONVERSION"
        fun DocumentSnapshot.toProfile(): UserProfile? {
            return try {
                UserProfile(getString("name")!!, getString("birthDate")!!)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting content", e)
                null
            }
        }
    }
}