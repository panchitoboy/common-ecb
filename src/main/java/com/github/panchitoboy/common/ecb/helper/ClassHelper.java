package com.github.panchitoboy.common.ecb.helper;

import java.lang.reflect.ParameterizedType;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

public class ClassHelper<T> {

    private final Class<T> injectionClass;

    @Inject
    public ClassHelper(InjectionPoint ip) {
        ParameterizedType type = (ParameterizedType) ip.getType();
        Class clazz = (Class) type.getActualTypeArguments()[0];
        this.injectionClass = clazz;
    }

    public Class<T> getInjectionClass() {
        return injectionClass;
    }

}
