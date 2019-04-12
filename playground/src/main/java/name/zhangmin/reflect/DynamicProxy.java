package name.zhangmin.reflect;

import name.zhangmin.concurrency.MyBlockingQueue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zhangmin.name
 * @date 2019/4/12
 */
public class DynamicProxy {
    public static void main(String[] args) {
        HelloImpl hello = new HelloImpl();
        // 首先实现对应的InvocationHandler， 在其中实现额外的业务逻辑
        MyInvocationHandler handler = new MyInvocationHandler(hello);

        // 以接口为纽带， 为目标对象构建代理
        Hello proxyHello = (Hello) Proxy.newProxyInstance(HelloImpl.class.getClassLoader(),
                HelloImpl.class.getInterfaces(), handler);
        // 使用代理对象访问目标对象的逻辑
        proxyHello.sayHello();
    }
}

interface Hello {
    void sayHello();
}


class HelloImpl implements Hello {
    @Override
    public void sayHello() {
        System.out.println("hello world");
    }
}

class MyInvocationHandler implements InvocationHandler {
    private Object target;
    public MyInvocationHandler(Object target) {
        this.target = target;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Invoking sayHello");
        Object result = method.invoke(target, args);
        return result;
    }
}
