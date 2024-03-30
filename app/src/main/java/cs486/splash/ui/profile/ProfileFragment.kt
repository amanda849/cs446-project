package cs486.splash.ui.profile

import android.R
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import cs486.splash.OnboardingActivity
import cs486.splash.databinding.FragmentProfileBinding
import cs486.splash.models.AuthenticationException
import cs486.splash.models.UserRepository
import cs486.splash.viewmodels.EmptyStringException
import cs486.splash.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Declare userViewModel as a class property
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        userViewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                _binding!!.emailField.text = it.email
            }
        }

        userViewModel.userProfile.observe(viewLifecycleOwner){
            if (it != null) {
                _binding!!.usernameField.text = it.name
                _binding!!.birthDateField.text = it.birthDate
            }
        }

        _binding!!.usernameCard.setOnClickListener {
            showNameDialog()
        }

        _binding!!.birthDateCard.setOnClickListener {
            showDatePicker()
        }

        _binding!!.emailCard.setOnClickListener {
            showEmailDialog()
        }

        _binding!!.passwordCard.setOnClickListener {
            showPasswordDialog()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding!!.signOutBtn.setOnClickListener {
            UserRepository.userSignOut()

            val intent = Intent(requireContext(), OnboardingActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showNameDialog() {
        val editText = EditText(requireContext())
        editText.hint = "New username"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(Html.fromHtml("<b>Change your username<b>"))
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                userViewModel.setUserName(editText.text.toString())
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    private fun showDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()

        builder.setTitleText("Change your birthday")
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

            userViewModel.setBirthDate(formattedDate)
        }

        picker.show(parentFragmentManager, picker.toString())
    }

    private fun showEmailDialog() {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        val passwordEditText = EditText(requireContext())
        val emailEditText = EditText(requireContext())
        val emailConfirmationEditText = EditText(requireContext())


        passwordEditText.hint = "Password"
        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        emailEditText.hint = "New email"
        emailConfirmationEditText.hint = "Confirm new email"

        layout.addView(passwordEditText)
        layout.addView(emailEditText)
        layout.addView(emailConfirmationEditText)


        MaterialAlertDialogBuilder(requireContext())
            .setTitle(Html.fromHtml("<b>Change your email<b>"))
            .setView(layout)
            .setPositiveButton("OK") { _, _ ->
                changeEmail(
                    passwordEditText.text.toString(),
                    emailEditText.text.toString(),
                    emailConfirmationEditText.text.toString()
                )
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    private fun changeEmail(
        pass: String,
        newEmail: String,
        confirmEmail: String
    ){
        // Email
        lifecycleScope.launch {
            try {
                userViewModel.updateEmail(
                    pass,
                    newEmail,
                    confirmEmail
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
    }

    private fun showPasswordDialog() {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        val currentPasswordEditText = EditText(requireContext())
        val newPasswordEditText = EditText(requireContext())
        val confirmPasswordEditText = EditText(requireContext())


        currentPasswordEditText.hint = "Current password"
        currentPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        newPasswordEditText.hint = "New password"
        newPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        confirmPasswordEditText.hint = "Confirm new password"
        confirmPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        layout.addView(currentPasswordEditText)
        layout.addView(newPasswordEditText)
        layout.addView(confirmPasswordEditText)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(Html.fromHtml("<b>Change your password<b>"))
            .setView(layout)
            .setPositiveButton("OK") { _, _ ->
                changePassword(
                    currentPasswordEditText.text.toString(),
                    newPasswordEditText.text.toString(),
                    confirmPasswordEditText.text.toString()
                )
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    private fun changePassword(
        currentPass: String,
        newPass: String,
        confirmPass: String
    ){
        lifecycleScope.launch {
            try {
                userViewModel.updatePassword(
                    currentPass,
                    newPass,
                    confirmPass
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
    }
}