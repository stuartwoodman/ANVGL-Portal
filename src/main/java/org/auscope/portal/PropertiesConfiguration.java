package org.auscope.portal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Configure property files
 * 
 * TODO: Expand this to get machine names similar to original VGL
 * 
 * @author woo392
 *
 */
@Configuration
public class PropertiesConfiguration {

    @Bean
    public PropertyPlaceholderConfigurer properties() {
        final PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
      //ppc.setIgnoreUnresolvablePlaceholders(true);
      ppc.setIgnoreResourceNotFound(true);

      final List<Resource> resourceLst = new ArrayList<Resource>();

      resourceLst.add(new ClassPathResource("application.properties"));
      resourceLst.add(new ClassPathResource("env.properties"));

      ppc.setLocations(resourceLst.toArray(new Resource[]{}));

      return ppc;
    }

}
