package org.auscope.portal.server.web.security;

import org.springframework.data.repository.CrudRepository;


/**
 * NCIDetails repository interface
 * 
 * @author woo392
 *
 */
public interface NCIDetailsRepository extends CrudRepository<NCIDetails, Integer> {
    
    NCIDetails findByUser(ANVGLUser user);
}
