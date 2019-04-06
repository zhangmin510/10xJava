package name.zhangmin.designpattern.decorator;

public class DecoratorUsageDemo {
    public static void main(String[] args) {
        Shape rectangle = new Rectangle();
        Shape redRectangle = new RedShapeDecorator(new Rectangle());

        rectangle.draw();
        redRectangle.draw();
    }
}
