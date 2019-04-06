package name.zhangmin.designpattern.decorator;

public class ShapeDecorator implements Shape {
    Shape decoratedShape;
    public ShapeDecorator(Shape shape) {
        this.decoratedShape = shape;
    }
    @Override
    public void draw() {
        decoratedShape.draw();
    }
}
