package io.github.yantrashala.sc.client;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.JsonNode;

import feign.Client;
import feign.Feign;
import feign.HeaderMap;
import feign.Logger;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import feign.Target;
import feign.Target.HardCodedTarget;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.slf4j.Slf4jLogger;
import io.github.yantrashala.sc.router.ServiceRouter;

public interface ServiceClient {

	@RequestLine("GET /{servicepath}")
	@RequestMapping(method = RequestMethod.GET, value = "/{servicepath}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	JsonNode getServiceResponse(@Param(value = "servicepath") String servicepath,
			@QueryMap Map<String, Iterable<String>> queryMap, @HeaderMap Map<String, Object> headers);

	@RequestLine("POST /{servicepath}")
	@RequestMapping(value = "/{servicepath}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	JsonNode postServiceResponse(@Param(value = "servicepath") String servicepath, Object body,
			@HeaderMap Map<String, Object> headers);

	@RequestLine("PUT /{servicepath}")
	@RequestMapping(method = RequestMethod.PUT, value = "/{servicepath}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	JsonNode putServiceResponse(@Param(value = "servicepath") String servicepath, Object body,
			@HeaderMap Map<String, Object> headers);

	feign.Logger _logger = new Slf4jLogger(ServiceRouter.class);

	static ServiceClient connect(String host, Encoder encoder, Decoder decoder, Client client,
			Logger.Level feignLoggerLevel) {
		Target<ServiceClient> target = new HardCodedTarget<ServiceClient>(ServiceClient.class, host);
		ServiceClient serviceClient = Feign.builder().client(client).encoder(encoder).decoder(decoder)
				.logger(ServiceClient._logger).logLevel(feignLoggerLevel).target(target);
		return serviceClient;
	}

	@RequestLine("GET /{servicepath}")
	@RequestMapping(method = RequestMethod.GET, value = "/{servicepath}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	String getServiceResponseXML(@Param(value = "servicepath") String servicepath,
			@QueryMap Map<String, Object> queryMap);

	@RequestLine("POST /{servicepath}")
	@RequestMapping(value = "/{servicepath}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	<T> T postServiceResponseXML(@Param(value = "servicepath") String servicepath, Object body,
			@Param("type") Class<T> returnType);

	@RequestLine("PUT /{servicepath}")
	@RequestMapping(method = RequestMethod.PUT, value = "/{servicepath}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	<T> T putServiceResponseXML(@Param(value = "servicepath") String servicepath, Object body,
			@Param("type") Class<T> returnType);
}
