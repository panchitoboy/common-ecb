package com.github.panchitoboy.common.ecb.boundary;

import java.io.IOException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public abstract class RestBoundary<T> extends Boundary<T> {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() throws IOException {
        List<T> list = super.findAll();
        return Response.status(200).entity(list).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public T find(@PathParam("id") long id) {
        T element = super.find(id);
        return element;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public T create(T instance) {
        super.create(instance);
        return instance;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public T update(T instance) {
        return super.update(instance);
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public T remove(@PathParam("id") long id) {
        return super.remove(id);
    }

}
