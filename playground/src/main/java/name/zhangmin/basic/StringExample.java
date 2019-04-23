package name.zhangmin.basic;

/**
 * @author zhangmin.name
 * @date 2019/4/13
 */
public class StringExample {
    public static void main(String[] args) {
        String s1 = "hello";
        String s2 = "he" + new String("llo");
        System.out.println(s1 == s2);
    }
}
