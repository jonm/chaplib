/* 
 * TestDefaultUserAgentGenerator.java
 * 
 * Copyright (C) 2012 Jonathan Moore
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
