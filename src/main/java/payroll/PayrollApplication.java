package payroll;

import com.example.demo.DemoApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Believe it or not, this is enough to launch an application! A Spring Boot application is, at a minimum, a public static void main entry-point and the @SpringBootApplication annotation.
// This tells Spring Boot to help out, wherever possible.

//@SpringBootApplication is a meta-annotation that pulls in component scanning, autoconfiguration, and property support. We won’t dive into the details of Spring Boot in this tutorial, but in essence,
// it will fire up a servlet container and serve up our service.
@SpringBootApplication
public class PayrollApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayrollApplication.class, args);
    }
}
