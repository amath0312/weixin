package tech.amath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;

@SpringBootApplication
public class App {
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(App.class);
		application.addListeners(new ApplicationPidFileWriter("app.pid"));
		logger.info("starting=========================================");
		application.run(args);
	}
}
