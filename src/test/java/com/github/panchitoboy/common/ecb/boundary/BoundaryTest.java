package com.github.panchitoboy.common.ecb.boundary;

import com.github.panchitoboy.common.ecb.control.Control;
import com.github.panchitoboy.common.ecb.helper.ClassHelper;
import com.github.panchitoboy.common.example.BoundaryExample;
import com.github.panchitoboy.common.example.EntityExample;
import java.util.List;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BoundaryTest {

    @Deployment
    public static WebArchive deployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addClasses(Control.class)
                .addClasses(Boundary.class)
                .addClasses(ClassHelper.class)
                .addClasses(BoundaryExample.class)
                .addClasses(EntityExample.class)
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("arquillian-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(war.toString(true));
        return war;
    }

    static EntityExample t1;

    @Inject
    Boundary<EntityExample> boundary;

    @Test
    @InSequence(1)
    public void initialize() {
        Assert.assertNotNull("Control must be not null", boundary.getControl());
        Assert.assertThat("Boundary must be a BoundaryExample", boundary, CoreMatchers.instanceOf(BoundaryExample.class));
    }

    @Test
    @InSequence(2)
    public void create() {
        t1 = new EntityExample();
        t1.setName("Test 1");
        boundary.create(t1);

        Assert.assertNotNull("Id must be not null", t1.getId());
        Assert.assertNotNull("Version must be not null", t1.getVersion());

        EntityExample t2 = new EntityExample();
        t2.setName("Test 2");
        boundary.create(t2);

        Assert.assertNotNull("Id must be not null", t2.getId());
        Assert.assertNotNull("Version must be not null", t2.getVersion());

        EntityExample t3 = new EntityExample();
        t3.setName("Test 3");
        boundary.create(t3);

        Assert.assertNotNull("Id must be not null", t3.getId());
        Assert.assertNotNull("Version must be not null", t3.getVersion());
    }

    @Test
    @InSequence(3)
    public void createInvalid() throws Exception {
        try {
            boundary.create(new EntityExample());
        } catch (EJBException e) {
            Assert.assertThat(e, CoreMatchers.instanceOf(EJBException.class));
            Assert.assertThat(e.getCausedByException(), CoreMatchers.instanceOf(ConstraintViolationException.class));
        }
    }

    @Test
    @InSequence(4)
    public void findAll() {
        List<EntityExample> list = boundary.findAll();
        Assert.assertEquals("List must have 3 elements", 3, list.size());
    }

    @Test
    @InSequence(5)
    public void findById() {
        EntityExample t1FromDatabase = boundary.find(t1.getId());
        Assert.assertNotNull("T1FromDatabase must be not null", t1FromDatabase);
        compare(t1, t1FromDatabase);
    }

    @Test
    @InSequence(6)
    public void updateInvalid() throws Exception {
        EntityExample t1FromDatabase = boundary.find(t1.getId());
        Assert.assertNotNull("T1FromDatabase must be not null", t1FromDatabase);
        compare(t1, t1FromDatabase);

        t1FromDatabase.setName(null);

        try {
            boundary.update(t1FromDatabase);
        } catch (EJBException e) {
            Assert.assertThat(e, CoreMatchers.instanceOf(EJBException.class));
            Assert.assertThat(e.getCausedByException(), CoreMatchers.instanceOf(ConstraintViolationException.class));
        }
    }

    @Test
    @InSequence(7)
    public void update() throws Exception {
        EntityExample t1FromDatabase = boundary.find(t1.getId());
        Assert.assertNotNull("T1FromDatabase must be not null", t1FromDatabase);
        compare(t1, t1FromDatabase);

        t1FromDatabase.setName("Test 1 2");
        boundary.update(t1FromDatabase);

        t1FromDatabase = boundary.find(t1.getId());
        Assert.assertEquals("Must have the same id", t1FromDatabase.getId(), t1.getId());
        Assert.assertNotEquals("Must have a different version", t1FromDatabase.getVersion(), t1.getVersion());
        Assert.assertNotEquals("Must have a different name", t1FromDatabase.getName(), t1.getName());
    }

    @Test
    @InSequence(8)
    public void remove() throws Exception {
        boundary.remove(t1.getId());

        EntityExample t1FromDatabase = boundary.find(t1.getId());
        Assert.assertNull("T1FromDatabase must be null", t1FromDatabase);

        List<EntityExample> list = boundary.findAll();
        Assert.assertEquals("List must have 2 elements", 2, list.size());

    }

    private void compare(EntityExample expected, EntityExample actual) {
        Assert.assertEquals("Must have the same id", expected.getId(), actual.getId());
        Assert.assertEquals("Must have the same version", expected.getVersion(), actual.getVersion());
        Assert.assertEquals("Must have the same name", expected.getName(), actual.getName());
    }
}
