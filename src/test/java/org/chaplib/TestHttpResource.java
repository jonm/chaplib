/* TestHttpResource.java
   
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestHttpResource {

    @Mock HttpClient mockHttpClient;
    @Mock ContentParser<Object> mockParser;
    @Mock InputStream mockInputStream;
    private HttpResource impl;
    private URI uri;
    private HttpEntity entity;
    private HttpResponse response;
    private Object parsed;
    
    @Before
    public void setUp() throws Exception {
        uri = new URI("http://www.example.com/");
        entity = new ByteArrayEntity(new byte[]{});
        response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        response.setEntity(entity);
        parsed = new Object();
        impl = new HttpResource(uri, mockHttpClient);
    }
    
    @Test
    public void issuesGetForValue() throws Exception {
        ArgumentCaptor<HttpUriRequest> arg = ArgumentCaptor.forClass(HttpUriRequest.class);
        when(mockHttpClient.execute(arg.capture())).thenReturn(response);
        when(mockParser.parse(entity)).thenReturn(parsed);
        assertSame(parsed, impl.value(mockParser));
        assertEquals(uri, arg.getValue().getURI());
        assertEquals("GET", arg.getValue().getMethod());
    }
    
    @Test
    public void usesHTTP1_1ForValue() throws Exception {
        ArgumentCaptor<HttpUriRequest> arg = ArgumentCaptor.forClass(HttpUriRequest.class);
        when(mockHttpClient.execute(arg.capture())).thenReturn(response);
        impl.value(mockParser);
        assertEquals(HttpVersion.HTTP_1_1, arg.getValue().getProtocolVersion());
    }
    
    
    @Test(expected=RuntimeException.class)
    public void transformsIOExceptionOnGet() throws Exception {
        when(mockHttpClient.execute(any(HttpUriRequest.class))).thenThrow(new IOException());
        impl.value(mockParser);
    }
    
    @Test(expected=RuntimeException.class)
    public void transformsClientProtocolExceptionOnGet() throws Exception {
        when(mockHttpClient.execute(any(HttpUriRequest.class))).thenThrow(new ClientProtocolException());
        impl.value(mockParser);
    }
    
    @Test
    public void nullValueIfNoContent() throws Exception {
        response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_NO_CONTENT, "No Content");
        when(mockHttpClient.execute(any(HttpUriRequest.class))).thenReturn(response);
        assertNull(impl.value(mockParser));
    }
    
    @Test
    public void closesConnectionAfterParsingGet() throws Exception {
        response.setEntity(new InputStreamEntity(mockInputStream, -1));
        when(mockHttpClient.execute(any(HttpUriRequest.class))).thenReturn(response);
        when(mockParser.parse(entity)).thenReturn(parsed);
        impl.value(mockParser);
        verify(mockInputStream, atLeastOnce()).close();
    }
    
    @Test
    public void closesConnectionIfParserThrowsException() throws Exception {
        response.setEntity(new InputStreamEntity(mockInputStream, -1));
        when(mockHttpClient.execute(any(HttpUriRequest.class))).thenReturn(response);
        when(mockParser.parse(entity)).thenThrow(new RuntimeException());
        try {
            impl.value(mockParser);
        } catch (RuntimeException expected) {
        }
        verify(mockInputStream, atLeastOnce()).close();
    }
    
    @Test(expected=RuntimeException.class)
    public void transformsIOExceptionOnStreamClose() throws Exception {
        response.setEntity(new InputStreamEntity(mockInputStream, -1));
        when(mockHttpClient.execute(any(HttpUriRequest.class))).thenReturn(response);
        when(mockParser.parse(entity)).thenReturn(parsed);
        doThrow(new IOException()).when(mockInputStream).close();
        impl.value(mockParser);
    }
    
    @Test
    public void issuesDeleteForDelete() throws Exception {
        ArgumentCaptor<HttpUriRequest> arg = ArgumentCaptor.forClass(HttpUriRequest.class);
        when(mockHttpClient.execute(arg.capture())).thenReturn(response);
        impl.delete();
        assertEquals(uri, arg.getValue().getURI());
        assertEquals("DELETE", arg.getValue().getMethod());
    }
    
    @Test
    public void usesHTTP1_1ForDelete() throws Exception {
        ArgumentCaptor<HttpUriRequest> arg = ArgumentCaptor.forClass(HttpUriRequest.class);
        when(mockHttpClient.execute(arg.capture())).thenReturn(response);
        impl.delete();
        assertEquals(HttpVersion.HTTP_1_1, arg.getValue().getProtocolVersion());
    }
    
    @Test
    public void issuesPutForReplaceOrCreate() throws Exception {
        ArgumentCaptor<HttpUriRequest> arg = ArgumentCaptor.forClass(HttpUriRequest.class);
        when(mockHttpClient.execute(arg.capture())).thenReturn(response);
        impl.replaceOrCreate(entity);
        assertEquals(uri, arg.getValue().getURI());
        assertEquals("PUT", arg.getValue().getMethod());
    }
    
    @Test
    public void usesHTTP1_1ForPut() throws Exception {
        ArgumentCaptor<HttpUriRequest> arg = ArgumentCaptor.forClass(HttpUriRequest.class);
        when(mockHttpClient.execute(arg.capture())).thenReturn(response);
        impl.replaceOrCreate(entity);
        assertEquals(HttpVersion.HTTP_1_1, arg.getValue().getProtocolVersion());
    }

    @Test
    public void usesSuppliedEntityForPut() throws Exception {
        ArgumentCaptor<HttpEntityEnclosingRequest> arg = ArgumentCaptor.forClass(HttpEntityEnclosingRequest.class);
        when(mockHttpClient.execute((HttpUriRequest) arg.capture())).thenReturn(response);
        impl.replaceOrCreate(entity);
        assertSame(entity, arg.getValue().getEntity());
    }
    
    @Test
    public void issuesPOSTForPost() throws Exception {
        ArgumentCaptor<HttpUriRequest> arg = ArgumentCaptor.forClass(HttpUriRequest.class);
        when(mockHttpClient.execute(arg.capture())).thenReturn(response);
        impl.post(entity);
        assertEquals(uri, arg.getValue().getURI());
        assertEquals("POST", arg.getValue().getMethod());
    }
    
    @Test
    public void usesHTTP1_1ForPost() throws Exception {
        ArgumentCaptor<HttpUriRequest> arg = ArgumentCaptor.forClass(HttpUriRequest.class);
        when(mockHttpClient.execute(arg.capture())).thenReturn(response);
        impl.post(entity);
        assertEquals(HttpVersion.HTTP_1_1, arg.getValue().getProtocolVersion());
    }

    @Test
    public void usesSuppliedEntityForPost() throws Exception {
        ArgumentCaptor<HttpEntityEnclosingRequest> arg = ArgumentCaptor.forClass(HttpEntityEnclosingRequest.class);
        when(mockHttpClient.execute((HttpUriRequest) arg.capture())).thenReturn(response);
        impl.post(entity);
        assertSame(entity, arg.getValue().getEntity());
    }
    
    /*
     * "User agents SHOULD include this [User-Agent] field with requests."
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.43
     */
    @Test
    public void setsUserAgentForGet() throws Exception {
        ArgumentCaptor<HttpUriRequest> arg = ArgumentCaptor.forClass(HttpUriRequest.class);
        when(mockHttpClient.execute(arg.capture())).thenReturn(response);
        when(mockParser.parse(entity)).thenReturn(parsed);
        impl.value(mockParser);
        String ua = arg.getValue().getFirstHeader("User-Agent").getValue();
        assertNotNull(ua);
        assertFalse("".equals(ua.trim()));
    }

}
