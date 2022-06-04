package com.wisetaskuser.controllers;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.wisetaskuser.entities.Tasks;
import com.wisetaskuser.entities.User;
import com.wisetaskuser.services.UsersServices;

/**
 * The rest controller (resource class) that contains url-endpoints for all the operations
 * that are general (for instance, the login validation takes place for users of
 * any account type).
 * @author Theofanis Gkoufas
 *
 */
@RestController
@CrossOrigin(origins = {"http://localhost:8025", "http://localhost:8026", "http://localhost:8027"})
@RequestMapping(value = "/users")
public class UserOperationsController {

	@Autowired
	private UsersServices usersService;
	
	/**
	 * Validates the credentials of a user.
	 * @param loginCredentials A user's credentials consists of a username and a password.
	 * @return True or false depending on whether the credentials are valid or not.
	 */
	@RequestMapping(value = "/validateUser", method = RequestMethod.POST)
	public Map<String, Boolean> validateUser(@RequestBody User loginCredentials) {
		return usersService.areCredentialsValid(loginCredentials) 
				? Collections.singletonMap("valid", true)
				: Collections.singletonMap("valid", false);
	}
	
	/**
	 * Retrieves a user based on his/her login credentials. 
	 * @param loginCredentials A user's credentials consists of a username and a password.
	 * @return A user reference corresponding to the one having the given credentials or
	 * -1 if the user does not exist.
	 */
	@RequestMapping(value = "/getUser", method = RequestMethod.POST)
	public User getUser(@RequestBody User loginCredentials) {
		return usersService.getUser(loginCredentials) != null 
				? usersService.getUser(loginCredentials)
				: User.builder()
					.userId(-1)
					.build();
	}
	
	/**
	 * Retrieves a user given the id.
	 * @param id The id that corresponds to a specific user.
	 * @return The user whose id matches the one given as an argument or
	 * -1 in the case that the user does not exist.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public User users(@PathVariable int id) {
		User user;
		try {
			user = usersService.getUser(id);
		} catch (NoSuchElementException e) {
			user = User.builder().userId(-1).build();
		}
		return user;
	}
	
	/**
	 * Creates a new user account.
	 * @param accountDetails The required details for the purpose of creating
	 * an account.
	 */
	@RequestMapping(value = "/addUserAccount", method = RequestMethod.POST)
	public void addUserAccount(@RequestBody User accountDetails) {
		usersService.addAccount(accountDetails);
	}
	
	/**
	 * Checks whether a username is unique or not (meaning it hasn't been taken by
	 * another account).
	 * @param username The name whose uniqueness we want to check.
	 * @return True or false depending of whether the username is unique or not.
	 */
	@RequestMapping(value = "/isUsernameUnique", method = RequestMethod.POST)
	public Map<String, Boolean> isUsernameUnique(@RequestBody Map<String, String> username) {
		return usersService.isUsernameUnique(username.get("username"))
				? Collections.singletonMap("unique", true)
				: Collections.singletonMap("unique", false);
	}
	
	/**
	 * Retrieves all the emails that needs to be sent at the present day.
	 * @return The list of all the emails (meaning the tasks that should be sent
	 * in an email format).
	 */
	@RequestMapping(value = "/getEmailsThatNeedsToBeSentToday", method = RequestMethod.GET)
	public Tasks getEmailsThatNeedsToBeSent() {
		return usersService.getTasksThatNeedSending();
	}
	
}
