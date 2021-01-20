package com.example.testapplication

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private lateinit var password: TextInputEditText
    private lateinit var login: TextInputEditText
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private lateinit var preferenceManager: SharedPreferenceManager
    val fragment = PFFingerprintAuthDialogFragment()
    private val FINGERPRINT_DIALOG_FRAGMENT_TAG = "FingerprintDialogFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferenceManager = SharedPreferenceManager(this)
        initUI()
    }

    private fun initUI() {
        alertDialogBuilder = AlertDialog.Builder(this)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        findViewById<Button>(R.id.saveButton).setOnClickListener {
            if (!isFieldsEmpty()) {
                preferenceManager.setLogin(login.text.toString())
                preferenceManager.setPassword(password.text.toString())
                password.setText("")
                login.setText("")
                showAlertDialog("Поздравляем!", "Вы успешно сохранили данные для авторизации")
            }
        }
        findViewById<Button>(R.id.checkButton).setOnClickListener {
            if (!isFieldsEmpty()) {
                dateChecking()
            }
        }
        fragment.setAuthListener(object : PFFingerprintAuthListener {
            override fun onAuthenticated() {
                showAlertDialog("Поздравляем!", "Вы успешно авторизировались чреез отпечаток пальца")
                fragment.dismiss()
            }

            override fun onError() {
                showAlertDialog("Ошибка!", "Ошибка аутентификации")
            }
        })
        findViewById<FloatingActionButton>(R.id.fingerPrint).setOnClickListener {
            fragment.show(
                supportFragmentManager, FINGERPRINT_DIALOG_FRAGMENT_TAG
            )
        }

    }

    private fun isFieldsEmpty() =
        (password.text.toString().isEmpty() || login.text.toString().isEmpty()).also { isEmpty ->
            if (isEmpty) {
                showAlertDialog("Предупреждение!", "Некоторые поля пустые")
            }
        }

    private fun dateChecking() {
        (preferenceManager.getPassword().equals(password.text.toString())
                && preferenceManager.getLogin().equals(login.text.toString()))
            .let { checking ->
                when (checking) {
                    true -> {
                        password.setText("")
                        login.setText("")
                        showAlertDialog("Поздравляем!", "Вы успешно авторизировались")
                    }
                    false -> showAlertDialog("Ошибка!", "Данные не совпадают")
                }
            }
    }

    private fun showAlertDialog(title: String, message: String) {
        alertDialogBuilder.apply {
            setTitle(title)
            setMessage(message)
            setNeutralButton("ok") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }
}
