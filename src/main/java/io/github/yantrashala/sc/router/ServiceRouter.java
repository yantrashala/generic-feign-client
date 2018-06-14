package io.github.yantrashala.sc.router;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;

import feign.Client;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import io.github.yantrashala.sc.client.AnyClient;
import io.github.yantrashala.sc.client.ServiceClient;
import io.github.yantrashala.sc.discovery.LookupService;
import io.github.yantrashala.sc.util.JsonUtil;
import io.github.yantrashala.sc.util.MapUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RefreshScope
public class ServiceRouter {

	@Autowired
	private LookupService discoveryService;

	public ServiceRouter(Decoder decoder, Encoder encoder, Client client) {
		this.decoder = decoder;
		this.encoder = encoder;
		this.client = client;
	}

	@Autowired
	private AnyClient anyClient;

	@Autowired
	private Logger.Level feignLoggerLevel = Logger.Level.BASIC;

	private Decoder decoder;
	private Encoder encoder;
	private Client client;

	boolean direct = true;

	public JsonNode route(String serviceKey, JsonNode requestBody, ServiceMeta service,
			MultiValueMap<String, String> queryMap) {
		JsonNode serviceResponse;
		if (service.isDisabled()) {
			log.info("Service disabled {}", service);
			serviceResponse = JsonUtil.getBlankJson();
		} else {
			Optional<String> serviceHost = discoveryService.getServiceHost(service);
			Map<String, Object> headers = new HashMap<>();
			if (serviceHost.isPresent()) {
				try {
					switch (service.getMethod()) {
					case PUT:
						log.trace("Invoking {} version {} at {} with body {}", service.getName(), service.getVersion(),
								service.getPath(), requestBody);
						if (direct) {
							serviceResponse = getServiceClient(serviceHost.get()).putServiceResponse(service.getPath(),
									requestBody, headers);
						} else {
							serviceResponse = anyClient.putServiceResponse(service.getName(), service.getVersion(),
									service.getPath(), requestBody);
						}
						break;
					case POST:
						log.trace("Invoking {} version {} at {} with body {}", service.getName(), service.getVersion(),
								service.getPath(), requestBody);
						if (direct) {
							serviceResponse = getServiceClient(serviceHost.get()).postServiceResponse(service.getPath(),
									requestBody, headers);
						} else {
							serviceResponse = anyClient.postServiceResponse(service.getName(), service.getVersion(),
									service.getPath(), requestBody);
						}
						break;
					case GET:
						log.trace("Invoking direct {} -> {} version {} at {} with query params {} and headers {}",
								direct, service.getName(), service.getVersion(), service.getPath(), queryMap);
						if (direct) {
							serviceResponse = getServiceClient(serviceHost.get()).getServiceResponse(service.getPath(),
									MapUtil.mapFrom(queryMap), headers);
						} else {
							serviceResponse = anyClient.getServiceResponse(service.getName(), service.getVersion(),
									service.getPath(), queryMap);
						}
						break;
					default:
						serviceResponse = JsonUtil.getBlankJson();
					}
				} catch (Exception e) {
					e.printStackTrace();
					serviceResponse = JsonUtil.getBlankJson();
					log.error(
							"Caught exception for {} version {} at {} with queryMap {} and headers {} exception {} for remoteService {} using cookies {}",
							service.getName(), service.getVersion(), service.getPath(), queryMap, e.getMessage(),
							serviceKey);
				}

			} else {
				serviceResponse = JsonUtil.getBlankJson();
			}
		}
		return serviceResponse;
	}

	private ServiceClient getServiceClient(String host) {
		return ServiceClient.connect(host, encoder, decoder, client, feignLoggerLevel);
	}

}
