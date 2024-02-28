package cs486.splash.viewmodels

import com.google.firebase.auth.FirebaseUser
import cs486.splash.models.UserRepository

class SignOutViewModel {
    fun signOut(){
        return UserRepository.userSignOut()
    }
}