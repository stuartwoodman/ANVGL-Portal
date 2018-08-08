package org.auscope.portal.server.web.security.social;

import java.util.List;

import org.auscope.portal.server.web.security.ANVGLAuthority;
import org.auscope.portal.server.web.security.ANVGLUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.security.SocialUserDetails;


/**
 * 
 * 
 * @author woo392
 *
 */
public class SecurityUtil {

    // Auto login
    //public static void logInUser(ANVGLUser user, List<String> roleNames) {
    public static void logInUser(ANVGLUser user, List<ANVGLAuthority> authorities) {

        //SocialUserDetails userDetails = new VglSocialUserDetails(user, roleNames);
        SocialUserDetails userDetails = new VglSocialUserDetails(user, authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
