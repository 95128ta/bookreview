package com.bookreview.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import com.bookreview.repository.AppUserRepository;

@Configuration
@Profile("dev")
public class DevAdminBootstrapConfig {

	@Bean
	ApplicationRunner demoAdminBootstrapRunner(
			AppUserRepository appUserRepository,
			@Value("${app.bootstrap.demo-admin-login-id:demo-user@example.com}") String demoAdminLoginId) {
		return args -> promoteDemoUserToAdmin(appUserRepository, demoAdminLoginId);
	}

	@Transactional
	void promoteDemoUserToAdmin(AppUserRepository appUserRepository, String loginId) {
		appUserRepository.findByLoginId(loginId)
				.ifPresent(user -> {
					if (user.getIsAdmin() == null || user.getIsAdmin() == 0) {
						user.setIsAdmin(1);
						appUserRepository.save(user);
					}
				});
	}
}


