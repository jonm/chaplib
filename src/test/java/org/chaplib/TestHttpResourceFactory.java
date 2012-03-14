/* 
 * TestHttpResourceFactory.java
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
