package io.github.yantrashala.sc.client.ssl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrustedHostnameVerifier implements HostnameVerifier {
	private static final Logger log = LoggerFactory.getLogger(TrustedHostnameVerifier.class);
	private final Set<String> trustedHostnames;
	private final HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();

	public TrustedHostnameVerifier(String... trustedHostnames) {
		this.trustedHostnames = trustedHostnames == null ? Collections.EMPTY_SET
				: Collections.unmodifiableSet(new HashSet<>(Arrays.asList(trustedHostnames)));
	}

	@Override
	public boolean verify(String hostname, SSLSession session) {
		boolean valid = trustedHostnames.stream()
				.anyMatch(trusted -> trusted.equals(hostname) || trusted.endsWith("." + hostname))
				|| hostnameVerifier.verify(hostname, session);
		log.debug("Hostname {} was forced trusted {}", hostname, valid);
		return valid;
	}
}