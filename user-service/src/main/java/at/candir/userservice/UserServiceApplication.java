package at.candir.userservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class UserServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}


	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder();
	}
}

@Component
class ProductClient {
	private final WebClient webClient;

	public ProductClient(WebClient.Builder wb) {
		webClient = wb
				.baseUrl("http://product-service")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}

	Flux<Product> getAllProducts() {
		return webClient.get()
				.uri("/products")
				.retrieve()
				.bodyToFlux(Product.class);
	}
}

@RestController
class UserController {
	@Autowired
	private DiscoveryClient discoveryClient;
	@Autowired
	private ProductClient productClient;


	@GetMapping("/users")
	public Mono<String> users() {
		return Mono.just("Greetings from User Service");
	}

	@GetMapping("/products")
	public Flux<Product> products() {
		return productClient.getAllProducts();
	}

	@GetMapping("/services")
	public Flux<ServiceInstance> serviceUrl() {
		List<ServiceInstance> list = discoveryClient.getInstances("product-service");

		return Flux.fromIterable(list);
	}
}

record Product(long id, String name) {}


