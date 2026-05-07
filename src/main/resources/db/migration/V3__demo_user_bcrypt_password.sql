-- デモユーザーはログイン検証用。平文 dummy-password から BCrypt（平文パスワード: demo）へ更新。
UPDATE "app_user"
SET "password" = '$2a$10$Vniy5GjiLwyxnwW20xQNBuDMUH.Nmp0LpFgLEyYcyo2buHFZvqZdi'
WHERE "login_id" = 'demo-user@example.com';
