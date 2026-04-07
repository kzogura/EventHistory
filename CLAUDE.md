# EventHistory プロジェクト - Claude Code ガイドライン

## プロジェクト概要

Android アプリ（Kotlin）。Firebase Firestore を使ったイベント履歴管理アプリ。

- **パッケージ名**: `com.example.eventhistory`
- **言語**: Kotlin
- **データストア**: Firebase Firestore
- **ビルドツール**: Gradle (Kotlin DSL)

---

## 開発ワークフロー（必須）

### 基本ルール

- **作業は必ず GitHub Issue から開始すること**
- `master` への直接 push は禁止
- 実装完了後は必ず PR を作成し、人のレビューを経てからマージする

### 手順

1. **Issue 確認**
   ```
   gh issue view {Issue番号}
   ```
   Issue の内容・要件・背景を把握してから作業を開始する。

2. **作業ブランチ作成**
   ```
   git checkout -b feature/issue-{番号}-{概要}   # 新機能
   git checkout -b fix/issue-{番号}-{概要}        # バグ修正
   ```

3. **実装・コミット**
   - コミットメッセージ形式: `{type}: {変更内容} (#Issue番号)`
   - 例: `feat: カテゴリフィルター機能を追加 (#12)`
   - type: `feat` / `fix` / `refactor` / `test` / `docs`

4. **Push & PR 作成**
   ```
   git push origin {ブランチ名}
   gh pr create --title "{タイトル}" --body "Closes #{Issue番号}"
   ```
   - PR 本文には必ず `Closes #{Issue番号}` を含める
   - レビュアーをアサインする

5. **マージはしない**
   - PR 作成後、Claude Code はマージを行わない
   - マージはレビュアーが承認後に実施する

---

## 技術スタック

| 項目 | 内容 |
|------|------|
| 言語 | Kotlin |
| 最小SDK | app/build.gradle.kts を参照 |
| データモデル | `EventEntry.kt` |
| リスト表示 | `EventAdapter.kt`（RecyclerView） |
| DB | Firebase Firestore |

### データモデル（EventEntry）

```kotlin
data class EventEntry(
    val id: String,          // Firestore ドキュメントID
    val userId: String,
    val category: String,
    var itemName: String,    // Firestore: item_name
    var actionDate: Date,    // Firestore: action_date
    val memo: String
)
```

---

## コーディング規約

- Kotlin の標準コーディング規約に従う
- Firestore フィールド名はスネークケース（`@PropertyName` アノテーションを使用）
- コメントは日本語で記載する
- 新規クラス・関数には KDoc コメントを付ける

---

## やってはいけないこと

- `master` ブランチへの直接コミット・push
- Issue なしでの機能追加・変更
- PR レビュー前のマージ
- `google-services.json` などの認証情報のコミット（すでに .gitignore 対象）
