package com.torryharris.employee.crud.dao;

import com.torryharris.employee.crud.model.Employee;
import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public interface Dao<T> {

  Promise<List<T>> get(String id);

  Promise<List<T>> getAll();

//  void login(String username,String password);

  Promise<List<Employee>> login(String username,String password);


   void save(Employee e);

 void update(Employee e);

  void delete(String id);

}
