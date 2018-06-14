package io.github.yantrashala.sc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.netflix.feign.ribbon.LoadBalancerFeignClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

import feign.Client;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.Target.HardCodedTarget;
import feign.codec.Decoder;
import feign.codec.Encoder;
import io.github.yantrashala.sc.client.ServiceClient;
import io.github.yantrashala.sc.client.ssl.TrustedHostnameVerifier;
import io.github.yantrashala.sc.client.ssl.TrustedSSLSocketFactory;

public class ClientConfigurer {

	@Autowired(required = false)
	private Logger.Level defaultLogLevel = Logger.Level.HEADERS;
	
	@Autowired(required = false)
	Request.Options options = new Request.Options();
	
	private Client defaultClient = directClient(null);

	public ClientConfigurer(Decoder decoder, Encoder encoder, Client client) {
		this.decoder = decoder;
		this.encoder = encoder;
		this.defaultClient = client;
	}

	public ServiceClient configure(String host) {
		return configure(host, defaultLogLevel);
	}
	
	public ServiceClient configure(String host, Client client) {
		return configure(host, client, defaultLogLevel);
	}

	public ServiceClient configure(String host, Logger.Level logLevel) {
		return configure(host, logLevel, options.connectTimeoutMillis(), options.readTimeoutMillis());
	}
	
	public ServiceClient configure(String host, Client client, Logger.Level logLevel) {
		return configure(host, client, logLevel, options.connectTimeoutMillis(), options.readTimeoutMillis());
	}

	public ServiceClient configure(String host, Logger.Level logLevel, int connectTimeoutMillis,
			int readTimeoutMillis) {
		return Feign.builder().client(defaultClient).encoder(encoder).decoder(decoder)
				.options(new Request.Options(connectTimeoutMillis, readTimeoutMillis)).logger(ServiceClient._logger)
				.logLevel(logLevel).target(new HardCodedTarget<ServiceClient>(ServiceClient.class, host));
	}
	
	public ServiceClient configure(String host, Client client, Logger.Level logLevel, int connectTimeoutMillis,
			int readTimeoutMillis) {
		return Feign.builder().client(defaultClient).encoder(encoder).decoder(decoder)
				.options(new Request.Options(connectTimeoutMillis, readTimeoutMillis)).logger(ServiceClient._logger)
				.logLevel(logLevel).target(new HardCodedTarget<ServiceClient>(ServiceClient.class, host));
	}

	public static Client loadBalancedClient(CachingSpringLoadBalancerFactory cachingFactory, SpringClientFactory clientFactory,
			String[] trustedHostnames) {
		return new LoadBalancerFeignClient(new Client.Default(new TrustedSSLSocketFactory(trustedHostnames),
				new TrustedHostnameVerifier(trustedHostnames)), cachingFactory, clientFactory);
	}

	public static Client directClient(String[] trustedHostnames) {
		return new Client.Default(new TrustedSSLSocketFactory(trustedHostnames),
				new TrustedHostnameVerifier(trustedHostnames));
	}

	private Decoder decoder;
	private Encoder encoder;

}
