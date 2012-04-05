package org.chaplib;

import static org.junit.Assert.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class TestRequestCollapser {

    private Callable<Object> callable;
    private RequestCollapser<Object> impl;
    private Object result;

    @Before
    public void setUp() {
        result = new Object();
        callable = new Callable<Object>() {
            public Object call() throws Exception {
                return result;
            }
        };
        impl = new RequestCollapser<Object>(callable);
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
        Callable<Object> c = new Callable<Object>() {
            public Object call() throws Exception {
                Thread.sleep(50L);
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
        Callable<Object> c = new Callable<Object>() {
            public Object call() throws Exception {
                Thread.sleep(50L);
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

        
    @Test
    public void collapserWithNoGettersIsntFinished() {
        assertFalse(impl.isFinished());
    }
    
    @Test
    public void collapserAfterAGetIsFinished() {
        impl.get();
        assertTrue(impl.isFinished());
    }

}
