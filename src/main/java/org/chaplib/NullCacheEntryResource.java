package org.chaplib;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.cache.Resource;

public class NullCacheEntryResource implements Resource {

    private static final long serialVersionUID = 1L;
    
    private byte[] buf = new byte[]{};

    public void dispose() {}

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(buf);
    }

    public long length() {
        return 0;
    }
}
