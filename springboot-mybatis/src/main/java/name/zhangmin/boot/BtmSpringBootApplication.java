package name.zhangmin.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author hzzhangmin15
 * @date 2018/07/05
 */
@SpringBootApplication
@MapperScan("name.zhangmin.boot.dao")
public class BtmSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(BtmSpringBootApplication.class, args);
	}
}
