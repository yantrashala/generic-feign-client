/**
 * 
 */
package io.github.yantrashala.sc.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;

@FeignClient(value = "${gateway-service-id}")
public interface AnyClient {

	@RequestMapping(method = RequestMethod.GET, value = "/{servicename}/{serviceversion}/{servicepath}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	JsonNode getServiceResponse(@PathVariable(value = "servicename") String servicename,
			@PathVariable(value = "serviceversion") String serviceversion,
			@PathVariable(value = "servicepath") String servicepath,
			@RequestParam(required = false) MultiValueMap<String, String> queryMap);

	@RequestMapping(value = "/{servicename}/{serviceversion}/{servicepath}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	JsonNode postServiceResponse(@PathVariable(value = "servicename") String servicename,
			@PathVariable(value = "serviceversion") String serviceversion,
			@PathVariable(value = "servicepath") String servicepath, @RequestBody(required = false) Object body);

	@RequestMapping(method = RequestMethod.PUT, value = "/{servicename}/{serviceversion}/{servicepath}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	JsonNode putServiceResponse(@PathVariable(value = "servicename") String servicename,
			@PathVariable(value = "serviceversion") String serviceversion,
			@PathVariable(value = "servicepath") String servicepath, @RequestBody(required = false) Object body);
	

}
