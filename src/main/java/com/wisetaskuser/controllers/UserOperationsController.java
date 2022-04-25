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

@RestController
@CrossOrigin(origins = {"http://localhost:8025", "http://localhost:8026", "http://localhost:8027"})
@RequestMapping(value = "/users")
public class UserOperationsController {

	@Autowired
	private UsersServices usersService;
	
	@RequestMapping(value = "/validateUser", method = RequestMethod.POST)
	public Map<String, Boolean> validateUser(@RequestBody User loginCredentials) {
		return usersService.areCredentialsValid(loginCredentials) 
				? Collections.singletonMap("valid", true)
				: Collections.singletonMap("valid", false);
	}
	
	@RequestMapping(value = "/getUser", method = RequestMethod.POST)
	public User getUser(@RequestBody User loginCredentials) {
		return usersService.getUser(loginCredentials) != null 
				? usersService.getUser(loginCredentials)
				: User.builder()
					.userId(-1)
					.build();
	}
	
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
	
	@RequestMapping(value = "/addUserAccount", method = RequestMethod.POST)
	public void addUserAccount(@RequestBody User accountDetails) {
		usersService.addAccount(accountDetails);
	}
	
	@RequestMapping(value = "/isUsernameUnique", method = RequestMethod.POST)
	public Map<String, Boolean> isUsernameUnique(@RequestBody Map<String, String> username) {
		return usersService.isUsernameUnique(username.get("username"))
				? Collections.singletonMap("unique", true)
				: Collections.singletonMap("unique", false);
	}
	
	/*
	 * I suppose the process for checking whether there are mails that needs to be
	 * sent, is a general one that is associated with the users. That is why it is
	 * placed in this class.
	 */
	
	@RequestMapping(value = "/getEmailsThatNeedsToBeSentToday", method = RequestMethod.GET)
	public Tasks getEmailsThatNeedsToBeSent() {
		return usersService.getTasksThatNeedSending();
	}
	
}
