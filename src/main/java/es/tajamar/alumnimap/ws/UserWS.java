package es.tajamar.alumnimap.ws;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import es.tajamar.alumnimap.bean.AlumniUser;
import es.tajamar.alumnimap.service.GoogleUserService;

@Path("/user")
public class UserWS {
	protected static final String CHARSET_UTF8 = ";charset=UTF-8";

	protected GoogleUserService googleUserService = new GoogleUserService();

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response create(@Context HttpServletRequest request, @Context HttpServletResponse response,
			@FormParam("user") String json) {
		if (!googleUserService.isGoogleAdmin()) {
			return Response.status(Status.UNAUTHORIZED).entity("Not authorized.").build();
		}
		if (json == null || json.isEmpty()) {
			return Response.status(Status.BAD_REQUEST).entity("Bad request.").build();
		}
		AlumniUser alumniuser = new Gson().fromJson(json, AlumniUser.class);
		Key<AlumniUser> key = ObjectifyService.ofy().save().entity(alumniuser).now();
		return Response.ok().entity(new Gson().toJson(alumniuser)).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{email}")
	public Response read(@PathParam("email") String email) {
		if (!googleUserService.isGoogleAdmin()) {
			return Response.status(Status.UNAUTHORIZED).entity("Not authorized.").build();
		}
		Key<AlumniUser> key = Key.create(AlumniUser.class, email);
		AlumniUser alumniuser = ObjectifyService.ofy().load().key(key).now();
		if (alumniuser != null) {
			return Response.ok().entity(alumniuser).build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("Not found.").build();
		}
	}

	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response delete(@QueryParam("email") String email) {
		if (!googleUserService.isGoogleAdmin()) {
			return Response.status(Status.UNAUTHORIZED).entity("Not authorized.").build();
		}
		Key<AlumniUser> key = Key.create(AlumniUser.class, email);
		AlumniUser alumniuser = ObjectifyService.ofy().load().key(key).now();
		if (alumniuser != null) {
			ObjectifyService.ofy().delete().key(key).now();
			return Response.ok().build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("Not found.").build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + CHARSET_UTF8)
	public Response list() {
		if (!googleUserService.isGoogleAdmin()) {
			return Response.status(Status.UNAUTHORIZED).entity("Not authorized.").build();
		}
		List<AlumniUser> users = ObjectifyService.ofy().load().type(AlumniUser.class).list();
		if (users != null) {
			return Response.ok().entity(new Gson().toJson(users)).build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("Not found.").build();
		}
	}
}
