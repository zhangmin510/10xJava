public class Foo7 implements AutoCloseable {
    private final String name;
    public Foo7(String name) { this.name = name; }
  
    @Override
    public void close() {
      throw new RuntimeException(name);
    }
  
    public static void main(String[] args) {
      try (
        Foo7 foo0 = new Foo7("Foo0"); // try-with-resources
        Foo7 foo1 = new Foo7("Foo1");
        Foo7 foo2 = new Foo7("Foo2")
      ) {
        throw new RuntimeException("Initial");
      }
    }
  }