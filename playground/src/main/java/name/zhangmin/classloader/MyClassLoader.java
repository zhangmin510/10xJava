package name.zhangmin.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 从自己定义的路径加载类。 一般可以通过继承URLClassLoader来实现
 *
 * @author zhangmin
 * @date 2019/4/6
 */
public class MyClassLoader extends URLClassLoader {
    private String packageName = "name.zhangmin.classloader";

    public MyClassLoader(URL[] classPath, ClassLoader parent) {
        super(classPath, parent);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> aClass = findLoadedClass(name);

        if (aClass != null) {
            return aClass;
        }

        if (!packageName.startsWith(name)) {
            return super.loadClass(name);
        } else {
            return findClass(name);
        }
    }
}
