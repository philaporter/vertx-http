package com.philaporter.verticles;

import com.philaporter.Main;
import com.philaporter.handlers.RedisHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/** @author Philip Porter */
public class RedisClientVerticle extends AbstractVerticle {

  private static Logger log = null;
  private EventBus eb = null;

  @Override
  public void start() throws IOException {
    // TODO: Replace this logging setup with something better
    InputStream stream =
        RedisClientVerticle.class.getClassLoader().getResourceAsStream("logging.properties");
    LogManager.getLogManager().readConfiguration(stream);
    log = Logger.getLogger(RedisClientVerticle.class.getName());

    final RedisClient client =
        RedisClient.create(vertx, new RedisOptions().setHost(Main.config.getString("redisHost")));
    eb = vertx.eventBus();
    final RedisHandler handler = new RedisHandler(client);
    eb.consumer("redis", handler::handle);
  }
}
