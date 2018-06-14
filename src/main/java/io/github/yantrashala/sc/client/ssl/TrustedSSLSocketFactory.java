package io.github.yantrashala.sc.client.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TrustedSSLSocketFactory extends SSLSocketFactory {
	private final SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
	private SSLContext allowSSLContext;
	private final Set<String> trustedHostnames;

	public TrustedSSLSocketFactory(String... trustedHostnames) {
		this.trustedHostnames = trustedHostnames == null ? Collections.emptySet()
				: Collections.unmodifiableSet(new HashSet<>(Arrays.asList(trustedHostnames)));
		try {
			allowSSLContext = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e) {
			try {
				allowSSLContext = SSLContext.getDefault();
			} catch (NoSuchAlgorithmException e1) {
				throw new RuntimeException("Unable to get SSL context", e1);
			}
		}
		TrustManager tm = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		try {
			allowSSLContext.init(null, new TrustManager[] { tm }, null);
		} catch (KeyManagementException e) {
			throw new RuntimeException("Unable to initialise SSL context", e);
		}
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return sslSocketFactory.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return sslSocketFactory.getSupportedCipherSuites();
	}

	private boolean isIgnored(String host) {
		return !trustedHostnames.isEmpty() && trustedHostnames.contains(host);
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
		return (isIgnored(host)) ? allowSSLContext.getSocketFactory().createSocket(socket, host, port, autoClose)
				: sslSocketFactory.createSocket(socket, host, port, autoClose);
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return (isIgnored(host)) ? allowSSLContext.getSocketFactory().createSocket(host, port)
				: sslSocketFactory.createSocket(host, port);
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localAddress, int localPort)
			throws IOException, UnknownHostException {
		return (isIgnored(host)) ? allowSSLContext.getSocketFactory().createSocket(host, port, localAddress, localPort)
				: sslSocketFactory.createSocket(host, port, localAddress, localPort);
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return (isIgnored(host.getHostName())) ? allowSSLContext.getSocketFactory().createSocket(host, port)
				: sslSocketFactory.createSocket(host, port);
	}

	@Override
	public Socket createSocket(InetAddress host, int port, InetAddress localHost, int localPort) throws IOException {
		return (isIgnored(host.getHostName()))
				? allowSSLContext.getSocketFactory().createSocket(host, port, localHost, localPort)
				: sslSocketFactory.createSocket(host, port, localHost, localPort);
	}
}