package cs486.splash.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import cs486.splash.databinding.FragmentBirthdaySetupBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import cs486.splash.R
import cs486.splash.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class BirthdaySetupFragment : Fragment() {
    private var _binding: FragmentBirthdaySetupBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBirthdaySetupBinding.inflate(inflater, container, false)
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        _binding!!.birthDate.setOnClickListener {
            showDatePicker()
        }

        _binding!!.nextBtn.setOnClickListener {
            userViewModel.setBirthDate(_binding!!.birthDate.text.toString())

            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, SetupDoneFragment())
            fragmentTransaction.addToBackStack(null) // Optional: Add to back stack
            fragmentTransaction.commit()
        }

        return binding.root
    }

    private fun showDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()

        builder.setTitleText("Set your birthday")

        // Set the range of selectable dates to be from today's date backward
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        builder.setSelection(today)
        builder.setCalendarConstraints(
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .build()
        )

        val picker = builder.build()

        picker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val formattedDate = dateFormat.format(Date(selection))

            _binding!!.birthDate.setText(formattedDate)
        }

        picker.show(parentFragmentManager, picker.toString())
    }
}