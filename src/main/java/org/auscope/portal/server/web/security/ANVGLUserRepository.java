package org.auscope.portal.server.web.security;

import org.springframework.data.repository.CrudRepository;


/**
 * ANVGLUser repository interface
 * 
 * @author woo392
 *
 */
public interface ANVGLUserRepository extends CrudRepository<ANVGLUser, String> {
    
    ANVGLUser findByEmail(String email);

    // Methods for Social login XXX
    //@Query("select a from ANVGLAuthority where a.userId = ?1")
    //List<String> getAuthorities(String userId);
    
    //ANVGLUser findByUsername(String username);

}
