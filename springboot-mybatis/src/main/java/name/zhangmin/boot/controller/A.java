package name.zhangmin.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 循环引用示例
 *
 * @author zhangmin.name
 * @date 2019/4/13
 */
@Component
public class A {
    @Autowired
    private A a;

    public void say() {
        System.out.println("A say");
    }
}
