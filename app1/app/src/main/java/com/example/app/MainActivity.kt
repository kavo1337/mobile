package com.example.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailEt = findViewById<EditText>(R.id.emailText)
        val passEt = findViewById<EditText>(R.id.passwordText)
        val loginBtn = findViewById<Button>(R.id.loginButton)

        loginBtn.setOnClickListener {
            val email = emailEt.text.toString().trim()
            val pass = passEt.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val conn = URL("https://localhost:7062/login")
                        .openConnection() as HttpURLConnection

                    conn.requestMethod = "POST"
                    conn.doOutput = true

                    // JSON данные
                    val json = """{"email":"$email","password":"$pass"}"""
                    conn.setRequestProperty("Content-Type", "application/json")

                    conn.outputStream.write(json.toByteArray())

                    val code = conn.responseCode

                    withContext(Dispatchers.Main) {
                        if (code == 200 || code == 201) {
                            // УСПЕШНАЯ АВТОРИЗАЦИЯ
                            Toast.makeText(
                                this@MainActivity,
                                "✅ ПРОШЕЛ",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            // НЕУДАЧНАЯ АВТОРИЗАЦИЯ
                            Toast.makeText(
                                this@MainActivity,
                                "❌ Ошибка: $code",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "⚠ Ошибка сети: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}