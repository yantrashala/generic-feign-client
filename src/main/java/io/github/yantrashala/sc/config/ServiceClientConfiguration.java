package io.github.yantrashala.sc.config;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.cloud.netflix.feign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.netflix.feign.ribbon.LoadBalancerFeignClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import feign.Client;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import io.github.yantrashala.sc.client.ssl.TrustedHostnameVerifier;
import io.github.yantrashala.sc.client.ssl.TrustedSSLSocketFactory;

@Configuration
@Import(FeignClientsConfiguration.class)
public class ServiceClientConfiguration {

	@Value("${trust-hostnames:'localhost'}")
	private String[] trustedHostnames;

	@Bean
	@ConditionalOnMissingBean
	public ClientConfigurer getConfigurer(Decoder decoder, Encoder encoder, Client client) {
		return new ClientConfigurer(decoder, encoder, client);
	}

	@Bean
	@ConditionalOnMissingBean
	public Logger.Level getLoggerLevel() {
		return Logger.Level.BASIC;
	}

	@Bean
	@Primary
	@ConditionalOnMissingBean(DiscoveryClient.class)
	public Client simpleClient() {
		return new Client.Default(new TrustedSSLSocketFactory(trustedHostnames),
				new TrustedHostnameVerifier(trustedHostnames));
	}

	@Bean
	@Autowired
	@ConditionalOnBean(DiscoveryClient.class)
	public Client loadBalancedClient(CachingSpringLoadBalancerFactory cachingFactory,
			SpringClientFactory clientFactory) {
		return new LoadBalancerFeignClient(new Client.Default(new TrustedSSLSocketFactory(trustedHostnames),
				new TrustedHostnameVerifier(trustedHostnames)), cachingFactory, clientFactory);
	}
}
