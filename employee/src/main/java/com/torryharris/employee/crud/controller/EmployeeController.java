package com.torryharris.employee.crud.controller;

import com.torryharris.employee.crud.dao.Dao;
import com.torryharris.employee.crud.dao.impl.EmployeeJdbcDao;
import com.torryharris.employee.crud.model.Employee;
import com.torryharris.employee.crud.model.Response;
import com.torryharris.employee.crud.util.Utils;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class EmployeeController {
  private static final Logger LOGGER = LogManager.getLogger(EmployeeController.class);
  private final Dao<Employee> employeeDao;

  public EmployeeController(Vertx vertx) {
    employeeDao = new EmployeeJdbcDao(vertx);
  }


  public Promise<Response> addEmployee(Employee e){
    List<Employee> employees = new ArrayList<>();
   Promise<Response> responsePromise = Promise.promise();
//    Response response = new Response();
    employeeDao.save(e);

    return responsePromise;
  }


  public Promise<Response> getEmployees() {
    Promise<Response> responsePromise = Promise.promise();
    Response response = new Response();
    employeeDao.getAll()
      .future()
      .onSuccess(employees -> {
        response.setStatusCode(200)
          .setResponseBody(Json.encode(employees));
        responsePromise.tryComplete(response);
      })
      .onFailure(throwable -> {
        response.setStatusCode(500)
          .setResponseBody(Utils.getErrorResponse(throwable.getMessage()).toString());
        responsePromise.tryComplete(response);
        LOGGER.catching(throwable);
      });
    return responsePromise;
  }

  public Promise<Response> getEmployeebyId(String id) {
    Promise<Response> responsePromise = Promise.promise();
    Response response = new Response();
//    String id=new String();
    LOGGER.info(id);
    employeeDao.get(id)
      .future()
      .onSuccess(employees -> {
        response.setStatusCode(200)
          .setResponseBody(Json.encode(employees));
        responsePromise.tryComplete(response);
        LOGGER.info("Employee received with ID : " + id);
      })
      .onFailure(throwable -> {
        response.setStatusCode(200)
          .setResponseBody(Utils.getErrorResponse(throwable.getMessage()).toString());
        responsePromise.tryComplete(response);
        LOGGER.catching(throwable);
      });
    return responsePromise;
  }

  public Promise<Response> delEmpbyId(String id) {
    Promise<Response> responsePromise = Promise.promise();
    Response response = new Response();
    LOGGER.info("hii");
//    String id=new String();
    employeeDao.delete(id);
LOGGER.info(id);

    return responsePromise;
  }

  public Promise<Response> updateEmpId(String id) {
    Promise<Response> responsePromise = Promise.promise();
    Response response = new Response();
    employeeDao.update(id);
    LOGGER.info("Employee updated with ID :  "  + id);
    return responsePromise;
  }


}
