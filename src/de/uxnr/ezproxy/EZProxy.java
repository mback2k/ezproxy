package de.uxnr.ezproxy;

import de.uxnr.proxy.Proxy;

public class EZProxy {
	public static void main(String[] args) throws Exception {
		EZProxySession session = new EZProxySession();
		EZProxyRewriter.setSession(session);
		session.launch();

		Proxy proxy = new Proxy(12348);
		proxy.addHostRewriter(".*", new EZProxyRewriter());
		proxy.start();
	}
}
