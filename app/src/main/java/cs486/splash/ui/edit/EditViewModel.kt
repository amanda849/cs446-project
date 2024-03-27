package cs486.splash.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is edit Fragment"
    }
    val text: LiveData<String> = _text
}