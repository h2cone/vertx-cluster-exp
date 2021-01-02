package io.h2cone.vertxhazelcastexp.provider;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 * @author h
 */
public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Promise<String> deployment = Promise.promise();
    vertx.deployVerticle(new DefaultProvider(), deployment);
    deployment.future()
      .onSuccess(result -> startPromise.complete())
      .onFailure(startPromise::fail);
  }
}
