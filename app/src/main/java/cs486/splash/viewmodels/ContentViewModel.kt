package cs486.splash.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import cs486.splash.models.Content
import cs486.splash.models.Content.Companion.toContent
import cs486.splash.models.ContentRepository

class ContentViewModel {
    val TAG = "CONTENT_VIEW_MODEL"
    var contents : MutableLiveData<List<Content>> = MutableLiveData()

    init {
        getAllContents()
    }

    /**
     * Ties bowelLogs to the state of the bowelLogRepository for the current user/collection
     * Do not call this again as it will tie another listener
     */
    private fun getAllContents() : ListenerRegistration {
        return ContentRepository.fetchAllContent()
            .addSnapshotListener(EventListener<QuerySnapshot> addSnapshotListener@{ value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val contentList : MutableList<Content> = mutableListOf()
                for (documentSnapshot in value!!) {
                    val logItem = documentSnapshot.toContent()
                    logItem?.let {
                        // not null
                        contentList.add(logItem)
                    }
                }
                contents.value = contentList
            })
    }
}