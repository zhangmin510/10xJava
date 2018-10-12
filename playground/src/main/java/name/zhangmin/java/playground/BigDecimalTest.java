 package name.zhangmin.java.playground;

import java.math.BigDecimal;

/**
 * @author hzzhangmin15
 * @date 2018/08/17
 */
public class BigDecimalTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BigDecimal a = BigDecimal.ONE;
		BigDecimal amount = BigDecimal.valueOf(0.000000001);
		System.out.println(amount);
		System.out.println(amount.negate().toPlainString());
		System.out.println(amount.multiply(BigDecimal.valueOf(-1)).toPlainString());
		System.out.println(a.subtract(amount.negate()));
		System.out.println(a.subtract(amount.multiply(BigDecimal.valueOf(-1))));

		BigDecimal d = BigDecimal.ZERO;
		System.out.println(d.toString());
		System.out.println(d.toEngineeringString());
		System.out.println(d.toPlainString());
	}

}
