package org.chaplib;

import java.util.Date;

import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Test;


public class TestResponse {

    @Test
    public void canCreate() {
        new Response(new Date(), new Date(), new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
    }
}
