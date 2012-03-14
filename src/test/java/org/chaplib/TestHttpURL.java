/* 
 * TestHttpURL.java
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

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import org.junit.Test;


public class TestHttpURL {

    @Test
    public void canCreateFromHttpURLString() {
        new HttpURL("http://www.example.com/");
    }

    @Test
    public void canCreateFromHttpsURLString() {
        new HttpURL("https://www.example.com/");
    }

    @Test(expected=IllegalArgumentException.class)
    public void cannotCreateWithOtherScheme() {
        new HttpURL("ftp://www.example.com/pub/README");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void cannotCreateWithPoorlyFormedURL() {
        new HttpURL("foo");
    }
    
    @Test
    public void isEqualIfURLsAreIdentical() {
        assertEquals(new HttpURL("http://www.example.com/"),
                new HttpURL("http://www.example.com/"));
    }
    
    /*
     * "A port that is empty or not given is equivalent to the
     * default port for that URI-reference."
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.2.3
     */
    @Test
    public void defaultHttpPortIsEqualto80() {
        assertEquals(new HttpURL("http://www.example.com/"),
                new HttpURL("http://www.example.com:80/"));
    }
    
    @Test
    public void defaultHttpPortIsNotEqualto79() {
        assertFalse(new HttpURL("http://www.example.com/").equals(
                new HttpURL("http://www.example.com:79/")));
    }
    
    @Test
    public void defaultHttpsPortIsEqualto443() {
        assertEquals(new HttpURL("https://www.example.com/"),
                new HttpURL("https://www.example.com:443/"));
    }

    @Test
    public void defaultHttpsPortIsNotEqualto444() {
        assertFalse(new HttpURL("https://www.example.com/").equals(
                new HttpURL("https://www.example.com:444/")));
    }

    /*
     * "Comparisons of host names MUST be case-insensitive."
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.2.3
     */
    @Test
    public void differentHostNameCapitalizationsAreTheSame() {
        assertEquals(new HttpURL("http://www.example.com/"),
                new HttpURL("http://WWW.eXaMpLe.Com/"));
    }
    
    @Test
    public void differentHostSpellingsAreNotEqual() throws Exception {
        assertFalse(new HttpURL("http://example.com/").equals(
                new HttpURL("http://www.example.com/")));
    }

    /*
     * "Comparisons of scheme names MUST be case-insensitive."
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.2.3
     */
    @Test
    public void differentSchemeCapitalizationsAreTheSame() {
        assertEquals(new HttpURL("http://www.example.com/"),
                new HttpURL("HTTP://www.example.com/"));
    }
    
    @Test
    public void differentSchemeSpellingsAreNotEqual() {
        assertFalse(new HttpURL("http://www.example.com/").equals(
                new HttpURL("https://www.example.com/")));
    }

    /*
     * "An empty abs_path is equivalent to an abs_path of '/'."
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.2.3
     */
    @Test
    public void emptyPathIsSameAsSlash() {
        assertEquals(new HttpURL("http://www.example.com"),
                new HttpURL("http://www.example.com/"));
    }
    
    /*
     * "Characters other than those in the 'reserved' and 'unsafe' sets 
     * (see RFC 2396) are equivalent to their '"%" HEX HEX' encoding."
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.2.3
     * http://www.ietf.org/rfc/rfc2396.txt
     */
    @Test
    public void reservedURLCharactersAreNotEqualToTheirHexEncoding() throws Exception {
        String[] reserved = { ";", "/", "?", ":", "@", "&", "=", "+",
                "$", "," };
        for(String s : reserved) {
            HttpURL url1 = new HttpURL("http://www.example.com/" + s);
            HttpURL url2 = new HttpURL("http://www.example.com/" + URLEncoder.encode(s, "utf-8"));
            assertFalse(url1.equals(url2));
        }
    }
    
    @Test
    public void unsafeURLCharactersAreNotEqualToTheirHexEncoding() throws Exception {
        String[] unsafe = { "{", "}", "|", "\\", "^", "[", "]", "`" };
        for(String s : unsafe) {
            HttpURL url1 = new HttpURL("http://www.example.com/" + s);
            HttpURL url2 = new HttpURL("http://www.example.com/" + URLEncoder.encode(s, "utf-8"));
            assertFalse(url1.equals(url2));
        }
    }

    @Test
    public void otherCharactersAreEqualToTheirHexEncoding() throws Exception {
        String unreserved = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789" +
            "-_.!~*'()";
        for(int i=0; i<unreserved.length(); i++) {
            Character c = unreserved.charAt(i);
            HttpURL url1 = new HttpURL("http://www.example.com/" + c);
            byte b = ("" + c).getBytes()[0];
            HttpURL url2 = new HttpURL("http://www.example.com/" + String.format("%%%x", b));
            assertTrue(url1.equals(url2));
        }
    }
    
    /*
     * "For example, the following three URIs are equivalent:
     * http://abc.com:80/~smith/home.html
     * http://ABC.com/%7Esmith/home.html
     * http://ABC.com:/%7esmith/home.html"
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.2.3
     */
    @Test
    public void rfc2616ExamplesAreEqual() {
        assertEquals(new HttpURL("http://abc.com:80/~smith/home.html"),
                new HttpURL("http://ABC.com/%7Esmith/home.html"));
        assertEquals(new HttpURL("http://ABC.com/%7Esmith/home.html"),
                new HttpURL("http://ABC.com:/%7esmith/home.html"));
    }
    
    @Test
    public void canGetCanonicalURI() {
        String uri = "http://ABC.com:/%7esmith/home.html";
        HttpURL url1 = new HttpURL(uri);
        URL canon = url1.getCanonicalURL();
        HttpURL url2 = new HttpURL(canon.toString());
        assertEquals(canon, url2.getCanonicalURL());
    }
    
    @Test
    public void canCreateFromURI() throws Exception {
        String s = "http://ABC.com:/%7esmith/home.html";
        assertEquals(new HttpURL(s), new HttpURL(new URI(s)));
    }
    
    @Test
    public void canCreateFromURL() throws Exception {
        String s = "http://ABC.com:/%7esmith/home.html";
        assertEquals(new HttpURL(s), new HttpURL(new URL(s)));
    }
}
