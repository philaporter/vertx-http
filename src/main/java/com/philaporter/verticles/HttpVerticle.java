package com.philaporter.verticles;

import com.philaporter.Main;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/** @author Philip Porter */
public class HttpVerticle extends AbstractVerticle {

  private static Logger log = null;
  private Map<String, JsonObject> employees = new HashMap<>();

  private void addEmployee(JsonObject employee) {
    employees.put(employee.getString("empId"), employee);
  }

  private void mockData() {
    addEmployee(
        new JsonObject()
            .put("empId", "a1b2c3d4")
            .put("fName", "Philip")
            .put("lName", "Porter")
            .put("salary", 100000));
    addEmployee(
        new JsonObject()
            .put("empId", "a2b3c4d5")
            .put("fName", "Alan")
            .put("lName", "Jones")
            .put("salary", 120000));
  }

  @Override
  public void start() {
    mockData();
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.get("/employees").handler(this::handleGetEmployees);
    router.get("/employees/:empId").handler(this::handleGetEmployee);
    router.post("/employees/:empId").handler(this::handleAddEmployee);
    router.delete("/employees/:empId").handler(this::handleRemoveEmployee);
    router.put("/employees/:empId").handler(this::handleUpdateAddEmployee);
    vertx.createHttpServer().requestHandler(router::accept).listen(Main.config.get("http.port"));
  }

  private void handleGetEmployees(RoutingContext routingContext) {
    JsonArray json = new JsonArray();
    employees.forEach((k, v) -> json.add(v));
    routingContext
        .response()
        .putHeader("content-type", "application/json")
        .end(json.encodePrettily());
  }

  private void handleGetEmployee(RoutingContext routingContext) {
    log.info("Bitches");
    String empId = routingContext.request().getParam("empId");
    HttpServerResponse response = routingContext.response();
    if (empId != null) {
      response
          .putHeader("content-type", "application/json")
          .end(employees.get(empId).encodePrettily());
    } else {
      sendError(400, response);
    }
  }

  private void handleAddEmployee(RoutingContext routingContext) {
    String empId = routingContext.request().getParam("empId");
    HttpServerResponse response = routingContext.response();
    if (empId != null) {
      JsonObject employee = routingContext.getBodyAsJson();
      if (employee != null) {
        employees.put(empId, employee);
        response.end();
      } else {
        sendError(400, response);
      }
    } else {
      sendError(400, response);
    }
  }

  private void handleUpdateAddEmployee(RoutingContext routingContext) {
    String empId = routingContext.request().getParam("empId");
    HttpServerResponse response = routingContext.response();
    if (empId != null) {
      JsonObject employee = routingContext.getBodyAsJson();
      employees.replace(empId, employee);
      response.end();
    } else {
      sendError(400, response);
    }
  }

  private void handleRemoveEmployee(RoutingContext routingContext) {
    String empId = routingContext.request().getParam("empId");
    HttpServerResponse response = routingContext.response();
    if (empId != null) {
      if (empId != null) {
        employees.remove(empId);
        response.end();
      } else {
        sendError(400, response);
      }
    } else {
      sendError(400, response);
    }
  }

  private void sendError(int statusCode, HttpServerResponse response) {
    response.setStatusCode(statusCode).end();
  }

  {
    InputStream stream =
        HttpVerticle.class.getClassLoader().getResourceAsStream("logging.properties");
    try {
      LogManager.getLogManager().readConfiguration(stream);
    } catch (IOException e) {
      System.out.println("The logging.properties isn't right");
    }
    log = Logger.getLogger(HttpVerticle.class.getName());
  }
}
