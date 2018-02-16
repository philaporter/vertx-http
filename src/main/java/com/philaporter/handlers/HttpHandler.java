package com.philaporter.handlers;

import com.philaporter.verticles.HttpVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class HttpHandler {

  private Vertx vertx = null;
  private EventBus eb = null;

  public HttpHandler(Vertx vertx) {
    this.vertx = vertx;
    eb = this.vertx.eventBus();
  }

  public void handleGetEmployees(RoutingContext routingContext) {
    JsonArray json = new JsonArray();
    HttpVerticle.employees.forEach((k, v) -> json.add(v));
    eb.publish("processingVerticle", json.encodePrettily());
    routingContext
        .response()
        .putHeader("content-type", "application/json")
        .end(json.encodePrettily());
  }

  public void handleGetEmployee(RoutingContext routingContext) {
    String empId = routingContext.request().getParam("empId");
    HttpServerResponse response = routingContext.response();
    if (empId != null) {
      eb.publish("processingVerticle", HttpVerticle.employees.get(empId).encodePrettily());
      response
          .putHeader("content-type", "application/json")
          .end(HttpVerticle.employees.get(empId).encodePrettily());
    } else {
      sendError(400, response);
    }
  }

  public void handleAddEmployee(RoutingContext routingContext) {
    String empId = routingContext.request().getParam("empId");
    HttpServerResponse response = routingContext.response();
    if (empId != null) {
      JsonObject employee = routingContext.getBodyAsJson();
      if (employee != null) {
        HttpVerticle.employees.put(empId, employee);
        eb.publish("processingVerticle", HttpVerticle.employees.get(empId).encodePrettily());
        response.end();
      } else {
        sendError(400, response);
      }
    } else {
      sendError(400, response);
    }
  }

  public void handleUpdateAddEmployee(RoutingContext routingContext) {
    String empId = routingContext.request().getParam("empId");
    HttpServerResponse response = routingContext.response();
    if (empId != null) {
      JsonObject employee = routingContext.getBodyAsJson();
      HttpVerticle.employees.replace(empId, employee);
      eb.publish("processingVerticle", HttpVerticle.employees.get(empId).encodePrettily());
      response.end();
    } else {
      sendError(400, response);
    }
  }

  public void handleRemoveEmployee(RoutingContext routingContext) {
    String empId = routingContext.request().getParam("empId");
    HttpServerResponse response = routingContext.response();
    if (empId != null) {
      if (empId != null) {
        HttpVerticle.employees.remove(empId);
        eb.publish("processingVerticle", HttpVerticle.employees.get(empId).encodePrettily());
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
}
