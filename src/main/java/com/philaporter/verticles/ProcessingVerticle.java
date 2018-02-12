package com.philaporter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ProcessingVerticle extends AbstractVerticle {

  private static Logger log = null;
  private EventBus eb = null;

  @Override
  public void start() {
    eb = vertx.eventBus();
    eb.consumer(
        "processingVerticle",
        data -> {
          System.out.println(data.body().toString());
        });
  }

  {
    InputStream stream =
        ProcessingVerticle.class.getClassLoader().getResourceAsStream("logging.properties");
    try {
      LogManager.getLogManager().readConfiguration(stream);
    } catch (IOException e) {
      System.out.println("The logging.properties isn't right");
    }
    log = Logger.getLogger(ProcessingVerticle.class.getName());
  }
}
