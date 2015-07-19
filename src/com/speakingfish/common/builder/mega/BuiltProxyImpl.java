package com.speakingfish.common.builder.mega;

import java.util.Map;

class BuiltProxyImpl {
        protected Map<MethodId, InstanceMethodInvoker<?>> _methods;
    }
/*    
    public static <RESULT, BUILDER> BUILDER createBuilder(final Class<RESULT> resultClass, final Class<BUILDER> builderClass) {
        //Map<builderMethods>
        final BuiltValues initial = null; //new BuiltValues(); 
        
        Object tempResult = Proxy.newProxyInstance(
            builderClass.getClassLoader(),
            new Class<?>[] {builderClass},
            new InvocationHandler() {
                @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return methodsMap.get(method.getName()).invoke(initial, args);
                }}
            );
        
        @SuppressWarnings("unchecked")
        BUILDER result = (BUILDER) tempResult;
        return result;
    }
*/