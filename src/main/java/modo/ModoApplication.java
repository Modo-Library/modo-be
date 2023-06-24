package modo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ModoApplication {

	public static void main(String[] args) {
		System.out.println(org.hibernate.Version.getVersionString());
        SpringApplication.run(ModoApplication.class, args);
	}

}
