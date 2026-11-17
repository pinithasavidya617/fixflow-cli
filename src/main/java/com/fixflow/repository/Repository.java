package com.fixflow.repository;

import java.util.List;


public interface Repository<T> {

    void add(T item);

    T findById(int id);


    List<T> findAll();


    void update(T item);


    boolean delete(int id);

    void save();


    void load();
}
