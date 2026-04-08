package com.example.eventhistory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * 画面遷移確認Main画面。
 * 画面上のボタンからLoop画面またはDFS/BFS画面へ遷移できます。
 */
class NavMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_main)

        findViewById<Button>(R.id.btnToLoop).setOnClickListener {
            startActivity(Intent(this, LoopActivity::class.java))
        }

        findViewById<Button>(R.id.btnToDfsBfs).setOnClickListener {
            startActivity(DfsBfsActivity.createIntent(this, "画面遷移確認DFS/BFS画面"))
        }
    }
}
