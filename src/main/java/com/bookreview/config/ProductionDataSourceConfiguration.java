package com.bookreview.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Render 等で {@code DATABASE_URL}（{@code postgres://} / {@code postgresql://}）だけが渡される場合に JDBC の
 * DataSource を組み立てる。{@code spring.datasource.url} が既に JDBC 形式で設定されていればそれを優先する。
 */
@Configuration
@Profile("production")
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class ProductionDataSourceConfiguration {

	/**
	 * Render は {@code DATABASE_URL} を渡す。{@code spring.datasource.url}（例: H2 の application-local）より必ず優先する。
	 */
	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource.hikari")
	public DataSource dataSource(
			DataSourceProperties properties,
			@Value("${DATABASE_URL:}") String databaseUrl) {
		if (StringUtils.hasText(databaseUrl)) {
			return buildFromRenderStyleUrl(databaseUrl);
		}
		if (StringUtils.hasText(properties.getUrl())) {
			String jdbcUrl = properties.getUrl();
			if (jdbcUrl.contains(":h2:")) {
				throw new IllegalStateException(
						"production で H2 が指定されています。Render では DATABASE_URL（Internal Database URL）を Web Service に設定してください。");
			}
			return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
		}
		throw new IllegalStateException(
				"production プロファイルでは DATABASE_URL（Render の Postgres 連携）"
						+ " または spring.datasource.url を設定してください。");
	}

	static DataSource buildFromRenderStyleUrl(String databaseUrl) {
		String normalized = databaseUrl.trim();
		if (!normalized.matches("^postgres(ql)?://.*")) {
			throw new IllegalArgumentException("DATABASE_URL は postgres:// または postgresql:// で始まる必要があります。");
		}
		URI uri = URI.create(normalized.replaceFirst("^postgres(ql)?://", "http://"));

		String userInfo = uri.getUserInfo();
		if (userInfo == null || userInfo.isBlank()) {
			throw new IllegalArgumentException("DATABASE_URL にユーザー情報がありません。");
		}
		String username;
		String password = "";
		int colon = userInfo.indexOf(':');
		if (colon < 0) {
			username = URLDecoder.decode(userInfo, StandardCharsets.UTF_8);
		} else {
			username = URLDecoder.decode(userInfo.substring(0, colon), StandardCharsets.UTF_8);
			password = URLDecoder.decode(userInfo.substring(colon + 1), StandardCharsets.UTF_8);
		}

		String host = uri.getHost();
		int port = uri.getPort() > 0 ? uri.getPort() : 5432;
		String path = uri.getPath();
		if (path == null || path.length() <= 1) {
			throw new IllegalArgumentException("DATABASE_URL にデータベース名がありません。");
		}
		String database = path.substring(1);

		String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database + "?sslmode=require";

		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(jdbcUrl);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setDriverClassName("org.postgresql.Driver");
		return ds;
	}
}
