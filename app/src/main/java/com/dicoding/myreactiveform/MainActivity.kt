package com.dicoding.myreactiveform

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailStream = RxTextView.textChanges(ed_email)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe {
            showEmailExistAlert(it)
        }

        val passwordStream = RxTextView.textChanges(ed_password)
            .skipInitialValue()
            .map { password ->
                password.length < 6
            }
        passwordStream.subscribe {
            showPasswordMinimalAlert(it)
        }

        val passwordConfirmationStream = Observable.merge(
            RxTextView.textChanges(ed_password)
                .map { password ->
                    password.toString() != ed_confirm_password.text.toString()
                },
            RxTextView.textChanges(ed_confirm_password)
                .map { confirmPassword ->
                    confirmPassword.toString() != ed_password.text.toString()
                }
        )
        passwordConfirmationStream.subscribe {
            showPasswordConfirmationAlert(it)
        }
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
