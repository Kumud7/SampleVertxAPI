package com.vertx.sample.api.verticle;

import com.vertx.sample.api.service.handler.SampleServiceHandler;
import com.vertx.sample.api.service.handler.ServiceHandler;

import io.reactivex.Single;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.net.SocketAddress;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.CookieHandler;
import io.vertx.reactivex.ext.web.handler.SessionHandler;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;

public class SampleVertxApiStartVerticle extends AbstractVerticle{
	
	public static final String port = System.getProperty("https.port");
	
	
	@Override
	public void start() throws Exception {
		
		Router subRouter = passRouter();
		Router mainRouter = Router.router(vertx);
		mainRouter.mountSubRouter("/", subRouter);
		
		// Need to figure out how to handle here
		mainRouter.route().failureHandler(failureRoutingContext ->{
			// most probably if exception comes something can be handled 
			// e.g. set status in db as failed
		});
		vertx.exceptionHandler(handler ->{
			// most probably if exception comes something can be handled 
						// e.g. set status in db as failed
		});
		boolean logEnabled = Boolean.getBoolean("http.logs");
		HttpServer httpServer = vertx.createHttpServer(new HttpServerOptions().setLogActivity(logEnabled));
		
		httpServer.requestHandler(mainRouter::accept);
		
		Single<HttpServer> listenSingle = httpServer.rxListen(getPort());
		
		listenSingle.subscribe(handler -> {
			System.out.println("Http server is running on port " + getPort());
		}, error ->{
			System.out.println("Http server is failed to run with reason "+ error.getMessage());
		});
		
	}
	
	
	private int getPort() {
		if(port == null) {
			return 443;
		}else {
			return Integer.parseInt(port);
		}
	}


	protected synchronized Router passRouter() {
		Router passRouter = Router.router(vertx);
		passRouter.route().handler(CookieHandler.create());
		passRouter.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
		passRouter.route().consumes("application/json");
		passRouter.route().produces("application/json");
		passRouter.route().handler(BodyHandler.create());
		setupHandler(passRouter);
		return passRouter;
		
	}


	private void setupHandler(Router passRouter) {
		SampleServiceHandler serviceHanndler = new SampleServiceHandler();
		String serviceName = serviceHanndler.getServiceName();
		String serviceURI = serviceHanndler.getServiceUri();
		
		if(serviceURI != null & !serviceURI.isEmpty()) {
			if(!serviceURI.startsWith("/")) {
				serviceURI = "/"+serviceURI;
			}
		}else {
			
		}
		
		passRouter.get(serviceURI+"/:id").handler(serviceHanndler::handleFetchRequest);
		passRouter.post(serviceURI+"/*").handler(serviceHanndler::handleCreateRequest);
		passRouter.put(serviceURI).handler(serviceHanndler::handleUpdateRequest);
		passRouter.delete(serviceURI).handler(serviceHanndler::handleRemoveRequest);
		
	}

}
