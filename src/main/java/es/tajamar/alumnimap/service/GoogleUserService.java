package es.tajamar.alumnimap.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import es.tajamar.alumnimap.bean.AlumniUser;
import es.tajamar.alumnimap.bean.ROLE;

public class GoogleUserService {

	public boolean isAdmin() {
		UserService googleUserService = UserServiceFactory.getUserService();
		User gu = googleUserService.getCurrentUser();
		if (gu == null) {
			return false;
		} else {
			Key<AlumniUser> key = Key.create(AlumniUser.class, gu.getEmail());
			AlumniUser aUser = ObjectifyService.ofy().load().key(key).now();
			return aUser != null ? (aUser.getRole().equals(ROLE.ADMIN)) : false;
		}
	}

	public boolean isGoogleAdmin() {
		UserService googleUserService = UserServiceFactory.getUserService();
		return googleUserService.isUserAdmin();
	}
}
