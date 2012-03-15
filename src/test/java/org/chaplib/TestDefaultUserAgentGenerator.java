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
    
    @Test
    public void generatesAUserAgentString() {
        HttpRequest request = new HttpGet("http://www.example.com/");
        impl.setUserAgent(request);
        assertFalse("".equals(request.getFirstHeader("User-Agent").getValue().trim()));
    }
}
