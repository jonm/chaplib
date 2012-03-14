package org.chaplib;
/* 
 * HttpResourceFactory.java
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.WeakHashMap;

import org.apache.http.client.HttpClient;

public class HttpResourceFactory {

    private HttpClient httpClient;
    private WeakHashMap<HttpURL, HttpResource> directory =
        new WeakHashMap<HttpURL, HttpResource>();
    
    public HttpResourceFactory(HttpClient client) {
        this.httpClient = client;
    }

    public synchronized HttpResource get(URI uri) {
        HttpURL url = new HttpURL(uri);
        if (directory.containsKey(url)) {
            return directory.get(url);
        }
        HttpResource out;
        try {
            out = new HttpResource(url.getCanonicalURL().toURI(), httpClient);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("URL to URI conversion failed", e);
        }
        directory.put(url, out);
        return out;
    }

}
