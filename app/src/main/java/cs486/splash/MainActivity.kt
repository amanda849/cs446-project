package cs486.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import cs486.splash.databinding.ActivityMainBinding
import cs486.splash.models.UserRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (UserRepository.getUser() == null){
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
        }

        supportActionBar?.hide() // hides the top status bar thing

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_calendar,
                R.id.navigation_analysis,
                R.id.navigation_add,
                R.id.navigation_content,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // used for testing purposes (please delete if not in use)
        /*
        binding.submitBtn.setOnClickListener {
            val t = binding.test.text.toString()
            val map = HashMap<String, String>()
            map["test"] = t
            binding.test.setText("")

            //BowelLogRepository.deleteBowelLog("w1st74EGcXTJdDCfWkSe")

            val documentObserver = Observer<List<BowelLog>> { listOfLogs ->
                // Update the UI, in this case, a TextView.
                for (log in listOfLogs) {
                    Log.w("VIEW", "Got " + log.id)
                }
            }

            val tempLog = BowelLog("hi", 1, "Running a test", java.util.Date(), java.util.Date(), "",
                SymptomTags(), FactorTags(), java.util.Date(), java.util.Date())
            blvm.deleteBowelLog("Fri Mar 01 13:37:54 EST 2024")
            blvm.bowelLogs.observe(this, documentObserver)
            // BowelLogRepository.testEdit("qqQTLZgUD894h56gi6QV", map)
        }
        */
    }
}