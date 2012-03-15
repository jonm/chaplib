package org.chaplib;

import org.apache.http.HttpRequest;

public interface UserAgentGenerator {

    void setUserAgent(HttpRequest request);
}
