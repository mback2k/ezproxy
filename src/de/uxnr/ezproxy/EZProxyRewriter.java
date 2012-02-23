package de.uxnr.ezproxy;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import de.uxnr.proxy.Headers;
import de.uxnr.proxy.HostRewriter;

public class EZProxyRewriter implements HostRewriter {
	private static EZProxySession session;
	
	public static void setSession(EZProxySession session) {
		EZProxyRewriter.session = session;
	}
	
	@Override
	public void rewriteRequest(StringBuilder requestMethod,
			StringBuilder requestURI, Headers requestHeaders)
			throws IOException {

		if (EZProxyRewriter.session != null) {
			int offset = requestURI.indexOf("/", requestURI.indexOf("://")+3);
			requestURI.insert(offset, ".ezproxy.dhbw-mannheim.de");
			requestHeaders.add("Cookie", session.getCookie());
		}

		System.out.println(requestURI.toString());
	}

	@Override
	public void rewriteResponse(StringBuilder requestMethod,
			StringBuilder requestURI, Headers requestHeaders,
			Headers responseHeaders) throws IOException {

		for (Entry<String, List<String>> header : responseHeaders.entrySet()) {
			StringBuilder print = new StringBuilder(header.getKey());
			print.append(": ");
			for (String prop : header.getValue()) {
				print.append(prop);
				print.append("; ");
			}
			System.out.println(print);
		}
	}
}
