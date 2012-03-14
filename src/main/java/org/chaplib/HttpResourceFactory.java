package org.chaplib;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.WeakHashMap;

import org.apache.http.client.HttpClient;

public class HttpResourceFactory {

    private HttpClient httpClient;
    private WeakHashMap<HttpURL, HttpResource> directory =
        new WeakHashMap<HttpURL, HttpResource>();
    
    public HttpResourceFactory(HttpClient client) {
        this.httpClient = client;
    }

    public synchronized HttpResource get(URI uri) {
        HttpURL url = new HttpURL(uri);
        if (directory.containsKey(url)) {
            return directory.get(url);
        }
        HttpResource out;
        try {
            out = new HttpResource(url.getCanonicalURL().toURI(), httpClient);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("URL to URI conversion failed", e);
        }
        directory.put(url, out);
        return out;
    }

}
