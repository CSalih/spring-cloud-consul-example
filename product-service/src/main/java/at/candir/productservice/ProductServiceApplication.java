package at.candir.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ProductServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}
}

@Service
class ProductService {
	private final List<Product> productList = new ArrayList<>() {
		{
			add(new Product(1, "Coffee"));
			add(new Product(2, "Tee"));
			add(new Product(3, "Water"));
			add(new Product(4, "Orange juice"));
		}
	};

	public List<Product> getAllProducts() {
		return productList;
	}

}

@RestController
class HelloController {
	private final ProductService productService;

	HelloController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping("/products")
	public Flux<Product> products() {
		var products = productService.getAllProducts();
		return Flux.fromIterable(products);
	}

}

record Product(long id, String name) {}