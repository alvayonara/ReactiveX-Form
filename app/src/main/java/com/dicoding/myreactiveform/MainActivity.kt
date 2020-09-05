package com.dicoding.myreactiveform

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun showEmailExistAlert(isValid: Boolean) {
        ed_email.error = if (isValid) getString(R.string.email_not_valid) else null
    }

    private fun showPasswordMinimalAlert(isValid: Boolean) {
        ed_password.error = if (isValid) getString(R.string.password_not_valid) else null
    }

    private fun showPasswordConfirmationAlert(isValid: Boolean) {
        ed_confirm_password.error = if (isValid) getString(R.string.password_not_same) else null
    }
}
