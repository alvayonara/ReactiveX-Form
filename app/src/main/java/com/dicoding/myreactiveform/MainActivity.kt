package com.dicoding.myreactiveform

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Menggunakan RxTextView.textChanges(ed_email) untuk membaca perubahan pada EditText dan mengubahnya menjadi data stream.
        // Operator skipInitialValue() untuk menghiraukan input awal agar aplikasi tidak langsung menampilkan eror pada saat pertama kali dijalankan.
        // Operator map dan memeriksa apakah format valid.
        val emailStream = RxTextView.textChanges(ed_email)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        // Saat subscribe, memanggil fungsi showEmailExistAlert(it) untuk menampilkan peringatan jika hasilnya TRUE.
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

        // Operator merge hanya menggabungkan datanya saja.
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

        // Operator combineLatest menggabungkan dan mengubah data di dalamnya.
        // Ex: Operator combineLatest untuk menggabungkan ketiga data stream dan menghasilkan 1 output data stream baru.
        val invalidFieldsStream = Observable.combineLatest(
            emailStream,
            passwordStream,
            passwordConfirmationStream,
            Function3 { emailInvalid: Boolean, passwordInvalid: Boolean, passwordConfirmationInvalid: Boolean ->
                !emailInvalid && !passwordInvalid && !passwordConfirmationInvalid
            }
        )
        invalidFieldsStream.subscribe { isValid ->
            if (isValid) {
                btn_register.isEnabled = true
                btn_register.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
            } else {
                btn_register.isEnabled = false
                btn_register.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.darker_gray
                    )
                )
            }
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
