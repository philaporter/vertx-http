package com.philaporter.handlers;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;

/** @author Philip Porter */
public class RedisHandler implements Handler<Message<Object>> {

  private RedisClient client = null;

  public RedisHandler(RedisClient client) {
    this.client = client;
  }

  @Override
  public void handle(Message<Object> data) {
    JsonObject json = new JsonObject(data.body().toString());
//    client.set(
//        json.getJsonObject("employee").getString("empId"),
//        json.getJsonObject("employee").toString(),
//        r -> {
//          if (r.failed()) {
//            System.out.println("Failed to set employee");
//          } else {
            client.get(
                "a2b3c4d5",
                r2 -> {
                  if (r2.result() == null) {
                    System.out.println("didn't find it");
                  } else {
                    System.out.println("Found " + r2.result() + " in the redis db");
                  }
                });
//          }
//        });
  }
}
