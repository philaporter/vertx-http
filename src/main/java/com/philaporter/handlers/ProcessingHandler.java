package com.philaporter.handlers;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class ProcessingHandler implements Handler<Message<Object>> {

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
    } else if ("add".equals(action)) {
      System.out.println("Added employee "
              + json.getJsonObject("employee").getString("empId"));
      System.out.println(json.getJsonObject("employee").encodePrettily());
    } else if ("get".equals(action)) {
      System.out.println(
          "Confirmed employee "
              + json.getJsonObject("employee").getString("empId")
              + " is in the database");
      System.out.println(json.getJsonObject("employee").encodePrettily());
    } else if ("getAll".equals(action)) {
      System.out.println(
          "All employee records \n"
                  + json.getJsonArray("employee").encodePrettily());
    } else if ("update".equals(action)) {
      System.out.println(
          "Updated employee "
              + json.getJsonObject("employee").getString("empId")
              + " in the database");
      System.out.println(json.getJsonObject("employee").encodePrettily());
    }
  }
}
