package io.h2cone.vertxhazelcastexp.consumer;

import io.h2cone.vertxhazelcastexp.common.BusAddress;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author h
 */
public class HttpServer extends AbstractVerticle {
  private static final Logger log = LoggerFactory.getLogger(HttpServer.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    retriever.getConfig(json -> {
      JsonObject config = json.result();
      Integer port = config.getInteger("http.port", 8080);

      Router router = Router.router(vertx);
      router.get("/hello").handler(this::hello);
      router.post().handler(BodyHandler.create());
      router.post("/test/request").handler(this::testRequest);
      router.post("/test/send").handler(this::testSend);
      router.post("/test/publish").handler(this::testPublish);

      vertx.createHttpServer()
        .requestHandler(router)
        .listen(port)
        .onSuccess(server -> {
            log.info("HTTP server started on port " + server.actualPort());
            startPromise.complete();
          }
        ).onFailure(startPromise::fail);
    });
  }

  private void testPublish(RoutingContext context) {
    JsonObject reqBody = context.getBodyAsJson();
    log.debug("request body: {}", reqBody);
    vertx.eventBus().publish(BusAddress.TEST_PUBLISH, reqBody);
    context.json(reqBody);
  }

  private void testSend(RoutingContext context) {
    JsonObject reqBody = context.getBodyAsJson();
    log.debug("request body: {}", reqBody);
    vertx.eventBus().send(BusAddress.TEST_SEND, reqBody);
    context.json(reqBody);
  }

  private void testRequest(RoutingContext context) {
    JsonObject reqBody = context.getBodyAsJson();
    log.debug("request body: {}", reqBody);
    vertx.eventBus().<JsonObject>request(BusAddress.TEST_REQUEST, reqBody, response -> {
      if (response.succeeded()) {
        Message<JsonObject> msg = response.result();
        JsonObject msgBody = msg.body();
        log.debug("reply from {}, message body: {}", BusAddress.TEST_REQUEST, msgBody);
        context.json(msgBody);
      } else {
        log.error("failed to test request", response.cause());
      }
    });
  }

  private void hello(RoutingContext context) {
    String address = context.request().connection().remoteAddress().toString();
    MultiMap queryParams = context.queryParams();
    String name = queryParams.contains("name") ? queryParams.get("name") : "unknown";
    context.json(
      new JsonObject()
        .put("name", name)
        .put("address", address)
        .put("message", "Hello " + name + " connected from " + address)
    );
  }
}
