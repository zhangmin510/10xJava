package name.zhangmin.basic;

/**
 * @author zhangmin.name
 * @date 2019/4/13
 */
public class SelfReferenceExample {
    SelfReferenceExample sre;

    public SelfReferenceExample() {
        this.sre = new SelfReferenceExample();
    }

    public static void main(String[] args) {
        new SelfReferenceExample(); // stackOverflowError
    }
}
