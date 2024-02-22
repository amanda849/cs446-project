package cs486.splash.ui.analysis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AnalysisViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is analysis Fragment"
    }
    val text: LiveData<String> = _text
}