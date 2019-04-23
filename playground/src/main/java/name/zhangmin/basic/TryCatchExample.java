package name.zhangmin.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangmin.name
 * @date 2019/4/13
 */
public class TryCatchExample {


    public int test() {
        int i = 0;
        try {
            i += 5;
            return i;
        } finally {
            i += 5;
        }
    }

    public static int baz() {
        int i = 0;
        try {
            i += 10;
            return i;
        } finally {
            i -= 10;
            return i;
        }
    }

    public static void main(String[] args) {
        TryCatchExample tce = new TryCatchExample();
        System.out.println(tce.test()); // output: 5

        System.out.println(baz()); // output: 0
    }
}
