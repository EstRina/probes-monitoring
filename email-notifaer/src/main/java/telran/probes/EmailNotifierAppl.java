package telran.probes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class EmailNotifierAppl {

	public static void main(String[] args) {
		
		SpringApplication.run(EmailNotifierAppl.class, args);

	}

	
}
