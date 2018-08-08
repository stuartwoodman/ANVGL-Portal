package org.auscope.portal.server.web.security.social;

import java.util.List;

import org.auscope.portal.server.web.security.ANVGLAuthority;
import org.auscope.portal.server.web.security.ANVGLAuthorityRepository;
import org.auscope.portal.server.web.security.ANVGLUser;
import org.auscope.portal.server.web.security.ANVGLUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * 
 * 
 * @author woo392
 */
public class VglUserDetailsService implements UserDetailsService {

    @Autowired
    ANVGLUserRepository userRepository;
    
    @Autowired
    ANVGLAuthorityRepository authorityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        System.out.println("UserDetailsServiceImpl.loadUserByUsername=" + username);

        // XXX Should this be ID/email?
        //ANVGLUser vglUser = this.userRepository.findByUsername(username);
        ANVGLUser vglUser = this.userRepository.findByEmail(username);

        if (vglUser == null) {
            System.out.println("User not found! " + username);
            throw new UsernameNotFoundException("User " + username + " was not found in the database");
        }
        
        System.out.println("Found User: " + vglUser);

        // [ROLE_USER, ROLE_ADMIN,..]
        //List<String> roleNames = this.appRoleDAO.getRoleNames(appUser.getUserId());
        List<ANVGLAuthority> authorities = this.authorityRepository.findByParent_Id(vglUser.getId());

        /*
        // Skip, ANVGLAuthority is type of GrantedAuthority
        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
        if (roleNames != null) {
            for (String role : roleNames) {
                // ROLE_USER, ROLE_ADMIN,..
                GrantedAuthority authority = new SimpleGrantedAuthority(role);
                grantList.add(authority);
            }
        }
        */
        VglSocialUserDetails userDetails = new VglSocialUserDetails(vglUser, authorities/*roleNames*/);
        return userDetails;
    }

}
