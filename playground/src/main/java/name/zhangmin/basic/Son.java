package name.zhangmin.basic;

/**
 * @author zhangmin.name
 * @date 2019/4/13
 */
public class Son extends Father {
    {
        System.out.println("Son {}");
    }

    static {
        System.out.println("Son static {}");
    }
    private String name = "child";

    public Son() {
        System.out.println("Son constructor");
    }

    @Override
    public void say() {
        System.out.println("Son say: " + name);
    }

    public int Son(int b) {
        return b;
    }

    public static void main(String[] args) {
        Father f = new Son();
        // output:
        // Father static {}
        // Son static {}
        // Father {}
        // Father constructor
        // Son say: null 在父类初始化调用say是执行的子类的say，此时子类的name还没有初始化
        // Son {}
        // Son constructor
    }
}
