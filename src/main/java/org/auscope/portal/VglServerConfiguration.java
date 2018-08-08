package org.auscope.portal;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * VglServer configuration file
 * 
 * TODO: Migrate away from this XML context to a straight code-defined Bean implementation
 * 
 * @author woo392
 *
 */
@Configuration
@ImportResource({
    "classpath*:applicationContext.xml",
    //"classpath*:applicationContext-security.xml",
    "classpath*:vl-known-layers.xml",
    "classpath*:vl-registries.xml",
    "classpath*:profile-portal-test.xml"})
public class VglServerConfiguration {

}
