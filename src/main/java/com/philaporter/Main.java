package com.philaporter;

import com.philaporter.verticles.HttpVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.logging.Logger;

/** @author Philip Porter */
public class Main {

  static final Logger log = Logger.getLogger("Main");

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(10));
    vertx.deployVerticle(
        HttpVerticle.class.getName(),
        res -> {
          if (res.succeeded()) {
            log.info("Successfully deployed HttpVerticle with id: " + res.result());
          } else {
            log.warning("HttpVerticle deploy failed");
          }
        });
  }
}
