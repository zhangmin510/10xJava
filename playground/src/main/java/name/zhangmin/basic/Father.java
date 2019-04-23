package name.zhangmin.basic;

/**
 * @author zhangmin.name
 * @date 2019/4/13
 */
public class Father {
    {
        System.out.println("Father {}");
    }

    static {
        System.out.println("Father static {}");
    }

    private String name = "father";
    public Father() {
        System.out.println("Father constructor");
        say();
    }

    public void say() {
        System.out.println("Father say: " + name);
    }
}
