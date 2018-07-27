package org.auscope.portal.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.amazonaws.services.appstream.model.Application;


/**
 * Spring Boot Application
 * 
 * @author woo392
 *
 */
@SpringBootApplication
@ImportResource("file:/WEB-INF/applicationContext.xml")
public class VglServer /*extends SpringBootServletInitializer*/ {
    
    /*
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application;
    }
    */

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
}
