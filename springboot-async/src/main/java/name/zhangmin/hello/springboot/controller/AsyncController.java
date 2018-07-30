 package name.zhangmin.hello.springboot.controller;

import java.util.concurrent.Callable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hzzhangmin15
 * @date 2018/07/30
 */
@RestController
public class AsyncController {
	@RequestMapping(value = "/sync", method = RequestMethod.GET)
    public String test() throws InterruptedException {
        Thread.sleep(1000);
        return "sync";
    }

    @RequestMapping("/async")
    public Callable<String> callable() {
        // 使用异步将不会阻塞tomcat的io读写线程池、使得增加系统的吞吐量
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(1000);
                return "hello";
            }
        };
    }

}
