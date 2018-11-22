package csc;

import csc.dao.DynamicDataSource;
import csc.web.config.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;


@SpringBootApplication
@PropertySource(factory = YamlPropertySourceFactory.class,
        value = {"classpath:endpoint.yaml", "classpath:constant.yaml"}, ignoreResourceNotFound = true)
public class DynamicAPIProvisionApplication extends SpringBootServletInitializer implements CommandLineRunner {

    private DynamicDataSource repo;
    @Autowired
    public void setRepositories(DynamicDataSource repo) {
        this.repo = repo;
    }

    public static void main(String[] args) {
        SpringApplication.run(DynamicAPIProvisionApplication.class, args);
    }

    @Override
    public void run(String... arg0) {
        System.out.println("Running Web Application ... ");
    }
}