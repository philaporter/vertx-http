package com.philaporter.verticles;

import com.philaporter.handlers.ProcessingHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/** @author Philip Porter */
public class ProcessingVerticle extends AbstractVerticle {

  private static Logger log = null;
  private EventBus eb = null;

  @Override
  public void start() throws IOException {
    // TODO: Replace this logging setup with something better
    InputStream stream =
            ProcessingVerticle.class.getClassLoader().getResourceAsStream("logging.properties");
    LogManager.getLogManager().readConfiguration(stream);
    log = Logger.getLogger(ProcessingVerticle.class.getName());

    eb = vertx.eventBus();
    final ProcessingHandler handler = new ProcessingHandler(vertx);
    eb.consumer("processingHandler", handler::handle);
  }
}
