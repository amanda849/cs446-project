package cs486.splash.models

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

const val TAG = "BowelLogRepository"

/**
 * Temporarily the object that serves as storage holding the bowel logs. To be updated with
 * communication to a backend server
 */
object  BowelLogRepository {
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // MB add a snap shot listener? see https://firebase.google.com/docs/firestore/query-data/listen?_gl=1*11caovd*_up*MQ..*_ga*MjU0MzE1NDQzLjE3MDkyMzE3MTI.*_ga_CW55HF8NVT*MTcwOTIzMTcxMi4xLjAuMTcwOTIzMTcxMi4wLjAuMA..
    // Problem with that is how to deal with when user changes?

    /**
     * Returns the bowel logs
     */
    fun fetchAllBowelLogs(): List<DocumentSnapshot> {
        val bowelLogList = mutableListOf<DocumentSnapshot>()

        firebaseFirestore.collection(UserRepository.getUser()!!.uid)
            .get()
            .addOnSuccessListener {
                for(document in it){
                    bowelLogList.add(document)
                }
                Log.i(TAG, "fetchAllBowelLogs: Successfully fetched all logs")
            }
            .addOnFailureListener {
                Log.e(TAG, "fetchAllBowelLogs: ${it.message}")
            }

        // This needs to wait for .get() to finish ... right now its not waiting for the task to finish
        return bowelLogList
    }

    /**
     * Returns the bowel logs after adding [bowelLog] to the repo
     */
    fun addNewBowelLog(bowelLog: BowelLog){
        firebaseFirestore.collection(UserRepository.getUser()!!.uid).document()
            .set(bowelLog.toHashMap())
            .addOnSuccessListener {
                Log.i(TAG, "addNewBowelLog: added log")
            }
            .addOnFailureListener{
                Log.e(TAG, "addNewBowelLog: ${it.message}")
            }
        // needs to wait for set to be completed
    }

    /**
     * Returns the bowel logs after editing [editedBowelLog]
     */
    fun editBowelLog(editedBowelLog: BowelLog){
        firebaseFirestore.collection(UserRepository.getUser()!!.uid).document(editedBowelLog.id)
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
    fun deleteBowelLog(id: String){
        firebaseFirestore.collection(UserRepository.getUser()!!.uid).document(id)
            .delete()
            .addOnSuccessListener {
                Log.i(TAG, "deleteBowelLog: deleted log $id")
            }
            .addOnFailureListener{
                Log.e(TAG, "deleteBowelLog: ${it.message}")
            }
    }

    /** Was used for testing purposes (feel free to delete if no longer in use)
     * fun testAdd(hashMap: HashMap<String, String>){
     *         firebaseFirestore.collection(UserRepository.getUser()!!.uid).document().set(hashMap)
     *     }
     *
     *     fun testGet(){
     *         val bowelLogList = mutableListOf<DocumentSnapshot>()
     *         firebaseFirestore.collection(UserRepository.getUser()!!.uid)
     *             .get()
     *             .addOnSuccessListener {
     *                 for(document in it){
     *                     bowelLogList.add(document)
     *                 }
     *             }
     *     }
     *
     *     fun testEdit(id: String, hashMap: HashMap<String, String>){
     *         firebaseFirestore.collection(UserRepository.getUser()!!.uid).document(id)
     *             .set(hashMap, SetOptions.merge())
     *             .addOnSuccessListener {
     *                 Log.i(TAG, "editBowelLog: edited log")
     *             }
     *             .addOnFailureListener{
     *                 Log.e(TAG, "editBowelLog: ${it.message}")
     *             }
     *     }
     */
}