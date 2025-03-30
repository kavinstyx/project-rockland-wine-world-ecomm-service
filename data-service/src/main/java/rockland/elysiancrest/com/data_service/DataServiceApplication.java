package rockland.elysiancrest.com.data_service;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
public class DataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataServiceApplication.class, args);
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Colombo"));
		// This ensures ZoneId.systemDefault() will return "Asia/Colombo"
		System.setProperty("user.timezone", "Asia/Colombo");
	}

	@Bean
	public CommandLineRunner commandLineRunner(RequestMappingHandlerMapping mapping){
		return args -> {
			Map<RequestMappingInfo, HandlerMethod> methods = mapping.getHandlerMethods();
			methods.forEach((info, method) -> System.out.println(info + " Handled by: " + method));
		};
	}
}
