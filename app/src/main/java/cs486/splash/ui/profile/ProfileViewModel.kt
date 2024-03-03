package cs486.splash.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cs486.splash.models.UserRepository

class ProfileViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = UserRepository.getUser()?.email
    }
    val useremail: LiveData<String> = _text
}