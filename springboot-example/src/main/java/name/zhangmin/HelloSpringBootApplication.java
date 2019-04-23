package name.zhangmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HelloSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloSpringBootApplication.class, args);
	}

	@RequestMapping("/hello")
	public String hello() {
		// 打印springboot应用的ClassLoader
	    ClassLoader classLoader = this.getClass().getClassLoader();
	    while (classLoader != null) {
			System.out.println(classLoader.getClass().getCanonicalName());
			classLoader = classLoader.getParent();
		}
		return "Hello World!";
	}
}

