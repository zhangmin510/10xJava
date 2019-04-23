package name.zhangmin.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhangmin.name
 * @date 2019/4/13
 */
@Component
public class C {
    @Autowired
    private B b;

    public void say() {
        System.out.println("C say");
    }
}
