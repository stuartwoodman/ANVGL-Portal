package org.auscope.portal.server.web.security.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;


@Service
public class VglSocialUserDetailsService implements SocialUserDetailsService {
    
    @Autowired
    private SocialUserDetailsService userDetailsService;
    
    // XXX This passes userName in example code is based on
    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        System.out.println("SocialUserDetailsServiceImpl.loadUserByUserId=" + userId);
        
        // See UserDetailServiceImpl.
        UserDetails userDetails = ((VglUserDetailsService)userDetailsService).loadUserByUsername(userId);
 
        return (VglSocialUserDetails)userDetails;
    }

}
