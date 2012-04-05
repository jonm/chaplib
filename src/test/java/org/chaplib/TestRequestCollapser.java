package org.chaplib;

import static org.junit.Assert.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class TestRequestCollapser {

    private Computation<Object> computation;
    private RequestCollapser<Object> impl;
    private Object result;

    @Before
    public void setUp() {
        result = new Object();
        computation = new Computation<Object>() {
            public Object execute() {
                return result;
            }
        };
        impl = new RequestCollapser<Object>(computation);
    }
    
    @Test
    public void singleCallerJustGetsResultOfRequest() {
        assertSame(result, impl.get());
    }
    
    @Test(expected=TooLateException.class)
    public void secondCallerGetsExceptionAfterFirstIsDone() {
        impl.get();
        impl.get();
    }

    @Test
    public void twoConcurrentCallersCanGetResults() throws Exception {
        Computation<Object> c = new Computation<Object>() {
            public Object execute() {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {
                    fail("interrupted");
                }
                return result;
            }
        };
        final CountDownLatch cdl = new CountDownLatch(2);
        Runnable r = new Runnable() {
            public void run() {
                assertSame(result, impl.get());
                cdl.countDown();
            }
        };
        impl = new RequestCollapser<Object>(c);
        (new Thread(r)).start();
        (new Thread(r)).start();
        assertTrue(cdl.await(1L, TimeUnit.SECONDS));
    }
    
    private static class Counter {
        public int count = 0;
    }
    
    @Test
    public void onlyOneRequestForTwoConcurrentCallers() throws Exception {
        final Counter cnt = new Counter();
        Computation<Object> c = new Computation<Object>() {
            public Object execute() {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {
                    fail("interrupted");
                }
                synchronized(cnt) {
                    cnt.count++;
                }
                return result;
            }
        };
        final CountDownLatch cdl = new CountDownLatch(2);
        Runnable r = new Runnable() {
            public void run() {
                impl.get();
                cdl.countDown();
            }
        };
        impl = new RequestCollapser<Object>(c);
        (new Thread(r)).start();
        (new Thread(r)).start();
        assertTrue(cdl.await(1L, TimeUnit.SECONDS));
        assertEquals(1, cnt.count);
    }

}
