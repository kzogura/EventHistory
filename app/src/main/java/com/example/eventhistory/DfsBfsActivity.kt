package com.example.eventhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * 画面遷移確認DFS/BFS画面。
 * 画面名と子画面リストをIntentで受け取り、動的にボタンを生成します。
 *
 * ツリー構造:
 * DFS/BFS画面
 * ├── DFS/BFS画面1
 * ├── DFS/BFS画面2
 * │   ├── DFS/BFS画面2-1
 * │   ├── DFS/BFS画面2-2
 * │   │   └── DFS/BFS画面2-2-1
 * │   └── DFS/BFS画面2-3
 * └── DFS/BFS画面3
 */
class DfsBfsActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_SCREEN_NAME = "EXTRA_SCREEN_NAME"
        private const val EXTRA_IS_ROOT = "EXTRA_IS_ROOT"

        /** 各画面の子画面定義 */
        private val CHILDREN_MAP = mapOf(
            "画面遷移確認DFS/BFS画面" to listOf(
                "画面遷移確認DFS/BFS画面1",
                "画面遷移確認DFS/BFS画面2",
                "画面遷移確認DFS/BFS画面3"
            ),
            "画面遷移確認DFS/BFS画面2" to listOf(
                "画面遷移確認DFS/BFS画面2-1",
                "画面遷移確認DFS/BFS画面2-2",
                "画面遷移確認DFS/BFS画面2-3"
            ),
            "画面遷移確認DFS/BFS画面2-2" to listOf(
                "画面遷移確認DFS/BFS画面2-2-1"
            )
        )

        /**
         * DfsBfsActivityのIntentを生成します。
         * @param context コンテキスト
         * @param screenName 表示する画面名
         * @param isRoot ルート画面（DFS/BFS画面へ戻るボタンを非表示）の場合はtrue
         */
        fun createIntent(context: Context, screenName: String, isRoot: Boolean = true): Intent {
            return Intent(context, DfsBfsActivity::class.java).apply {
                putExtra(EXTRA_SCREEN_NAME, screenName)
                putExtra(EXTRA_IS_ROOT, isRoot)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dfs_bfs)

        val screenName = intent.getStringExtra(EXTRA_SCREEN_NAME) ?: "画面遷移確認DFS/BFS画面"
        val isRoot = intent.getBooleanExtra(EXTRA_IS_ROOT, true)

        supportActionBar?.title = screenName

        val screenNameTextView = findViewById<TextView>(R.id.dfsBfsScreenNameTextView)
        screenNameTextView.text = screenName

        // 子画面へのボタンを動的生成
        val childButtonsLayout = findViewById<LinearLayout>(R.id.childButtonsLayout)
        val children = CHILDREN_MAP[screenName] ?: emptyList()
        for (childName in children) {
            val button = Button(this)
            button.text = childName
            button.setOnClickListener {
                startActivity(createIntent(this, childName, isRoot = false))
            }
            childButtonsLayout.addView(button)
        }

        // ルート以外はDFS/BFS画面へ戻るボタンを表示
        val backButton = findViewById<Button>(R.id.backToDfsBfsRootButton)
        if (!isRoot) {
            backButton.visibility = View.VISIBLE
            backButton.setOnClickListener {
                startActivity(
                    createIntent(this, "画面遷移確認DFS/BFS画面").apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                )
            }
        }
    }
}
