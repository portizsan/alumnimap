package es.tajamar.alumnimap.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import es.tajamar.alumnimap.bean.Alumni;
import es.tajamar.alumnimap.service.GoogleUserService;

@Path("/alumni")
public class AlumniWS {
	protected static final String CHARSET_UTF8 = ";charset=UTF-8";

	protected GoogleUserService googleUserService = new GoogleUserService();

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response create(@Context HttpServletRequest request, @Context HttpServletResponse response,
			@FormParam("alumni") String json) {
		if (!googleUserService.isAdmin()) {
			return Response.status(Status.UNAUTHORIZED).entity("Not authorized.").build();
		}
		if (json == null || json.isEmpty()) {
			return Response.status(Status.BAD_REQUEST).entity("Bad request.").build();
		}
		Alumni alumni = new Gson().fromJson(json, Alumni.class);
		Key<Alumni> key = ObjectifyService.ofy().save().entity(alumni).now();
		alumni.setId(key.getId());
		return Response.ok().entity(new Gson().toJson(alumni)).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}")
	public Response read(@PathParam("id") Long id) {
		if (!googleUserService.isAdmin()) {
			return Response.status(Status.UNAUTHORIZED).entity("Not authorized.").build();
		}
		Key<Alumni> key = Key.create(Alumni.class, id);
		Alumni alumni = ObjectifyService.ofy().load().key(key).now();
		if (alumni != null) {
			return Response.ok().entity(new Gson().toJson(alumni)).build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("Not found.").build();
		}

	}

	@PUT
	@Produces({ MediaType.APPLICATION_JSON })
	public Response update(@FormParam("alumni") String json) {
		if (!googleUserService.isAdmin()) {
			return Response.status(Status.UNAUTHORIZED).entity("Not authorized.").build();
		}
		if (json == null || json.isEmpty()) {
			return Response.status(Status.BAD_REQUEST).entity("Bad request.").build();
		}
		Alumni alumni = new Gson().fromJson(json, Alumni.class);
		Key<Alumni> key = Key.create(Alumni.class, alumni.getId());
		Alumni oldAlumni = ObjectifyService.ofy().load().key(key).now();
		if (oldAlumni != null) {
			ObjectifyService.ofy().save().entity(alumni).now();
		} else {
			return Response.status(Status.NOT_FOUND).entity("Not found.").build();
		}
		return Response.ok().entity(new Gson().toJson(alumni)).build();
	}

	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response delete(@QueryParam("id") Long id) {
		if (!googleUserService.isAdmin()) {
			return Response.status(Status.UNAUTHORIZED).entity("Not authorized.").build();
		}
		Key<Alumni> key = Key.create(Alumni.class, id);
		Alumni alumni = ObjectifyService.ofy().load().key(key).now();
		if (alumni != null) {
			ObjectifyService.ofy().delete().key(key).now();
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "deleted");
			return Response.ok().entity(new Gson().toJson(result)).build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("Not found.").build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + CHARSET_UTF8)
	public Response list() {
		List<Alumni> alumnis = ObjectifyService.ofy().load().type(Alumni.class).list();
		if (alumnis != null) {
			return Response.ok().entity(new Gson().toJson(alumnis)).build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("Not found.").build();
		}
	}
}
