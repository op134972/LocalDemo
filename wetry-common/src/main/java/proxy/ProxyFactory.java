package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by tangwc on 2018/4/6.
 */
public class ProxyFactory {

    private Object target;

    ProxyFactory(Object target) {
        this.target = target;
    }

    public Object getProxtInstance() {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces()
                , new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return method.invoke(target, args);
                    }
                });
    }
}
