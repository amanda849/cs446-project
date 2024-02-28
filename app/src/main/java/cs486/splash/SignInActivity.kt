package cs486.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import cs486.splash.databinding.ActivitySignInBinding
import cs486.splash.models.UserRepository

class SignInActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySignInBinding

    // For logging, logs are found in logcat and search for TAG
    companion object {
        // Define a tag for your log messages
        private const val TAG = "SignInActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val pass = binding.password.text.toString()

            val user = UserRepository.userSignIn(email, pass)

            Log.e("SignIn", user.toString())

            if (user != null){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    // This function checks that if a current user is logged in, sign in page is skipped
    override fun onStart(){
        super.onStart()
        if(UserRepository.getUser()!= null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}