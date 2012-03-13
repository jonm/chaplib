package org.chaplib;

import java.net.MalformedURLException;
import java.net.URL;

public class HttpURL {
    private URL url;
    
    public HttpURL(String s) {
        try {
            URL url = new URL(s);
            String scheme = url.getProtocol();
            if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
                throw new IllegalArgumentException("can only be created with http or https URI schemes");
            }
            this.url = canonicalURL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("must be created with valid URL syntax", e);
        }
    }

    private URL canonicalURL(URL url) {
        String protocol = url.getProtocol().toLowerCase();
        String host = url.getHost().toLowerCase();
        int port = url.getPort();
        if (port == -1 && "http".equals(protocol)) port = 80;
        if (port == -1 && "https".equals(protocol)) port = 443;
        String file = url.getFile();
        if ("".equals(file)) file = "/";
        try {
            return new URL(protocol, host, port, file);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("canonicalization error", e);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HttpURL other = (HttpURL) obj;
        if (url == null) {
            if (other.url != null)
                return false;
        }
        return (url.getProtocol().equals(other.url.getProtocol())
                && url.getHost().equals(other.url.getHost())
                && url.getPort() == other.url.getPort()
                && url.getFile().equals(other.url.getFile()));
    }

    
}
