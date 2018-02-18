package com.philaporter.handlers;

import com.philaporter.verticles.RedisClientVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.op.ScanOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/** @author Philip Porter */
public class RedisHandler implements Handler<Message<Object>> {

  private RedisClient client;
  private static Logger log;

  public RedisHandler(RedisClient client) throws IOException {
    // TODO: Replace this logging setup with something better
    InputStream stream =
        RedisClientVerticle.class.getClassLoader().getResourceAsStream("logging.properties");
    LogManager.getLogManager().readConfiguration(stream);
    log = Logger.getLogger(RedisClientVerticle.class.getName());

    this.client = client;
    initializeRedisEntries();
  }

  public void initializeRedisEntries() {
    log.info("Initializing the database with sample entries: ");
    client.set(
        "a1b2c3d4",
        new JsonObject()
            .put("empId", "a1b2c3d4")
            .put("fName", "Philip")
            .put("lName", "Porter")
            .put("salary", 100000)
            .toString(),
        r -> {
          if (r.failed()) {
            log.warning("Failed to set Philip Porter as an employee");
          } else {
            client.get(
                "a1b2c3d4",
                philip -> {
                  log.info("Successfully set Philip Porter as an employee");
                  log.info(philip.result());
                });
          }
        });
    client.set(
        "a2b3c4d5",
        new JsonObject()
            .put("empId", "a2b3c4d5")
            .put("fName", "Alan")
            .put("lName", "Jones")
            .put("salary", 120000)
            .toString(),
        r -> {
          if (r.failed()) {
            log.warning("Failed to set Alan Jones as an employee");
          } else {
            client.get(
                "a2b3c4d5",
                alan -> {
                  log.info("Successfully set Alan Jones as an employee");
                  log.info(alan.result());
                });
          }
        });
  }

  @Override
  public void handle(Message<Object> data) {
    // move the processingHandler's lookup process here
    JsonObject json = new JsonObject(data.body().toString());
    String action = json.getString("action");
    if ("get".equals(action)) {
      get(json);
    } else if ("getAll".equals(action)) {
      getAll();
    } else if ("add".equals(action)) {
      add(json);
    } else if ("update".equals(action)) {
      update(json);
    } else if ("remove".equals(action)) {
      remove(json);
    }
  }

  public void get(JsonObject json) {
    client.get(
        json.getString("empId"),
        result -> {
          if (result.succeeded()) {
            log.info("Found employee record" + result.result());
          } else {
            log.warning("Couldn't find record for employee " + json.getString("empId"));
          }
        });
  }

  public void getAll() {
    client.scan(
        "0", // I don't fully get the cursor concept yet
        new ScanOptions().setCount(100),
        result -> {
          if (result.failed()) {
            log.warning("Couldn't find anything from the Redis db");
          } else {
            JsonArray list = result.result();
            if (list != null) {
              log.info("Found the following employees: ");
              list.getJsonArray(1)
                  .forEach(
                      item -> {
                        client.get(
                            item.toString(),
                            employee -> {
                              log.info(employee.result());
                            });
                      });
              // list.getJsonArray(0).stream().forEach(item -> {});
            }
          }
        });
  }

  public void add(JsonObject json) {
    client.set(
        json.getJsonObject("employee").getString("empId"),
        json.getJsonObject("employee").toString(),
        result -> {
          if (result.failed()) {
            log.warning("Couldn't add " + json.getJsonObject("employee").toString());
          } else {
            log.info("Added " + json.getJsonObject("employee").toString());
          }
        });
  }

  public void update(JsonObject json) {
    client.set(
        json.getJsonObject("employee").getString("empId"),
        json.getJsonObject("employee").toString(),
        result -> {
          if (result.failed()) {
            log.warning("Couldn't update " + json.getJsonObject("employee"));
          } else {
            log.info("Updated " + json.getJsonObject("employee"));
          }
        });
  }

  public void remove(JsonObject json) {
    System.out.println(json.toString());
    client.del(
        json.getString("empId"),
        result -> {
          if (result.failed()) {
            log.warning("Couldn't remove " + json.getString("empId") + " from the Redis db");
          } else {
            log.info("Successfully removed " + json.getString("empId"));
          }
        });
  }
}
