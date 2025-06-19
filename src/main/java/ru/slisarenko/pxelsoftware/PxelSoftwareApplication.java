package ru.slisarenko.pxelsoftware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import ru.slisarenko.pxelsoftware.db.dao.UserDAOImpl;

@ConfigurationPropertiesScan("ru.slisarenko.pxelsoftware.config")
@SpringBootApplication
public class PxelSoftwareApplication {

    public static void main(String[] args) {

        var context = SpringApplication.run(PxelSoftwareApplication.class, args);

        var test = context.getBean(UserDAOImpl.class);
    }

}
