package io.github.yantrashala.sc.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;

import feign.Client;
import io.github.yantrashala.sc.config.ClientConfigurer;
import io.github.yantrashala.sc.config.ServiceClientConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ServiceClientConfiguration.class,
		HttpMessageConvertersAutoConfiguration.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableFeignClients
public class ServiceClientTest {

	@Autowired
	ClientConfigurer clientConfigurer;

	@Test
	public void testWithSimpleClient() {
		Client client = ClientConfigurer.directClient(new String[0]);
		ServiceClient serviceClient = clientConfigurer.configure("https://httpbin.org");
		JsonNode jsonNode = serviceClient.getServiceResponse("get", new HashMap<>(), new HashMap<>());
		// {"args":{},"headers":{"Accept":"*/*","Connection":"close","Host":"httpbin.org","User-Agent":"Java/1.8.0_161"},"origin":"125.16.91.5","url":"https://httpbin.org/get"}
		assertNotNull(jsonNode);
		assertTrue(jsonNode.has("args"));
		assertTrue(jsonNode.has("headers"));
		JsonNode headers = jsonNode.get("headers");
		assertTrue(headers.has("Host"));
		assertEquals("httpbin.org", headers.get("Host").asText());
	}

}
