package org.auscope.portal.server.web.security.social;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.auscope.portal.server.web.security.ANVGLAuthority;
import org.auscope.portal.server.web.security.ANVGLUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.social.security.SocialUserDetails;


public class VglSocialUserDetails implements SocialUserDetails {

    private static final long serialVersionUID = -1637803254223276379L;
    
    private List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
    // TODO: Change to ANVGLUser
    //private AppUser appUser;
    private ANVGLUser vglUser;

    //public VglSocialUserDetails(/*AppUser appUser*/ ANVGLUser vglUser, List<String> roleNames) {
    public VglSocialUserDetails(/*AppUser appUser*/ ANVGLUser vglUser, List<ANVGLAuthority> authorities) {
        //this.appUser = appUser;
        this.vglUser = vglUser;

        /*
        // Now passing ANVGLAuthority which is already a GrantedAuthority
        for (String roleName : roleNames) {
            GrantedAuthority grant = new SimpleGrantedAuthority(roleName);
            this.list.add(grant);
        }
        */
        for (ANVGLAuthority authority : authorities) {
            GrantedAuthority grant = new SimpleGrantedAuthority(authority.getAuthority());
            this.list.add(grant);
        }
    }

    @Override
    public String getUserId() {
        //return this.appUser.getUserId() + "";
        return this.vglUser.getId() + "";
    }

    @Override
    public String getUsername() {
        //return appUser.getUserName();
        return vglUser.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return list;
    }

    @Override
    public String getPassword() {
        //return appUser.getEncrytedPassword();
        return vglUser.getEncryptedPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
