package com.example.eventhistory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * 画面遷移確認Loop画面。
 * この画面が表示された累計回数nをタイトルおよび本文に表示します。
 * 挑戦ボタン・チャレンジボタンで画面遷移確認Main画面へ戻ります。
 */
class LoopActivity : AppCompatActivity() {

    companion object {
        private var displayCount = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loop)

        displayCount++
        supportActionBar?.title = "画面遷移確認Loop($displayCount)画面"

        // 表示回数
        val loopCountTextView = findViewById<TextView>(R.id.loopCountTextView)
        loopCountTextView.text = "表示回数: $displayCount"

        // 年月日時分秒(UTC)
        val loopDateTextView = findViewById<TextView>(R.id.loopDateTextView)
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        loopDateTextView.text = "${dateFormat.format(Date())} (UTC)"

        // 挑戦ボタン → 画面遷移確認Main画面へ
        val challengeButton1 = findViewById<Button>(R.id.challengeButton1)
        challengeButton1.setOnClickListener {
            startActivity(Intent(this, NavMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }

        // チャレンジボタン → 画面遷移確認Main画面へ
        val challengeButton2 = findViewById<Button>(R.id.challengeButton2)
        challengeButton2.setOnClickListener {
            startActivity(Intent(this, NavMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }
    }
}
