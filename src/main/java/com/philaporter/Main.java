package com.philaporter;

import com.philaporter.verticles.HttpVerticle;
import com.philaporter.verticles.ProcessingVerticle;
import com.philaporter.verticles.RedisClientVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/** @author Philip Porter */
public class Main {

  public static Logger log = null;
  public static JsonObject config = null;

  public static void main(String[] args) throws IOException {
    // TODO: Replace this logging setup with something better
    InputStream stream = Main.class.getClassLoader().getResourceAsStream("logging.properties");
    LogManager.getLogManager().readConfiguration(stream);
    log = Logger.getLogger(Main.class.getName());

    printBanner();
    Vertx vertx = Vertx.vertx();
    ConfigStoreOptions fileStore =
        new ConfigStoreOptions()
            .setType("file")
            .setConfig(new JsonObject().put("path", "vertxfun-config.json"));

    ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore);
    ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
    retriever.getConfig(
        ar -> {
          if (ar.failed()) {
            log.severe("Failed to load the config file; exiting");
            vertx.close();
          } else {
            config = ar.result();
            vertx.deployVerticle(
                HttpVerticle.class.getName(),
                new DeploymentOptions().setInstances(config.getInteger("instances")),
                res -> {
                  if (res.succeeded()) {
                    log.info("Successfully deployed HttpVerticle with id: " + res.result());
                    vertx.deployVerticle(
                        ProcessingVerticle.class.getName(),
                        processingRes -> {
                          if (processingRes.succeeded()) {
                            log.info(
                                "Successfully deployed ProcessingVerticle with id: "
                                    + processingRes.result());
                            vertx.deployVerticle(RedisClientVerticle.class.getName(), redisClientRes -> {
                                if(redisClientRes.succeeded()) {
                                    log.info("Successfully deployed RedisClientVerticle with id: " + redisClientRes.result());
                                } else {
                                    log.warning("RedisClientVerticle failed to deploy");
                                    vertx.close();
                                }
                            });
                          } else {
                            log.warning("ProcessingVerticle failed to deploy");
                            vertx.close();
                          }
                        });
                  } else {
                    log.warning("Failed to deploy HttpVerticle");
                  }
                });
          }
        });
  }

  public static void printBanner() {
    System.out.println("                        _              ");
    System.out.println("                       / |_            ");
    System.out.println(" _   __  .---.  _ .--.`| |-'   _   __  ");
    System.out.println("[ \\ [  ]/ /__\\\\[ `/'`\\]| |    [ \\ [  ] ");
    System.out.println(" \\ \\/ / | \\__., | |    | |, _  > '  <  ");
    System.out.println("  \\__/   '.__.'[___]   \\__/(_)[__]`\\_] ");
    System.out.println("        __        _                    ");
    System.out.println("       [  |      (_)                   ");
    System.out.println(" .--.   | |--.   __   ____             ");
    System.out.println("( (`\\]  | .-. | [  | [_   ]            ");
    System.out.println(" `'.'.  | | | |  | |  .' /_            ");
    System.out.println("[\\__) )[___]|__][___][_____]           ");
    System.out.println("                                       ");
  }
}
