package com.example.eventhistory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.security.ProviderInstaller
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date

/**
 * EventHistoryアプリのメインアクティビティです。
 * Googleログイン、Firestoreへのデータ保存、および履歴表示を管理します。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var adapter: EventAdapter
    private val eventList = mutableListOf<EventEntry>()
    
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient

    // Googleサインインの結果を受け取るランチャー
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("MainActivity", "Google Sign-In failed", e)
                Toast.makeText(this, "Googleサインインに失敗しました: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Google Play Services のセキュリティプロバイダを更新
        updateSecurityProvider()
        
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Firebaseの初期化
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Googleサインインの設定
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setupUI()
        
        // 既にログインしていればデータを読み込む
        if (auth.currentUser != null) {
            updateUIForLoggedInUser()
            loadEventsFromFirestore()
        }
    }

    private fun updateSecurityProvider() {
        ProviderInstaller.installIfNeededAsync(this, object : ProviderInstaller.ProviderInstallListener {
            override fun onProviderInstalled() {
                Log.i("MainActivity", "Security provider installed successfully")
            }

            override fun onProviderInstallFailed(errorCode: Int, recoveryIntent: Intent?) {
                Log.e("MainActivity", "Security provider installation failed: $errorCode")
                GoogleApiAvailability.getInstance().showErrorNotification(this@MainActivity, errorCode)
            }
        })
    }

    private fun setupUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.historyRecyclerView)
        adapter = EventAdapter(eventList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val categoryEditText = findViewById<TextInputEditText>(R.id.categoryEditText)
        val itemEditText = findViewById<TextInputEditText>(R.id.itemEditText)
        val memoEditText = findViewById<TextInputEditText>(R.id.memoEditText)
        val addButton = findViewById<Button>(R.id.addButton)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            signInLauncher.launch(signInIntent)
        }

        addButton.setOnClickListener {
            val user = auth.currentUser
            if (user == null) {
                Toast.makeText(this, "先にログインしてください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category = categoryEditText.text.toString()
            val itemName = itemEditText.text.toString()
            val memo = memoEditText.text.toString()

            if (itemName.isNotBlank()) {
                val newEntry = hashMapOf(
                    "userId" to user.uid,
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

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateUIForLoggedInUser()
                    loadEventsFromFirestore()
                } else {
                    Log.e("MainActivity", "Firebase authentication failed", task.exception)
                    Toast.makeText(this, "Firebase認証に失敗しました", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loadEventsFromFirestore() {
        val user = auth.currentUser ?: return
        
        db.collection("events")
            .whereEqualTo("userId", user.uid)
            .orderBy("actionDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val fetchedEvents = result.map { doc ->
                    EventEntry(
                        id = doc.id,
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

    private fun updateUIForLoggedInUser() {
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.text = "ログイン中: ${auth.currentUser?.email}"
        loginButton.isEnabled = false
    }
}
