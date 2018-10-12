class Chinese extends Passenger {
    @Override
    void outbound () { 
        System.out.println("Chinese outbound channel");
    }
    void buy () {
        System.out.println("buy buy buy");
    }
}
  