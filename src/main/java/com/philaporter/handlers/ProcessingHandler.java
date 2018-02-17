package com.philaporter.handlers;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/** @author Philip Porter */
public class ProcessingHandler implements Handler<Message<Object>> {

  private EventBus eb = null;

  public ProcessingHandler(Vertx vertx) {
    eb = vertx.eventBus();
  }

  /**
   * Clearly this is dummy processing, but it shows how the verticles can interact with each other
   * using the eventbus
   *
   * TODO: 1) bring the RedisHandler into this is a smarter way.
   * TODO: 2) change the order of that if structure.
   *
   * @param data
   */
  @Override
  public void handle(Message<Object> data) {
    JsonObject json = new JsonObject(data.body().toString());
    String action = json.getString("action");
    if ("remove".equals(action)) {
      System.out.println(
          "Record for employee "
              + json.getJsonObject("employee").getString("empId")
              + " was removed: ");
      System.out.println(json.getJsonObject("employee").encodePrettily());
      //      eb.publish("redis", json);
    } else if ("add".equals(action)) {
      System.out.println("Added employee " + json.getJsonObject("employee").getString("empId"));
      System.out.println(json.getJsonObject("employee").encodePrettily());
      //      eb.publish("redis", json);
    } else if ("get".equals(action)) {
      System.out.println(
          "Confirmed employee "
              + json.getJsonObject("employee").getString("empId")
              + " is in the database");
      System.out.println(json.getJsonObject("employee").encodePrettily());
      eb.publish("redis", json);
    } else if ("getAll".equals(action)) {
      System.out.println(
          "All employee records \n" + json.getJsonArray("employee").encodePrettily());
      //      eb.publish("redis", json);
    } else if ("update".equals(action)) {
      System.out.println(
          "Updated employee "
              + json.getJsonObject("employee").getString("empId")
              + " in the database");
      System.out.println(json.getJsonObject("employee").encodePrettily());
      //      eb.publish("redis", json);
    }
  }
}
