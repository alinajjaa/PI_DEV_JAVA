package org.agritrace.interfaces;

import java.util.List;

public interface IService<T> {

    void addEntity(T t);
    void deleteEntity(T t);
    void updateEntity(T t, int id);
    List<T> getAllData();

}
