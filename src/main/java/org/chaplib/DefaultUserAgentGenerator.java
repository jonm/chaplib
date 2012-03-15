package org.chaplib;

import org.apache.http.HttpRequest;
import org.apache.http.util.VersionInfo;

public class DefaultUserAgentGenerator implements UserAgentGenerator {

    private static final String USER_AGENT;
    static {
        String ua = String.format("chaplib/%s", ChapLibVersion.VERSION); 
        VersionInfo versionInfo = VersionInfo.loadVersionInfo("org.apache.http.client", 
                DefaultUserAgentGenerator.class.getClassLoader());
        if (versionInfo != null)
            ua += String.format(" Apache-HttpClient/%s", versionInfo.getRelease());
        USER_AGENT = ua;
    }
    
    public void setUserAgent(HttpRequest request) {
        request.setHeader("User-Agent", USER_AGENT); 
    }

}
