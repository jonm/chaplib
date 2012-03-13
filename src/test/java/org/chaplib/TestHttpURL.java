package org.chaplib;

import static org.junit.Assert.*;

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
    
}
