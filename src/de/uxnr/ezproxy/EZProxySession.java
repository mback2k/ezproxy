package de.uxnr.ezproxy;

import javax.swing.UIManager;

public class EZProxySession {
	protected void launch() throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}

	protected String getCookie() {
		return "";
	}
}
