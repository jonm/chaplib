package org.chaplib;

import static org.junit.Assert.*;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;

public class TestDefaultUserAgentGenerator {

    private DefaultUserAgentGenerator impl;

    @Before
    public void setUp() {
        impl = new DefaultUserAgentGenerator();
    }
    
    /*
     * "User agents SHOULD include this [User-Agent] field with requests."
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.43
     */
    @Test
    public void generatesAUserAgentString() {
        HttpRequest request = new HttpGet("http://www.example.com/");
        impl.setUserAgent(request);
        assertFalse("".equals(request.getFirstHeader("User-Agent").getValue().trim()));
    }
}
