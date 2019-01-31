 package name.zhangmin.java.playground;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author hzzhangmin15
 * @date 2018/10/25
 */
public class ReactiveExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<String> list = Arrays.asList("hello", "world", "zhangmin");
		Flux<String> fewWords = Flux.just("hello", "world");
		Flux<String> manyWords = Flux.fromIterable(list);

		fewWords.subscribe(System.out::println);
		manyWords.subscribe(System.out::println);
		
		//Mono.fromCallable( () -> System.currentTimeMillis() )
		//.repeat()
	    //.parallel(8) //parallelism
	    //.runOn(Schedulers.parallel())
	    //.doOnNext( d -> System.out.println("I'm on thread "+Thread.currentThread()) )
	    //.subscribe();
		
		WebClient client = WebClient.create("https://api.github.com/");
		
		Mono<String> result = client.get()
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.retrieve()
				.bodyToMono(String.class);
		
		result
		.doOnError(e -> System.out.println("====ERROR=====\n" + e.getMessage()))
		.subscribe(System.out::println);
		//System.out.println("=======\n" + result.block());
		
		
		//Mono<String> result2 = client.get()
		//		.accept(MediaType.APPLICATION_JSON)
		//		.exchange().block().bodyToMono(String.class);
		//result2.subscribe(System.out::println);
		//System.out.println("=======\n" + result2.block());
		//异步操作，调用时要等待返回
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
	}

}
