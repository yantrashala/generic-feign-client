package io.github.yantrashala.sc.router;

import org.springframework.web.bind.annotation.RequestMethod;

import lombok.Data;

public @Data class ServiceMeta {
	private String scheme = "http://";

	public String getServiceURI() {
		return String.format("%s%s-%s", scheme, name, version);
	}

	public String getServiceId() {
		return String.format("%s-%s", name, version);
	}

	String name;
	String version;
	String path;
	RequestMethod method;
	boolean disabled = false;

}