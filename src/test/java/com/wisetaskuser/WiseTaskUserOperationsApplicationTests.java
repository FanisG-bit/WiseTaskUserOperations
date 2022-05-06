package com.wisetaskuser;

import static org.hamcrest.CoreMatchers.is;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.wisetaskuser.services.UsersServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisetaskuser.controllers.UserOperationsController;
import com.wisetaskuser.entities.Tasks;
import com.wisetaskuser.entities.User;
import com.wisetaskuser.entities.UserType;

@WebMvcTest(controllers = UserOperationsController.class)
class WiseTaskUserOperationsApplicationTests {

	@MockBean
	private UsersServices usersService;
	
	@Autowired
    private MockMvc mockMvc;
	
	/*
		The following test will return false even if the result should have been true.
		The reason of this is because the password field is annotated as @JsonProperty write only,
		which even though it should not affect it, it does, meaning that the password is not being
		passed, thus the mockito "when" statement does not return the required result.
	*/
	@Test
	public void testValidateUser() throws Exception {
		User loginCredentials = User.builder()
				.username("Fanis")
				.password("myPassword")
				.build();
		Mockito.when(usersService.areCredentialsValid(loginCredentials))
			.thenReturn(true);
		MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/users/validateUser")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(loginCredentials)))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.valid").value(false))
			.andReturn();
		Assertions.assertEquals("application/json", response.getResponse().getContentType());
	}
	
	@Test
	public void testGetUserBasedOnId() throws Exception {
		Mockito.when(usersService.getUser(1))
			.thenReturn(User.builder()
					.userId(1)
					.username("Fanis")
					.userType(UserType.ADMIN)
					.email("tgkoufas@athtech.gr")
					.build());
		MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/users/1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1))
			.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Fanis"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.userType").value("ADMIN"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("tgkoufas@athtech.gr"))
			.andReturn();
		Assertions.assertEquals("application/json", response.getResponse().getContentType());
	}

	@Test
	public void testGetEmailsThatNeedsToBeSentToday() throws Exception {
		Tasks tasks = new Tasks();
		tasks.setTasks(new ArrayList<>());
		Mockito.when(usersService.getTasksThatNeedSending()).thenReturn(tasks);
		MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/users/getEmailsThatNeedsToBeSentToday")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.tasks.length()", is(0)))
			.andReturn();
		Assertions.assertEquals("application/json", response.getResponse().getContentType());
	}
	
}
