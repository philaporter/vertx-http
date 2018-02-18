package com.philaporter.verticles;

import com.philaporter.Main;
import com.philaporter.handlers.HttpHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/** @author Philip Porter */
public class HttpVerticle extends AbstractVerticle {

  private static Logger log;
  private EventBus eb;

  private void setup() throws IOException {
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
