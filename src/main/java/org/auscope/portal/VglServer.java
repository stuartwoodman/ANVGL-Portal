package org.auscope.portal;

import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


/**
 * Spring Boot Application
 * 
 * @author woo392
 *
 */
@SpringBootApplication
public class VglServer /*extends SpringBootServletInitializer*/ {
    
    /*
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application;
    }
    */
    
    // TODO: Find a proper spot for this XXX
    @Bean
    public VelocityEngine velocityEngine() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("input.encoding", "UTF-8");
        properties.setProperty("output.encoding", "UTF-8");
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        VelocityEngine velocityEngine = new VelocityEngine(properties);
        return velocityEngine;
    }
    

    public static void main(String[] args) {
        //SpringApplication.run(VglServer.class, args);
        
        /*ApplicationContext applicationContext = */SpringApplication.run(VglServer.class, args);

        /*
        for (String name : applicationContext.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        */
    }
    
}
