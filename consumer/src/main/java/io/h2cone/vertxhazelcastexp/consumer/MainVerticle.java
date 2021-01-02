package io.h2cone.vertxhazelcastexp.consumer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author h
 */
public class MainVerticle extends AbstractVerticle {
  private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Promise<String> deployment = Promise.promise();
    vertx.deployVerticle(new HttpServer(), deployment);
    deployment.future()
      .onSuccess(result -> startPromise.complete())
      .onFailure(startPromise::fail);
  }
}
