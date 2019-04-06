package name.zhangmin.designpattern.proxy;

public class ProxyUsageDemo {
    public static void main(String[] args) {
        Image image = new ImageProxy("test_file");
        image.display();
        // Image object already created
        image.display();
    }
}
