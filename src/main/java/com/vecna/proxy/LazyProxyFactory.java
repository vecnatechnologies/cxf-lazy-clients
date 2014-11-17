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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a <em>lazy proxy</em> around an object.
 *
 * Given a way to instantiate the target object (encapsulated in a {@link InstanceFactory} factory) and a set of interfaces that the
 * object would implement, {@link #getInstance()} will generate a {@link Proxy} object that implements the interfaces,
 * instantiates the target object (through {@link InstanceFactory#getInstance()} upon the first call to any of the interface methods,
 * and delegates that and all subsequent calls to the target object.
 *
 * A typical use case for {@link LazyProxyFactory} is proxying a service whose initialization is expensive (such as a JAX-WS
 * webservice with complex data bindings) to cut down the application initialization time.
 *
 * Note that the generated proxy will incur an additional reflection call cost for every invocation of an interface method.
 *
 * @param <T> specifies one of the proxied interfaces for convenience
 * @author ogolberg@vecna.com
 */
public class LazyProxyFactory<T> implements InstanceFactory<T> {
  private static final Logger s_log = LoggerFactory.getLogger(LazyProxyFactory.class);

  private final Class<T> m_mainInterface;
  private final Class<?>[] m_interfaces;
  private final InstanceFactory<?> m_factory;

  private Object m_instance;

  /**
   * Create a new {@link LazyProxyFactory}.
   *
   * @param factory the strategy for instantiating the target object.
   * @param mainInterface main interface that the target object implements and the proxy intercepts.
   * @param additionalInterfaces additional interfaces that the target object implements and the proxy intercepts.
   */
  public LazyProxyFactory(InstanceFactory<T> factory, Class<T> mainInterface, Class<?> ... additionalInterfaces) {
    if (factory == null) {
      throw new IllegalArgumentException("target object factory cannot be null");
    }

    if (mainInterface == null) {
      throw new IllegalArgumentException("must specify at least one interface to implement");
    }

    m_mainInterface = mainInterface;

    m_interfaces = new Class<?>[additionalInterfaces.length + 1];
    m_interfaces[0] = mainInterface;
    for (int i = 0; i < additionalInterfaces.length; i++) {
      m_interfaces[i + 1] = additionalInterfaces[i];
    }

    m_factory = factory;
  }

  /**
   * @return create the lazy proxy instance.
   */
  @Override
  public T getInstance() {
    s_log.debug("creating a lazy proxy for {}", Arrays.asList(m_interfaces));

    return m_mainInterface.cast(Proxy.newProxyInstance(m_interfaces[0].getClassLoader(), m_interfaces, new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (m_instance == null) {
          synchronized (m_factory) {
            if (m_instance == null) {
              s_log.debug("initializing the proxy for {}", Arrays.asList(m_interfaces));
              m_instance = m_factory.getInstance();
            }
          }
        }

        return method.invoke(m_instance, args);
      }
    }));
  }
}