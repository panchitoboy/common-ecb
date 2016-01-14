package com.github.panchitoboy.common.ecb.control;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.Valid;

public class Control<T> {

    @PersistenceContext
    EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    public T find(Class<T> entityClass, Serializable id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll(Class<T> entityClass) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> c = cq.from(entityClass);
        cq.select(c);
        return getEntityManager().createQuery(cq).getResultList();
    }

    public void create(@Valid T instance) {
        getEntityManager().persist(instance);
    }

    public void update(@Valid T instance) {
        getEntityManager().merge(instance);
    }

    public void remove(@Valid T instance) {
        getEntityManager().remove(instance);
    }
}
