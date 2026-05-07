INSERT INTO "app_user" ("login_id", "password", "user_name", "is_admin") VALUES
('demo-user@example.com', 'dummy-password', 'デモユーザー', 0);

INSERT INTO "book" ("title", "author", "publisher", "description", "published_date", "image_path", "sales_url", "user_id")
VALUES
('チェンソーマン 1', '藤本タツキ', '集英社', '公安対魔特異4課に所属するデンジの物語。', DATE '2019-03-04', 'img/Noimage.png', 'https://example.com/book/1', NULL),
('BLEACH 1', '久保帯人', '集英社', '死神代行となった黒崎一護の戦い。', DATE '2002-01-05', 'img/Noimage.png', 'https://example.com/book/2', NULL),
('パンどろぼう', '柴田ケイコ', 'KADOKAWA', 'ユーモアたっぷりの人気絵本。', DATE '2020-04-16', 'img/Noimage.png', 'https://example.com/book/3', NULL);
