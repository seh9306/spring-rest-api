package kr.ac.hansung.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import kr.ac.hansung.exception.UserDuplicatedException;
import kr.ac.hansung.exception.UserNotFoundException;
import kr.ac.hansung.model.User;
import kr.ac.hansung.service.UserService;

@RestController // @Controller + @ResponseBody
@RequestMapping("/api")
public class RestAPIController {

	@Autowired
	UserService userService;

	// -- Retrieve All Users
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ResponseEntity<List<User>> listAllUsers() { // header, body,
														// HTTP.status
		List<User> users = userService.findAllUsers();
		
		if (users.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
		/*
		 * header : x body : users HTTP.status : HttpStatus.OK
		 */
	}

	// -- Retrieve Single Users
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	public ResponseEntity<User> getUser(@PathVariable("id") long id) {

		User user = userService.findById(id);
		if (user == null) {
			throw new UserNotFoundException(id);
		}
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	// -- Create a Users
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public ResponseEntity<Void> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {

		if (userService.isUserExist(user)) {
			throw new UserDuplicatedException(user.getName());
		}
		userService.saveUser(user);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/api/users/{id}").buildAndExpand(user.getId()).toUri());
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	// --- Update a User
	@RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
	public ResponseEntity<User> updateUser(@PathVariable("id") long id, @RequestBody User user) {// body에
																									// 있는
																									// json
																									// 정보를
																									// 객체로
																									// 컨버팅
		User currentUser = userService.findById(id);

		if (currentUser == null) {
			// to do list: exception
			throw new UserNotFoundException(id);
		}

		currentUser.setName(user.getName());
		currentUser.setAge(user.getAge());
		currentUser.setSalary(user.getSalary());

		userService.updateUser(currentUser);
		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}

	// --- Delete a User
	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<User> deleteUser(@PathVariable("id") long id) {// body에
																			// 있는
																			// json
																			// 정보를
																			// 객체로
																			// 컨버팅
		User currentUser = userService.findById(id);

		if (currentUser == null) {
			// to do list: exception
			throw new UserNotFoundException(id);
		}
		userService.deleteUserById(id);

		return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
	}

	// --- Delete All User
	@RequestMapping(value = "/users", method = RequestMethod.DELETE)
	public ResponseEntity<User> deleteAllUser() {// body에 있는 json 정보를 객체로 컨버팅
		userService.deleteAllUsers();
		return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
	}

}
