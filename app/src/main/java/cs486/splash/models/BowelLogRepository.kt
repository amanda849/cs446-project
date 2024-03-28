package cs486.splash.models

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

const val TAG = "BOWEL_LOG_REPOSITORY"

/**
 * Temporarily the object that serves as storage holding the bowel logs. To be updated with
 * communication to a backend server
 */
class BowelLogRepository {
    private var bowelLogsDb: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Returns the collectionReference for livedata
     */
    fun fetchAllBowelLogs(): CollectionReference {
        return bowelLogsDb.collection(UserRepository.getUser()!!.uid)
    }

    /**
     * Returns the bowel logs after adding [bowelLog] to the repo
     */
    fun addNewBowelLog(bowelLog: BowelLog) : Task<Void> {
        return bowelLogsDb.collection(UserRepository.getUser()!!.uid).document()
            .set(bowelLog.toHashMap())
            .addOnSuccessListener {
                Log.i(TAG, "addNewBowelLog: added log")
            }
            .addOnFailureListener{
                Log.e(TAG, "addNewBowelLog: ${it.message}")
            }
    }

    /**
     * Returns the bowel logs after editing [editedBowelLog]
     */
    fun editBowelLog(editedBowelLog: BowelLog){
        bowelLogsDb.collection(UserRepository.getUser()!!.uid).document(editedBowelLog.id)
            .set(editedBowelLog.toHashMap(), SetOptions.merge())
            .addOnSuccessListener {
                Log.i(TAG, "editBowelLog: edited log ${editedBowelLog.id}")
            }
            .addOnFailureListener{
                Log.e(TAG, "editBowelLog: ${it.message}")
            }
    }

    /**
     * Returns the bowel logs after deleting the bowel log with id [id]
     */
    fun deleteBowelLog(id: String) : Task<Void>{
        return bowelLogsDb.collection(UserRepository.getUser()!!.uid).document(id)
            .delete()
            .addOnSuccessListener {
                Log.i(TAG, "deleteBowelLog: deleted log $id")
            }
            .addOnFailureListener{
                Log.e(TAG, "deleteBowelLog: ${it.message}")
            }
    }
}