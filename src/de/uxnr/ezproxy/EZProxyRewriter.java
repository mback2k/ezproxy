package de.uxnr.ezproxy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uxnr.proxy.Headers;
import de.uxnr.proxy.HostRewriter;

public class EZProxyRewriter implements HostRewriter {
  private static final String CONTENT_TYPE_HEADER_UPPER = "Content-Type";
  private static final String CONTENT_TYPE_HEADER_LOWER = "Content-type";
  private static final String COOKIE_HEADER = "Cookie";

  private EZProxySession session;

  public synchronized void setSession(EZProxySession session) {
    this.session = session;
  }

  @Override
  public synchronized void rewriteRequest(StringBuilder requestMethod, StringBuilder requestURI,
      Headers requestHeaders) throws IOException {

    if (this.session != null) {
      String url = requestURI.toString();
      String domain = this.session.getDomain();
      String cookie = this.session.getCookie();
      if (!url.contains(domain)) {
        int offset = requestURI.indexOf("/", requestURI.indexOf("://") + 3);
        requestURI.insert(offset, "." + domain);
      }
      if (!cookie.isEmpty()) {
        requestHeaders.add(COOKIE_HEADER, cookie);
      }
    }

    System.out.println(requestURI.toString());
    System.out.println(">>> " + requestHeaders.entrySet());
  }

  @Override
  public void rewriteResponse(StringBuilder requestMethod, StringBuilder requestURI,
      Headers requestHeaders, Headers responseHeaders) throws IOException {

    // DIRTY HACK: Rewrite Content-type to Content-Type
    if (responseHeaders.containsKey(CONTENT_TYPE_HEADER_LOWER)) {
      List<String> contentTypes = responseHeaders.remove(CONTENT_TYPE_HEADER_LOWER);
      Map<String, List<String>> contentHeaders = new HashMap<String, List<String>>();

      contentHeaders.put(CONTENT_TYPE_HEADER_UPPER, contentTypes);
      responseHeaders.putAll(contentHeaders);
    }

    System.out.println("<<< " + responseHeaders.entrySet());
  }
}
