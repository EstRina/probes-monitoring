package telran.probes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@ComponentScan(basePackages = "telran")
public class AdminConsoleAppl {

	public static void main(String[] args) {
		
		SpringApplication.run(AdminConsoleAppl.class, args);

	}

}
