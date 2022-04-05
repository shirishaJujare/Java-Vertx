package com.torryharris.employee.crud.verticles;

import com.torryharris.employee.crud.controller.EmployeeController;
import com.torryharris.employee.crud.dao.impl.EmployeeJdbcDao;
import com.torryharris.employee.crud.model.Employee;
import com.torryharris.employee.crud.model.Response;
import com.torryharris.employee.crud.util.ConfigKeys;
import com.torryharris.employee.crud.util.PropertyFileUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ApiServer extends AbstractVerticle {
  private static final Logger logger = LogManager.getLogger(ApiServer.class);
  private static Router router;
  private EmployeeController employeeController;

  List<Employee> employees = new ArrayList<>();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    employeeController = new EmployeeController(vertx);
    router = Router.router(vertx);

    // Attach a BodyHandler to parse request body and set upload to false
   router.route().handler(BodyHandler.create(false));

      router.post("/employee")
      .handler(routingContext ->  {
        JsonObject reqJo = new JsonObject();

        Employee employee = Json.decodeValue(routingContext.getBody(), Employee.class);
        HttpServerResponse serverResponse = routingContext.response();
        employeeController.addEmployee(employee).future()
         .onSuccess(response -> sendResponse(routingContext, response));
        System.out.println(routingContext.getBodyAsJson());
        employees.add(employee);
       serverResponse.end(" Employee added successfully...");
        routingContext.response().end(Json.encodePrettily(employees));
      });

    router.get("/employees")
      .handler(routingContext -> employeeController.getEmployees().future()
        .onSuccess(response -> sendResponse(routingContext, response))
      );

    router.get("/employee/:id")
      .handler(routingContext ->{
          String id= routingContext.request().getParam("id");
          employeeController.getEmployeebyId(id).future()
            .onSuccess(response -> sendResponse(routingContext,response));
        }
      );

    router.put("/employee/:id")
      .handler(routingContext ->{
        Employee employee = Json.decodeValue(routingContext.getBody(), Employee.class);
          String id= routingContext.request().getParam("id");
        HttpServerResponse serverResponse = routingContext.response();
        employeeController.updateEmpId(employee).future()
            .onSuccess(response -> sendResponse(routingContext,response));
        routingContext.response().end(Json.encodePrettily(employee));

        }
      );
    
    router.delete("/employee/:id")
      .handler(routingContext -> {
        String  id = routingContext.request().getParam("id");
        HttpServerResponse serverResponse = routingContext.response();
        employeeController.delEmpbyId(id).future()
          .onSuccess(response -> sendResponse(routingContext, response));
        serverResponse.end(" Employee deleted with ID" + id);
      });



    HttpServerOptions options = new HttpServerOptions().setTcpKeepAlive(true);
    vertx.createHttpServer(options)
      .exceptionHandler(logger::catching)
      .requestHandler(router)
      .listen(Integer.parseInt(PropertyFileUtils.getProperty(ConfigKeys.HTTP_SERVER_PORT)))
      .onSuccess(httpServer -> {
        logger.info("Server started on port {}", httpServer.actualPort());
        startPromise.tryComplete();
      })
      .onFailure(startPromise::tryFail);
  }

  private void sendResponse(RoutingContext routingContext, Response response) {
    response.getHeaders().stream()
      .forEach(entry -> routingContext.response().putHeader(entry.getKey(), entry.getValue().toString()));
    routingContext.response().setStatusCode(response.getStatusCode())
      .end(response.getResponseBody());
  }
}
