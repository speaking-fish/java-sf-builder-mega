package com.speakingfish.common.builder.mega.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class CommonInstance<
    DEFINITION extends CommonInstanceDefinition<INSTANCE>, 
    INSTANCE   extends CommonInstance<DEFINITION, INSTANCE, PROXY>,
    PROXY
> implements InvocationHandler {
    
    protected final DEFINITION _definition;
    protected final PROXY      _proxy     ;

    @SuppressWarnings("unchecked")
    public CommonInstance(DEFINITION definition) {
        super();
        _definition = definition;
        _proxy = (PROXY) createProxy();
    }
    
    protected Object createProxy() {
        return Proxy.newProxyInstance(
            _definition.instanceClassLoader(),
            _definition.instanceInterfaces (),
            this
            );
    }

    public DEFINITION definition() { return _definition; }
    public PROXY      proxy     () { return _proxy     ; }

    @SuppressWarnings("unchecked")
    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final MethodInvoker<?, INSTANCE> invoker = definition().methodById(new MethodId(method));
        if(null != invoker) {
            return invoker.invoke((INSTANCE) this, args);
        } else {
            throw new NoSuchMethodException("Method: " + method + " is undefined in instance of definition: " + definition());
        }
    }

}
