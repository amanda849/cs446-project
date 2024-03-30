package cs486.splash.ui.profile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import cs486.splash.databinding.FragmentEditProfileBinding
import cs486.splash.models.AuthenticationException
import cs486.splash.viewmodels.EmptyStringException
import cs486.splash.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        userViewModel.userProfile.observe(viewLifecycleOwner){
            if (it != null) {
                _binding!!.nameField.setText(it.name)
                _binding!!.bdayField.setText(it.birthDate)
            }
        }

        _binding!!.bdayField.setOnClickListener {
            showDatePicker()
        }

        // Save button
        _binding!!.saveBtn.setOnClickListener {
            // Password
            lifecycleScope.launch {
                try {
                    userViewModel.updatePassword(
                        _binding!!.currentPasswordField.text.toString(),
                        _binding!!.passwordField.text.toString(),
                        _binding!!.confirmPasswordField.text.toString()
                    )

                    // Show AlertDialog to confirm password change
                    AlertDialog.Builder(requireContext())
                        .setTitle("Info")
                        .setMessage("Password has been changed!")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                } catch (e: AuthenticationException) {
                    // Show AlertDialog when exception is caught
                    AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage("${e.message}")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                } catch(_: EmptyStringException){}
            }

            // Email
            lifecycleScope.launch {
                try {
                    userViewModel.updateEmail(
                        _binding!!.emailPasswordField.text.toString(),
                        _binding!!.emailField.text.toString(),
                        _binding!!.confirmEmailField.text.toString()
                    )

                    // Show AlertDialog to confirm password change
                    AlertDialog.Builder(requireContext())
                        .setTitle("Info")
                        .setMessage("Email change initiated, check your inbox for confirmation!")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                } catch (e: AuthenticationException) {
                    // Show AlertDialog when exception is caught
                    AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage("${e.message}")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                } catch(_: EmptyStringException){}
            }

            userViewModel.setUserName(_binding!!.nameField.text.toString())
            userViewModel.setBirthDate(_binding!!.bdayField.text.toString())

            // Go back to profile after updating view model
            returnToProfile()
        }

        // Back to profile page button
        _binding!!.backBtn.setOnClickListener {
            returnToProfile()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Remove this fragment and display profile page fragment again
    private fun returnToProfile() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(this.id, ProfileFragment())
        fragmentTransaction.addToBackStack(null) // Optional: Add to back stack
        fragmentTransaction.commit()
    }

    private fun showDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()

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

            _binding!!.bdayField.setText(formattedDate)
        }

        picker.show(parentFragmentManager, picker.toString())
    }
}