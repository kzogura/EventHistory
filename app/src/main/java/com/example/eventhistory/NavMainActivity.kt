package com.example.eventhistory

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

/**
 * 画面遷移確認Main画面。
 * プルダウンメニューからLoop画面またはDFS/BFS画面へ遷移できます。
 */
class NavMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_main)
        // NoActionBarテーマのためToolbarを手動でActionBarに設定する
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_nav_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_loop -> {
                startActivity(Intent(this, LoopActivity::class.java))
                true
            }
            R.id.menu_dfsbfs -> {
                startActivity(DfsBfsActivity.createIntent(this, "画面遷移確認DFS/BFS画面"))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
