package com.philaporter.handlers;

import com.philaporter.verticles.HttpVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class HttpHandler {

  private Vertx vertx = null;
  private EventBus eb = null;
  public static final String PROCESSING_HANDLER = "processingHandler";

  public HttpHandler(Vertx vertx) {
    this.vertx = vertx;
    eb = this.vertx.eventBus();
  }

  public void handleGetEmployees(RoutingContext routingContext) {
    JsonArray list = new JsonArray();
    HttpVerticle.employees.forEach((k, v) -> list.add(v));
    final JsonObject json =
        new JsonObject()
            .put("employee", list)
            .put("action", "getAll");
    eb.publish(PROCESSING_HANDLER, json);
    routingContext
        .response()
        .putHeader("content-type", "application/json")
        .end(json.encodePrettily());
  }

  public void handleGetEmployee(RoutingContext routingContext) {
    String empId = routingContext.request().getParam("empId");
    HttpServerResponse response = routingContext.response();
    if (empId != null && HttpVerticle.employees.containsKey(empId)) {
      final JsonObject json =
          new JsonObject()
              .put("employee", HttpVerticle.employees.get(empId))
              .put("action", "get");
      eb.publish(PROCESSING_HANDLER, json);
      response
          .putHeader("content-type", "application/json")
          .end(json.encodePrettily());
    } else {
      sendError(418, response);
    }
  }

  public void handleAddEmployee(RoutingContext routingContext) {
    String empId = routingContext.request().getParam("empId");
    HttpServerResponse response = routingContext.response();
    if (empId != null) {
      JsonObject employee = routingContext.getBodyAsJson();
      if (employee != null) {
        HttpVerticle.employees.put(empId, employee);
        final JsonObject json =
            new JsonObject()
                .put("employee", employee)
                .put("action", "add");
        eb.publish(PROCESSING_HANDLER, json);
        response
            .putHeader("content-type", "application/json")
            .end(json.encodePrettily());
      } else {
        sendError(418, response);
      }
    } else {
      sendError(418, response);
    }
  }

  public void handleUpdateAddEmployee(RoutingContext routingContext) {
    String empId = routingContext.request().getParam("empId");
    HttpServerResponse response = routingContext.response();
    if (empId != null) {
      JsonObject employee = routingContext.getBodyAsJson();
      if (employee != null) {
        HttpVerticle.employees.replace(empId, employee);
        final JsonObject json =
            new JsonObject()
                .put("employee", employee)
                .put("action", "update");
        eb.publish(PROCESSING_HANDLER, json);
        response
            .putHeader("content-type", "application/json")
            .end(json.encodePrettily());
      } else {
        sendError(418, response);
      }
    } else {
      sendError(418, response);
    }
  }

  public void handleRemoveEmployee(RoutingContext routingContext) {
    String empId = routingContext.request().getParam("empId");
    HttpServerResponse response = routingContext.response();
    if (empId != null && HttpVerticle.employees.containsKey(empId)) {
      JsonObject json =
          new JsonObject()
              .put("employee", HttpVerticle.employees.get(empId))
              .put("action", "remove");
      eb.publish(PROCESSING_HANDLER, json);
      HttpVerticle.employees.remove(empId);
      response
              .putHeader("content-type", "application/json")
              .end(json.encodePrettily());
    } else {
      sendError(418, response);
    }
  }

  private void sendError(int statusCode, HttpServerResponse response) {
    response.setStatusCode(statusCode).end();
  }
}
