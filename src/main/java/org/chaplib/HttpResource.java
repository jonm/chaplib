/* HttpResource.java
   
   Copyright (C) 2012 Jonathan Moore

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.chaplib;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

public class HttpResource {

    private URI uri;
    private HttpClient httpClient;
    
    public HttpResource(URI uri, HttpClient httpClient) {
        this.uri = uri;
        this.httpClient = httpClient;
    }

    public <T> T value(ContentParser<T> parser) {
        HttpResponse resp = execute(new HttpGet(uri));
        HttpEntity entity = resp.getEntity();
        if (entity == null) return null;
        try {
            return parser.parse(entity);
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private HttpResponse execute(HttpUriRequest req) {
        try {
            req.setHeader("User-Agent", "chaplib/0.1.0 Apache-HttpClient/4.1.3");
            return httpClient.execute(req);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() {
        execute(new HttpDelete(uri));
    }

    public void replaceOrCreate(HttpEntity entity) {
        HttpPut req = new HttpPut(uri);
        req.setEntity(entity);
        execute(req);
    }

    public void post(HttpEntity entity) {
        HttpPost req = new HttpPost(uri);
        req.setEntity(entity);
        execute(req);
    }

}
