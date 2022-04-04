package com.torryharris.employee.crud.dao;

import com.torryharris.employee.crud.model.Employee;
import io.vertx.core.Promise;

import java.util.List;

public interface Dao<T> {

  Promise<List<T>> get(String id);

  Promise<List<T>> getAll();

   void save(Employee e);

 void update(String id);

  void delete(String id);

}
