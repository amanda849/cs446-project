package cs486.splash.models

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot

class Content (
    var title: String,
    var url: String,
    var source: String,
    var content: String,
    var imageUrl: String,
    var datePublished: String,
){
    companion object {
        private const val TAG = "CONTENT_CONVERSION"
        fun DocumentSnapshot.toContent(): Content? {
            return try {
                val title = id
                val url = getString("url")!!
                val source = getString("source")!!
                val content = getString("content")!!
                val imageUrl = getString("imageUrl")!!
                val datePublished = getString("datePublished")!!

                Content(title, url, source, content, imageUrl, datePublished)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting content", e)
                null
            }
        }
    }
}