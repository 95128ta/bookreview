package com.bookreview;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class WebSmokeTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void rankingIsPublic() throws Exception {
		mockMvc.perform(get("/ranking")).andExpect(status().isOk());
	}

	@Test
	void actuatorHealthIsPublic() throws Exception {
		mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
	}

	@Test
	void profileRequiresLogin() throws Exception {
		mockMvc.perform(get("/profile"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrlPattern("**/login"));
	}

	@Test
	@WithMockUser(roles = "USER")
	void adminRouteForbiddenForNormalUser() throws Exception {
		mockMvc.perform(get("/admin/books")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void adminRouteOkForAdmin() throws Exception {
		mockMvc.perform(post("/admin/users/promote")
				.with(csrf())
				.param("loginId", "no-such-user@example.com"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/books"));
	}

	@Test
	@WithMockUser(roles = "USER")
	void promoteRouteForbiddenForNormalUser() throws Exception {
		mockMvc.perform(post("/admin/users/promote")
				.with(csrf())
				.param("loginId", "someone@example.com"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void promoteRouteReachableForAdmin() throws Exception {
		mockMvc.perform(post("/admin/users/promote")
				.with(csrf())
				.param("loginId", "no-such-user@example.com"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/books"));
	}
}

