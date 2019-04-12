package name.zhangmin.gc;

/**
 * @author zhangmin
 * @date 2019/4/7
 */
public class GCLogTest {
    private static final int MB = 1024 * 1024;

    public static void testAllocation() {
        byte[] a1, a2, a3, a4, a5;
        a1 = new byte[2 * MB];
        a2 = new byte[2 * MB];
        a3 = new byte[2 * MB];
        a4 = new byte[4 * MB];
        a5 = new byte[10 * MB];
    }

    public static void main(String[] args) {
        GCLogTest.testAllocation();
    }
}
