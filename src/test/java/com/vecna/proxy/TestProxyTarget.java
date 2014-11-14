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

import java.util.Random;
import java.util.UUID;

public class TestProxyTarget implements TestProxyInterface {
  private final Random m_rand = new Random(System.currentTimeMillis());
  private final String m_id = UUID.randomUUID().toString();

  @Override
  public String id() {
    return m_id;
  }

  @Override
  public long random() {
    return m_rand.nextLong();
  }
}
