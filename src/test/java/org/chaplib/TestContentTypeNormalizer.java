/* 
 * TestContentTypeNormalizer.java
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

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderValueParser;
import org.junit.Before;
import org.junit.Test;


public class TestContentTypeNormalizer {

    private ContentTypeNormalizer impl;

    @Before
    public void setUp() {
        impl = new ContentTypeNormalizer();
    }
    
    /*
     * "Senders wishing to defeat this behavior MAY include a charset
     * parameter even when the charset is ISO-8859-1 and SHOULD do so
     * when it is known that it will not confuse the recipient."
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.4.1
     */
    @Test
    public void addsDefaultCharset() {
        String result = impl.normalize("text/plain");
        HeaderElement elt = BasicHeaderValueParser.parseHeaderElement(result, null);
        assertNotNull(elt.getParameterByName("charset"));        
    }
    
    @Test
    public void defaultCharsetIsIso_8859_1() {
        String result = impl.normalize("text/plain");
        HeaderElement elt = BasicHeaderValueParser.parseHeaderElement(result, null);
        assertTrue("iso-8859-1".equalsIgnoreCase(elt.getParameterByName("charset").getValue()));        
    }
    
    @Test
    public void doesNotAddCharsetIfOneExists() {
        String result = impl.normalize("text/plain;charset=utf-8");
        HeaderElement elt = BasicHeaderValueParser.parseHeaderElement(result, null);
        int charsets = 0;
        for(NameValuePair pair : elt.getParameters()) {
            if ("charset".equalsIgnoreCase(pair.getName())) charsets++;
        }
        assertEquals(1, charsets);
    }
    
    @Test
    public void doesNotModifyExistingCharset() {
        String result = impl.normalize("text/plain;charset=utf-8");
        HeaderElement elt = BasicHeaderValueParser.parseHeaderElement(result, null);
        assertEquals("utf-8", elt.getParameterByName("charset").getValue());
    }
}
