package com.example.eventhistory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = FirebaseFirestore.getInstance()

        // 初期データの登録（デバッグ用・要件通り）
        setupDefaultUsers()

        val usernameEditText = findViewById<TextInputEditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginBtn = findViewById<Button>(R.id.loginBtn)

        loginBtn.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "ユーザー名とパスワードを入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("users").document(username).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        if (user?.password == password) {
                            // ログイン成功
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("USERNAME", username)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "パスワードが正しくありません", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "ユーザーが見つかりません", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "ログインエラー: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupDefaultUsers() {
        val users = listOf(
            User("user1", "pass1"),
            User("user2", "pass2")
        )

        for (user in users) {
            db.collection("users").document(user.username).set(user)
        }
    }
}
