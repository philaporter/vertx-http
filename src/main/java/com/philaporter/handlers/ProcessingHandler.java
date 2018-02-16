package com.philaporter.handlers;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

public class ProcessingHandler implements Handler<Message<Object>> {

  @Override
  public void handle(Message<Object> data) {
    System.out.println(data.body().toString());
  }
}
