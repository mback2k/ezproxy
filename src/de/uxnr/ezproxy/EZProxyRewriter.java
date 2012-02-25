package de.uxnr.ezproxy;

import java.io.IOException;

import de.uxnr.proxy.Headers;
import de.uxnr.proxy.HostRewriter;

public class EZProxyRewriter implements HostRewriter {
	private EZProxySession session;

	public synchronized void setSession(EZProxySession session) {
		this.session = session;
	}

	@Override
	public synchronized void rewriteRequest(StringBuilder requestMethod,
			StringBuilder requestURI, Headers requestHeaders)
			throws IOException {

		if (this.session != null) {
			String url = requestURI.toString();
			String domain = this.session.getDomain();
			String cookie = this.session.getCookie();
			if (!url.contains(domain)) {
				int offset = requestURI.indexOf("/",
						requestURI.indexOf("://") + 3);
				requestURI.insert(offset, "." + domain);
			}
			if (!cookie.isEmpty()) {
				requestHeaders.add("Cookie", cookie);
			}
		}

		System.out.println(requestURI.toString());
		System.out.println(">>> " + requestHeaders.entrySet());
	}

	@Override
	public void rewriteResponse(StringBuilder requestMethod,
			StringBuilder requestURI, Headers requestHeaders,
			Headers responseHeaders) throws IOException {

		System.out.println("<<< " + responseHeaders.entrySet());
	}
}
