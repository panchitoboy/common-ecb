package com.github.panchitoboy.common.example;

import com.github.panchitoboy.common.ecb.boundary.RestBoundary;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

@Path("test")
@Stateless
public class ResourceExample extends RestBoundary<EntityExample> {

}
