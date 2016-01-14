package com.github.panchitoboy.common.ecb.boundary;

import com.github.panchitoboy.common.ecb.control.Control;
import com.github.panchitoboy.common.ecb.helper.ClassHelper;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.validation.Valid;

public abstract class Boundary<T> {

    @Inject
    ClassHelper<T> entityClass;

    @Inject
    Control<T> control;

    public Control<T> getControl() {
        return control;
    }

    public T find(Serializable id) {
        return getControl().find(entityClass.getInjectionClass(), id);
    }

    public List<T> findAll() {
        return getControl().findAll(entityClass.getInjectionClass());
    }

    public T create(@Valid T entity) {
        getControl().create(entity);
        return entity;
    }

    public T update(@Valid T entity) {
        getControl().update(entity);
        return entity;
    }

    public T remove(Serializable id) {
        T instance = find(id);
        getControl().remove(instance);
        return instance;
    }
}
