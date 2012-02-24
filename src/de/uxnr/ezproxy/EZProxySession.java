package de.uxnr.ezproxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;

public class EZProxySession {
	private String cookie = "";
	private String username;
	private String password;
	private String domain;
	private URL location;
	private int port;

	protected void launch() throws Exception {
		load();
		login();
	}

	protected void finalize() throws Exception {
		logout();
	}

	protected String getDomain() {
		return this.domain;
	}

	protected String getCookie() {
		return this.cookie;
	}

	protected int getPort() {
		return this.port;
	}

	private void load() throws IOException {
		Properties properties = new Properties();

		File config = new File("ezproxy.cfg");
		if (!config.exists() || !config.canRead()) {
			config.createNewFile();

			properties.setProperty("username", "");
			properties.setProperty("password", "");
			properties.setProperty("domain", "ezproxy.dhbw-mannheim.de");
			properties.setProperty("port", "12345");
			properties.store(new FileOutputStream(config),
					"EZProxy configuration");
		} else {
			properties.load(new FileInputStream(config));
		}

		this.username = properties.getProperty("username", "");
		this.password = properties.getProperty("password", "");
		this.domain = properties.getProperty("domain",
				"ezproxy.dhbw-mannheim.de");
		this.port = Integer.parseInt(properties.getProperty("port", "12345"));

		if (this.username.isEmpty() || this.password.isEmpty()
				|| this.domain.isEmpty()) {
			throw new Error(
					"Please set username, password and domain in ezproxy.cfg");
		}
	}

	private void login() throws IOException {
		this.location = new URL("https://" + this.domain + "/login");

		int counter = 0;
		while (perform()) {
			if (counter > 3) {
				throw new Error("Unable to login at EZProxy");
			} else {
				counter++;
			}
		}

		System.out.println("Successfully logged-in using cookie: "
				+ this.cookie);
	}

	private void logout() throws IOException {
		this.location = new URL("https://" + this.domain + "/logout");

		int counter = 0;
		while (perform()) {
			if (counter > 3) {
				throw new Error("Unable to logout at EZProxy");
			} else {
				counter++;
			}
		}

		System.out.println("Successfully logged-out");
	}

	private boolean perform() throws IOException {
		URLConnection conn = this.location.openConnection();

		HttpURLConnection connection = null;
		if (conn instanceof URLConnection) {
			connection = (HttpURLConnection) conn;
		} else {
			throw new IOException("Unsupported non-HTTP connection");
		}

		connection.setRequestMethod("POST");
		connection.setDefaultUseCaches(false);
		connection.setInstanceFollowRedirects(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty("Cookie", this.cookie);
		connection.connect();

		OutputStreamWriter output = new OutputStreamWriter(
				connection.getOutputStream());
		output.write("user=" + URLEncoder.encode(this.username, "utf-8")
				+ "&pass=" + URLEncoder.encode(this.password, "utf-8")
				+ "&submit=Login");
		output.flush();
		output.close();

		if (connection.getResponseCode() == 302) {
			String cookie = connection.getHeaderField("Set-Cookie");
			String location = connection.getHeaderField("Location");
			if (cookie != null) {
				this.cookie = cookie.split(";")[0];
			}
			if (location != null) {
				this.location = new URL(location);
			}
			return true;
		}

		return false;
	}
}
