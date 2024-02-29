package cs486.splash

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import cs486.splash.databinding.ActivityMainBinding
import cs486.splash.models.BowelLogRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_calendar, R.id.navigation_analysis, R.id.navigation_add, R.id.navigation_content, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        /** used for testing purposes (please delete if not in use)
        binding.submitBtn.setOnClickListener {
            val t = binding.test.text.toString()
            val map = HashMap<String, String>()
            map["test"] = t
            binding.test.setText("")

            //BowelLogRepository.testGet()

            //BowelLogRepository.deleteBowelLog("w1st74EGcXTJdDCfWkSe")

            //BowelLogRepository.testAdd(map)
            BowelLogRepository.testEdit("qqQTLZgUD894h56gi6QV", map)
        }**/

    }
}