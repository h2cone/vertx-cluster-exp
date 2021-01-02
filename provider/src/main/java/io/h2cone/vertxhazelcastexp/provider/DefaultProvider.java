package io.h2cone.vertxhazelcastexp.provider;

import io.h2cone.vertxhazelcastexp.common.BusAddress;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author h
 */
public class DefaultProvider extends AbstractVerticle {
  private static final Logger log = LoggerFactory.getLogger(DefaultProvider.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.eventBus().<JsonObject>consumer(BusAddress.TEST_REQUEST, msg -> {
      JsonObject body = msg.body();
      log.debug("consume from {}, message body: {}", BusAddress.TEST_REQUEST, body);
      if (Objects.nonNull(body)) {
        body.put("consumed", true);
      }
      msg.reply(body);
    });
    vertx.eventBus().<JsonObject>consumer(BusAddress.TEST_SEND, msg -> {
      JsonObject body = msg.body();
      log.debug("consume from {}, message body: {}", BusAddress.TEST_SEND, body);
    });
    vertx.eventBus().<JsonObject>consumer(BusAddress.TEST_PUBLISH, msg -> {
      JsonObject body = msg.body();
      log.debug("consume from {}, message body: {}", BusAddress.TEST_PUBLISH, body);
    });
  }
}
