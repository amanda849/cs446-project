package cs486.splash.ui.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContentViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is content Fragment"
    }
    val text: LiveData<String> = _text
}