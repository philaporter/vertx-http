package com.philaporter.handlers;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/** @author Philip Porter */
public class ProcessingHandler implements Handler<Message<Object>> {

  private EventBus eb;

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
      eb.publish("redis", json);
    } else if ("add".equals(action)) {
      eb.publish("redis", json);
    } else if ("get".equals(action)) {
      eb.publish("redis", json);
    } else if ("getAll".equals(action)) {
      eb.publish("redis", json);
    } else if ("update".equals(action)) {
      eb.publish("redis", json);
    }
  }
}
