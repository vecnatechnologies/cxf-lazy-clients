Lazy CXF
========================

About
-----------------

Deploy CXF clients faster (in development).

This library provides drop-in replacements for CXF's JAXRSClientFactoryBean and JaxWsProxyFactoryBean.
Both have an additional __lazy__ parameter which controls whether a proxied, uninitialized (lazy) client is created by the factory.
A lazy client would delay all expensive initialization (data binding generation/introspection or what have you) until the first
call to the client.

Note that lazy clients incur the cost of an additional reflective call for each call, so it's not advised to set __lazy__ to
__true__ outside of the development environment.

Compatibility
-----------------

Has been tested with CXF 3.0.1 but should be compatible with older 2.x versions. The CXF dependencies are marked as optional
(assuming your project already depends on the CXF's JAX-RS and/or JAX-WS runtime if you're using this library).

Example
-----------------

```xml

  <bean id="myServiceFactory" class="com.vecna.cxf.ws.JaxWsProxyFactoryBean"
    p:serviceClass="com.services.MyService"
    p:address="https://myserver/MyService"
    p:lazy="true"/>

  <bean id="myService" factory-bean="myServiceFactory" factory-method="get"/>
```