/**
 * Copyright 2014 Vecna Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.vecna.proxy;

import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

/**
 * Tests for {@link LazyProxyFactory}.
 * @author ogolberg@vecna.com
 */
public class LazyProxyFactoryTest extends TestCase {
  private static final int CONCURRENT_THREADS = 8;

  /**
   * Tests the proxy returned by {@link LazyProxyFactory#createLazyProxy()}.
   *
   * Verifies that the target object is initialized lazily and only once.
   */
  public void testProxy() {
    final AtomicReference<TestProxyTarget> targetRef = new AtomicReference<>();

    final TestProxyInterface instance = new LazyProxyFactory<>(new InstanceFactory<TestProxyInterface>() {
      @Override
      public TestProxyInterface getInstance() {
        TestProxyTarget target = new TestProxyTarget();
        targetRef.set(target);
        return target;
      }
    }, TestProxyInterface.class).getInstance();

    assertNull("proxy initialized too early", targetRef.get());

    String id = instance.id();

    assertNotNull("proxy must be initialized", targetRef.get());

    assertEquals("first invocation of the proxy returns the wrong result", id, targetRef.get().id());
    assertEquals("second invocation of the proxy returns the wrong result", id, instance.id());
  }

  /**
   * Attempts to verify that the target object is only initialized once
   * when concurrent calls to the proxy are made.
   */
  public void testThreading() {
    final TestProxyInterface instance = new LazyProxyFactory<>(new InstanceFactory<TestProxyInterface>() {
      @Override
      public TestProxyInterface getInstance() {
        return new TestProxyTarget();
      }
    }, TestProxyInterface.class).getInstance();

    Thread[] threads = new Thread[CONCURRENT_THREADS];
    final String[] output = new String[CONCURRENT_THREADS];

    for (int i = 0; i < threads.length; i++) {
      final int idx = i;
      threads[i] = new Thread() {
        @Override
        public void run() {
          output[idx] = instance.id();
        }
      };
    }

    for (Thread thread : threads) {
      thread.start();
    }

    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        fail("thread interrupted " + e.getMessage());
      }
    }

    for (int i = 1; i < output.length; i++) {
      assertEquals("all invocations must return the same result; possibly multiple target objects created",
                   output[0], output[i]);
    }
  }
}
