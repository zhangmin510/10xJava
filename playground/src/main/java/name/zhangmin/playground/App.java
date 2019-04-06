package name.zhangmin.playground;

import java.math.BigInteger;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        System.out.println(Math.ceil(2/0.75));

        //for (int i = 0; i < 65535; i++) {
        //    System.out.print("int a" + i + ",");
        //}
        BigInteger a = BigInteger.valueOf(System.currentTimeMillis());
        System.out.println("====" + a.longValue());
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
        System.out.println(a.longValue() + 1 * 1000L > System.currentTimeMillis());
    }
}
