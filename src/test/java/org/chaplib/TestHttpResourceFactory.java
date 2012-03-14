package org.chaplib;

import static org.junit.Assert.*;

import java.net.URI;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestHttpResourceFactory {
    
    @Mock private HttpClient mockClient;
    private HttpResourceFactory impl;
    private URI uri;
    
    @Before
    public void setUp() throws Exception {
        uri = new URI("http://www.example.com/");
        impl = new HttpResourceFactory(mockClient);
    }
    
    @Test
    public void canGetResourceFromString() {
        assertNotNull(impl.get(uri));
    }
    
    @Test
    public void sameURIResultsInSameHttpResource() {
        assertSame(impl.get(uri), impl.get(uri));
    }
    
    @Test
    public void equivalentURIsResultInSameHttpResource() throws Exception {
        URI uri2 = new URI("http://www.example.com");
        assertSame(impl.get(uri), impl.get(uri2));
    }
    
    @Test
    public void nonequalURIsResultInDifferentHttpResources() throws Exception {
        URI uri2 = new URI("http://foo.example.com/bar/baz/quxx");
        assertFalse(impl.get(uri) == impl.get(uri2));
    }
}
