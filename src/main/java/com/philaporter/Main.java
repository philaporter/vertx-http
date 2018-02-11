package com.philaporter;

import com.philaporter.verticles.HttpVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/** @author Philip Porter */
public class Main {

  public static Logger log = null;
  public static Map<String, Integer> config = new HashMap<>();

  public static void main(String[] args) throws IOException {
    Main.loadConfigs(args);
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(
        HttpVerticle.class.getName(),
        new DeploymentOptions().setInstances(Main.config.get("http.verticle.instances")),
        res -> {
          if (res.succeeded()) {
            log.info("Successfully deployed HttpVerticle with id: " + res.result());
          } else {
            log.warning("HttpVerticle deploy failed");
          }
        });
  }

  public static void loadConfigs(String[] args) throws IOException {
    InputStream stream = Main.class.getClassLoader().getResourceAsStream("logging.properties");
    LogManager.getLogManager().readConfiguration(stream);
    log = Logger.getLogger(Main.class.getName());

    if (args.length == 0) {
      log.info("A custom config can be passed as an argument");
      log.info("java -jar <name>.jar <path>/<name>.properties");
      // Set defaults
      config.put("http.port", 8080);
    } else {
      for (String s : args) log.info(s);
      BufferedReader br = new BufferedReader(new FileReader(args[0]));
      while (br.ready()) {
        String entry[] = br.readLine().split("=");
        config.put(entry[0], Integer.parseInt(entry[1]));
      }
    }
  }
}
