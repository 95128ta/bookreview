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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import com.bookreview.security.LoginUserPrincipal;

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
	void adminUsersListOkForAdmin() throws Exception {
		LoginUserPrincipal admin = new LoginUserPrincipal(1, "admin@bookreview.local", "hash", "管理者", true);
		mockMvc.perform(get("/admin/users").with(user(admin))).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = "USER")
	void adminUsersListForbiddenForNormalUser() throws Exception {
		mockMvc.perform(get("/admin/users")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void promoteRouteReachableForAdmin() throws Exception {
		mockMvc.perform(post("/admin/users/99999/promote").with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/users"));
	}

	@Test
	@WithMockUser(roles = "USER")
	void promoteRouteForbiddenForNormalUser() throws Exception {
		mockMvc.perform(post("/admin/users/1/promote").with(csrf()))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "USER")
	void demoteRouteForbiddenForNormalUser() throws Exception {
		mockMvc.perform(post("/admin/users/1/demote").with(csrf()))
				.andExpect(status().isForbidden());
	}

	@Test
	void demoteRouteReachableForAdmin() throws Exception {
		LoginUserPrincipal admin = new LoginUserPrincipal(1, "admin@bookreview.local", "hash", "管理者", true);
		mockMvc.perform(post("/admin/users/99999/demote")
				.with(csrf())
				.with(user(admin)))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/users"));
	}
}
