package lv.emes.libraries.communication.http.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Starts Spring server with behavior that is defined using @Controller.
 */
//@ComponentScan(basePackages = "lv.emes.software", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value=SomeClassToExclude.class) )
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class SpringRunner {
	public static final String WEB_PORT = "8080";

	public static void main(String[] args) throws Exception {
		System.getProperties().put( "server.port", WEB_PORT );
		SpringApplication.run(SpringRunner.class, args);
	}
}