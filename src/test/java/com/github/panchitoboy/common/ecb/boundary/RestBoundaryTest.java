package com.github.panchitoboy.common.ecb.boundary;

import com.github.panchitoboy.common.ecb.control.Control;
import com.github.panchitoboy.common.ecb.helper.ClassHelper;
import com.github.panchitoboy.common.example.BoundaryExample;
import com.github.panchitoboy.common.example.EntityExample;
import com.github.panchitoboy.common.example.JAXRSConfigurationExample;
import com.github.panchitoboy.common.example.ResourceExample;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class RestBoundaryTest {

    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(Control.class)
                .addClasses(Boundary.class)
                .addClasses(RestBoundary.class)
                .addClasses(ClassHelper.class)
                .addClasses(JAXRSConfigurationExample.class, ResourceExample.class)
                .addClasses(BoundaryExample.class)
                .addClasses(EntityExample.class)
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("arquillian-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    private WebTarget target;

    static Long t1Id;

    @ArquillianResource
    private URL base;

    @Before
    public void setUp() throws MalformedURLException {

        Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
        target = client.target(URI.create(new URL(base, "resources/test").toExternalForm()));
        target.register(EntityExample.class);
    }

    @Test
    @InSequence(1)
    public void create() throws IOException {

        EntityExample t1 = new EntityExample();
        t1.setName("Test 1");

        Response r1 = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(Entity.entity(t1, MediaType.APPLICATION_JSON));
        Assert.assertEquals("Response code must be 200", 200, r1.getStatus());

        EntityExample o1 = r1.readEntity(EntityExample.class);

        Assert.assertNotEquals("Id must be not null", null, o1.getId());
        Assert.assertNotEquals("Version must be not null", null, o1.getVersion());
        t1Id = o1.getId();

        EntityExample t2 = new EntityExample();
        t2.setName("Test 2");
        Response r2 = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(Entity.entity(t2, MediaType.APPLICATION_JSON));
        Assert.assertEquals("Response code must be 200", 200, r2.getStatus());

        EntityExample o2 = r2.readEntity(EntityExample.class);

        Assert.assertNotEquals("Id must be not null", null, o2.getId());
        Assert.assertNotEquals("Version must be not null", null, o2.getVersion());

        EntityExample t3 = new EntityExample();
        t3.setName("Test 3");
        Response r3 = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(Entity.entity(t3, MediaType.APPLICATION_JSON));
        Assert.assertEquals("Response code must be 200", 200, r3.getStatus());

        EntityExample o3 = r3.readEntity(EntityExample.class);

        Assert.assertNotEquals("Id must be not null", null, o3.getId());
        Assert.assertNotEquals("Version must be not null", null, o3.getVersion());

    }

    @Test
    @InSequence(3)
    public void list() {
        List<EntityExample> list = target.request().accept(MediaType.APPLICATION_JSON).get(new GenericType<List<EntityExample>>() {
        });
        Assert.assertEquals("List must have 3 elements", 3, list.size());

        for (EntityExample object : list) {
            Assert.assertNotNull("id must be not null", object.getId());
            Assert.assertNotNull("version must be not null", object.getVersion());
            Assert.assertNotNull("name must be not null", object.getName());
        }

    }

    @Test
    @InSequence(4)
    public void get() {
        EntityExample t1FromDatabase = target.path("/" + t1Id).request().accept(MediaType.APPLICATION_JSON).get(EntityExample.class);
        Assert.assertNotNull("T1FromDatabase must be not null", t1FromDatabase);
        Assert.assertNotEquals("Id must be not null", null, t1FromDatabase.getId());
        Assert.assertEquals("Id must be the same", t1Id.longValue(), t1FromDatabase.getId());
        Assert.assertNotEquals("Version must be not null", null, t1FromDatabase.getVersion());
    }

    @Test
    @InSequence(5)
    public void update() throws Exception {
        EntityExample before = target.path("/" + t1Id).request().accept(MediaType.APPLICATION_JSON).get(EntityExample.class);
        Assert.assertNotNull("T1FromDatabase must be not null", before);

        EntityExample t1FromDatabase = new EntityExample();
        t1FromDatabase.setId(before.getId());
        t1FromDatabase.setVersion(before.getVersion());
        t1FromDatabase.setName("Test 1 2");
        target.request().accept(MediaType.APPLICATION_JSON).put(Entity.json(t1FromDatabase));

        EntityExample after = target.path("/" + t1Id).request().accept(MediaType.APPLICATION_JSON).get(EntityExample.class);

        Assert.assertEquals("Must have the same id", before.getId(), after.getId());
        Assert.assertNotEquals("Must have a different version", before.getVersion(), after.getVersion());
        Assert.assertNotEquals("Must have a different name", before.getName(), after.getName());
    }

    @Test
    @InSequence(7)
    public void remove() {
        target.path("/" + t1Id).request().accept(MediaType.APPLICATION_JSON).delete();

        List<EntityExample> list = target.request().accept(MediaType.APPLICATION_JSON).get(new GenericType<List<EntityExample>>() {
        });
        Assert.assertEquals("List must have 2 elements", 2, list.size());
    }
}
