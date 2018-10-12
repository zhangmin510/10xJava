abstract class Passenger {
    abstract void outbound ();
    @Override
    public String toString() { 
        return this.getClass().getName() + ".toString()";
    }
  }