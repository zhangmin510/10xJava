import java.util.Random;
class BadMerchant extends Merchant {
  @Override
   public double discount (double price, Customer customer) {
    if (customer.isVIP()) {                         // invokeinterface
      return price * discrimination ();                    // invokestatic
    } else {
      return super.discount(price, customer);          // invokespecial
    }
  }
  public static double discrimination () {
    // 咱们的杀熟算法太粗暴了，应该将客户城市作为随机数生成器的种子。
    return new Random()                          // invokespecial
           .nextDouble()                         // invokevirtual
           + 0.8d;
  }
}