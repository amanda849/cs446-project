package cs486.splash.ui.poopview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PoopViewViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is poop view Fragment"
    }
    val text: LiveData<String> = _text
}