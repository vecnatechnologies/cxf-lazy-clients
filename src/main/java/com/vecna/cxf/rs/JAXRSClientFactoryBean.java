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

package com.vecna.cxf.rs;

import org.apache.cxf.jaxrs.client.Client;

import com.vecna.proxy.InstanceFactory;
import com.vecna.proxy.LazyProxyFactory;

/**
 * An extension of CXF's {@link org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean} that can (optionally) delay the initialization
 * of the webservice client until its first invocation.
 *
 * @author ogolberg@vecna.com
 */
public class JAXRSClientFactoryBean extends org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean implements InstanceFactory<Client> {
  private boolean m_lazy;

  /**
   * @return proxied, lazily initialized instance (if specified {@link #setLazy(boolean)})
   *         or a fully initialized webservice client instance.
   */
  @Override
  public Client getInstance() {
    return super.create();
  }

  @Override
  public Client create() {
    if (m_lazy) {
      return new LazyProxyFactory<>(this, Client.class, getServiceClass()).getInstance();
    } else {
      return super.create();
    }
  }

  /**
   * @param lazy whether the initialize the service lazily; only set to <code>true</code> in development environments.
   */
  public void setLazy(boolean lazy) {
    m_lazy = lazy;
  }
}
