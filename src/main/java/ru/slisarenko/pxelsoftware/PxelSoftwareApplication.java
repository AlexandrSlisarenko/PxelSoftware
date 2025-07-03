package ru.slisarenko.pxelsoftware;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import ru.slisarenko.pxelsoftware.db.dao.UserDAOImpl;

@ConfigurationPropertiesScan("ru.slisarenko.pxelsoftware.config")
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "PxelSoftware", description = "My first test task!!!"))
public class PxelSoftwareApplication {

    public static void main(String[] args) {

        var context = SpringApplication.run(PxelSoftwareApplication.class, args);

        var test = context.getBean(UserDAOImpl.class);
    }

}
