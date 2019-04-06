package name.zhangmin.designpattern.proxy;

public class RealImage {
    private String fileName;

    public RealImage(String fileName) {
        this.fileName = fileName;
    }
    void display() {
        System.out.println("display images");
    }
}
