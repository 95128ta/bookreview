# bookreview

書籍の閲覧・検索・レビュー投稿、読書ステータスやブックマーク、ユーザー登録とプロフィール編集、管理者向けの書籍・レビュー管理までを扱う **Spring Boot** の学習・ポートフォリオ用 Web アプリです。

## 技術スタック

| 区分 | 内容 |
|------|------|
| 言語 | Java 17 |
| フレームワーク | Spring Boot 3.4.x（Web / Security / Data JPA / Thymeleaf / Validation / Actuator） |
| データベース | H2（ローカルはファイル永続化、テストはインメモリ） |
| スキーマ管理 | Flyway |
| ビルド | Maven（`mvnw` 同梱） |

## 前提条件

- JDK 17 以上

## 起動方法

```bash
# Windows
.\mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

ブラウザで `http://localhost:8080/` を開きます。  
本番・PaaS では環境変数 `PORT` が指定された場合、そのポートで待ち受けます。

## テスト

```bash
.\mvnw.cmd clean test
```

テストは `application-test.yml` により **インメモリ H2** を使用し、ローカルの `./data` とは分離されます。

## デモ用アカウント（シード）

Flyway のマイグレーションで投入されます。**ローカル検証用**です。本番では利用しないでください。

| 用途 | ログイン ID | パスワード |
|------|-------------|------------|
| 一般ユーザー | `demo-user@example.com` | `demo` |
| 管理者 | `admin@bookreview.local` | `demo` |

新規登録（`/register`）でも一般ユーザーとしてアカウントを作成できます。

## 主な URL

| パス | 説明 |
|------|------|
| `/` `/menu` | トップ・メニュー |
| `/books` `/books/{id}` | 書籍一覧・詳細（レビュー・ブックマーク・読書ステータスはログイン後） |
| `/search` | 検索 |
| `/ranking` | ランキング |
| `/reading` | 読書中の一覧（ログイン後） |
| `/login` `/register` | ログイン・新規登録 |
| `/profile` | プロフィール（表示名・パスワード・退会） |
| `/bookmarks` | ブックマーク一覧 |
| `/admin/books` | 管理者：書籍管理 |
| `/actuator/health` | ヘルスチェック（認証不要） |

## Render へのデプロイ（概要）

このリポジトリは **Docker** でビルドできるようにしてあります。

1. [Render](https://render.com) で **New → Web Service**、Git リポジトリを接続
2. **Runtime**: Docker（ルートの `Dockerfile` を使用）
3. **Health Check Path**: `/actuator/health`（ダッシュボードで設定可能）
4. デプロイ後に表示される URL にアクセス

リポジトリルートの `render.yaml` を使うと、上記に近い設定を Blueprint として再利用できます。

### 注意（H2 on PaaS）

既定のままでは **H2 ファイル DB** をコンテナ内に置いています。**再デプロイでコンテナが作り直されるとデータは消える**ことがあります。常時同じデータを載せたい場合は、マネージド DB（PostgreSQL 等）への切り替えを検討してください。

## アーキテクチャの目安

- **Web**: `com.bookreview.web`（Thymeleaf 向けコントローラ）
- **ドメイン / 永続化**: JPA エンティティと `...repository`
- **ユースケース**: `com.bookreview.service`
- **セキュリティ**: Spring Security（フォームログイン、`ROLE_ADMIN` / `ROLE_USER`）

## セキュリティについて

- パスワードは **BCrypt** で保存します。
- `/admin/**` は管理者ロールのみアクセス可能です。

## データについて（ローカル）

`application.yml` では H2 を **`./data/bookreview` にファイル保存**します。  
アプリを止めてもデータは残ります（`data/` を削除すると初期状態に戻せます）。

## ライセンス

未指定（必要に応じて追記してください）。
