package com.vertx.sample.api.service.handler;

import io.vertx.reactivex.ext.web.RoutingContext;

public class SampleServiceHandler {

	public String getServiceName() {
		return "Sample Vertx API";
	}

	public String getServiceUri() {
		return "/sample/api";
	}

	public void handleFetchRequest(RoutingContext routingContext) {

	}

	public void handleCreateRequest(RoutingContext routingContext) {
		//here do all businees logic
		//in my case save data to redis and call some other api to do some stuff
		// once get reply from other api than reply 
	}

	public void handleUpdateRequest(RoutingContext routingContext) {

	}

	public void handleRemoveRequest(RoutingContext routingContext) {

	}

}
