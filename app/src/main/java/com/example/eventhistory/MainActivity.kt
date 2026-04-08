package com.example.eventhistory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date

/**
 * EventHistoryアプリのメインアクティビティです。
 * Firestoreへのデータ保存、および履歴表示を管理します。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var adapter: EventAdapter
    private val eventList = mutableListOf<EventEntry>()
    private lateinit var db: FirebaseFirestore
    private var currentUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // LoginActivityからユーザー名を取得
        currentUsername = intent.getStringExtra("USERNAME")
        if (currentUsername == null) {
            // ログインしていない場合はログイン画面へ
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        db = FirebaseFirestore.getInstance()

        setupUI()
        loadEventsFromFirestore()
    }

    private fun setupUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userTextView = findViewById<TextView>(R.id.userTextView)
        userTextView.text = "ログイン中: $currentUsername"

        val recyclerView = findViewById<RecyclerView>(R.id.historyRecyclerView)
        adapter = EventAdapter(eventList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val categoryEditText = findViewById<TextInputEditText>(R.id.categoryEditText)
        val itemEditText = findViewById<TextInputEditText>(R.id.itemEditText)
        val memoEditText = findViewById<TextInputEditText>(R.id.memoEditText)
        val addButton = findViewById<Button>(R.id.addButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        logoutButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val navCheckButton = findViewById<Button>(R.id.navCheckButton)
        navCheckButton.setOnClickListener {
            startActivity(Intent(this, NavMainActivity::class.java))
        }

        addButton.setOnClickListener {
            val username = currentUsername ?: return@setOnClickListener

            val category = categoryEditText.text.toString()
            val itemName = itemEditText.text.toString()
            val memo = memoEditText.text.toString()

            if (itemName.isNotBlank()) {
                val newEntry = hashMapOf(
                    "userId" to username,
                    "category" to category,
                    "itemName" to itemName,
                    "actionDate" to Date(),
                    "memo" to memo
                )

                db.collection("events")
                    .add(newEntry)
                    .addOnSuccessListener {
                        categoryEditText.text?.clear()
                        itemEditText.text?.clear()
                        memoEditText.text?.clear()
                        loadEventsFromFirestore() // 再読み込み
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "保存に失敗しました", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun loadEventsFromFirestore() {
        val username = currentUsername ?: return
        
        db.collection("events")
            .whereEqualTo("userId", username)
            .orderBy("actionDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val fetchedEvents = result.map { doc ->
                    EventEntry(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        category = doc.getString("category") ?: "",
                        itemName = doc.getString("itemName") ?: "",
                        actionDate = doc.getDate("actionDate") ?: Date(),
                        memo = doc.getString("memo") ?: ""
                    )
                }
                adapter.updateEvents(fetchedEvents)
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Firestore read failed", e)
                Toast.makeText(this, "データの読み込みに失敗しました: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
