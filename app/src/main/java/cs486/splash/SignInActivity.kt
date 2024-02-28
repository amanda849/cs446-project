package cs486.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import cs486.splash.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    // For logging, logs are found in logcat and search for TAG
    companion object {
        // Define a tag for your log messages
        private const val TAG = "SignInActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Key logic for signing into Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        binding.signInBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val pass = binding.password.text.toString()

            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    // firebase.currentUser.uid is the unique id of that user, likely the id will be used when retrieve the bowel logs for that user
                    firebaseAuth.currentUser?.let { it1 -> Log.e(TAG, it1.uid) }


                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // Note: Error can happen due to weak password, should show error message to user
                    // Should have graceful error handling
                    Log.e(TAG, "here1")
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
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
        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}

