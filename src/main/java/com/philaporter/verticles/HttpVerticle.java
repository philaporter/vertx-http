package com.philaporter.verticles;

import com.philaporter.Main;
import com.philaporter.handlers.HttpHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
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
  public static Map<String, JsonObject> employees = new HashMap<>();
  private EventBus eb = null;

  private void addEmployee(JsonObject employee) {
    employees.put(employee.getString("empId"), employee);
  }

  private void setup() throws IOException {
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

    // TODO: Replace this logging setup with something better
    InputStream stream =
        HttpVerticle.class.getClassLoader().getResourceAsStream("logging.properties");
    LogManager.getLogManager().readConfiguration(stream);
    log = Logger.getLogger(HttpVerticle.class.getName());
  }

  @Override
  public void start() throws IOException {
    setup();
    eb = vertx.eventBus();
    final HttpHandler handler = new HttpHandler(vertx);
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.get("/employees").handler(handler::handleGetEmployees);
    router.get("/employees/:empId").handler(handler::handleGetEmployee);
    router.post("/employees/:empId").handler(handler::handleAddEmployee);
    router.delete("/employees/:empId").handler(handler::handleRemoveEmployee);
    router.put("/employees/:empId").handler(handler::handleUpdateAddEmployee);
    vertx
        .createHttpServer()
        .requestHandler(router::accept)
        .listen(Main.config.getInteger("httpPort"));
  }
}
