package io.github.yantrashala.sc.discovery;

import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import feign.Client;
import feign.codec.Decoder;
import feign.codec.Encoder;
import io.github.yantrashala.sc.router.ServiceMeta;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LookupService {
	public LookupService(Decoder decoder, Encoder encoder, Client client) {
		this.decoder = decoder;
		this.encoder = encoder;
		this.client = client;
	}

	final Decoder decoder;
	final Encoder encoder;
	final Client client;

	@Autowired
	private DiscoveryClient discoveryClient;

	protected String getHostByServiceName(String serviceName) {
		return (discoveryClient.getInstances(serviceName).get(0).isSecure() ? "https://" : "http://") + serviceName;
	}

	public Optional<String> getServiceHost(ServiceMeta service) {
		Optional<String> serviceName = getServiceId(service);
		if (serviceName.isPresent()) {
			String host = getHostByServiceName(serviceName.get());
			log.debug("Created host {} for service {}", host, service);
			return Optional.of(host);
		} else {
			return Optional.empty();
		}
	}

	private Optional<String> matchWith(Predicate<String> predicate) {
		return discoveryClient.getServices().stream().filter(predicate).findAny();
	}

	protected Predicate<String> exactMatch(ServiceMeta service) {
		return name -> service.getServiceId().equalsIgnoreCase(name);
	}

	protected Predicate<String> partialMatch(ServiceMeta service) {
		return name -> name.startsWith(service.getName());
	}

	private Optional<String> getServiceId(ServiceMeta service) {
		final Optional<String> exactMatchServiceId = matchWith(exactMatch(service));
		Optional<String> serviceId;
		if (exactMatchServiceId.isPresent()) {
			serviceId = exactMatchServiceId;
			log.debug("Found {} exactly matching {} ", serviceId, service);
		} else {
			final Optional<String> partialMatchServiceId = matchWith(partialMatch(service));
			if (partialMatchServiceId.isPresent()) {
				serviceId = partialMatchServiceId;
				log.debug("Found {} partially matching {} ", serviceId, service);
			} else {
				log.error("No service found for name {} ", service.getName());
				serviceId = Optional.empty();
			}
		}
		return serviceId;
	}
}