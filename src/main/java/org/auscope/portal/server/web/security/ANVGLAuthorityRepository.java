package org.auscope.portal.server.web.security;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


/**
 * ANVGLUser repository interface
 * 
 * @author woo392
 *
 */
public interface ANVGLAuthorityRepository extends CrudRepository<ANVGLAuthority, Integer> {
    
    // Methods for Social login
    //List<ANVGLAuthority> findByUserId(String userId);
    List<ANVGLAuthority> findByParent_Id(String userId);
    
}
