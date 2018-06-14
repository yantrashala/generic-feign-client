package io.github.yantrashala.sc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class AnyClientFallback implements AnyClient {

	private static final Logger log = LoggerFactory.getLogger(AnyClientFallback.class);
	JsonNode emptyResponse = new TextNode("");

	@Override
	public JsonNode postServiceResponse(String servicename, String serviceversion, String servicepath, Object body) {
		return null;
	}

	@Override
	public JsonNode putServiceResponse(String servicename, String serviceversion, String servicepath, Object body) {
		log.error("Fell back Invoking {} version {} at {} with body {}", servicename, serviceversion, servicepath,
				body);
		return null;
	}

	@Override
	public JsonNode getServiceResponse(String servicename, String serviceversion, String servicepath,
			MultiValueMap<String, String> queryMap) {
		log.error("Fell back Invoking {} version {} at {} with queryMap {} , cookiesHeader {} and headers {}",
				servicename, serviceversion, servicepath, queryMap);
		return emptyResponse;
	}

}
