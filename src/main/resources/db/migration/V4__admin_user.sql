-- ローカル用管理者（平文パスワード: demo。V3 のデモユーザーと同じ BCrypt ハッシュ）
INSERT INTO "app_user" ("login_id", "password", "user_name", "is_admin") VALUES
('admin@bookreview.local', '$2a$10$Vniy5GjiLwyxnwW20xQNBuDMUH.Nmp0LpFgLEyYcyo2buHFZvqZdi', '管理者', 1);
