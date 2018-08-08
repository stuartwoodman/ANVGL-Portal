package org.auscope.portal.server.web.security.social;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.auscope.portal.server.web.security.ANVGLAuthority;
import org.auscope.portal.server.web.security.ANVGLUser;
import org.auscope.portal.server.web.security.ANVGLUserRepository;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;


/**
 * 
 * 
 * @author woo392
 * 
 */
public class VglConnectionSignUp implements ConnectionSignUp {

    private ANVGLUserRepository userRepository;
    
    public VglConnectionSignUp(ANVGLUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // After login, create user if doesn't already exist
    @Override
    public String execute(Connection<?> connection) {
 
        // TODO: Change AppUser to ANVGLUser, using initially for DB differences
        //ANVGLUser account = userRepository.createVglUser(connection);
        // XXX The following was moved out of AppUserDAO
        ConnectionKey key = connection.getKey();
        // (facebook,12345), (google,123) ...
        System.out.println("key= (" + key.getProviderId() + "," + key.getProviderUserId() + ")");
  
        UserProfile userProfile = connection.fetchUserProfile();
        String email = userProfile.getEmail();
        
        //AppUser appUser = this.findByEmail(email);
        ANVGLUser vglUser = userRepository.findByEmail(email);
        
        if (vglUser == null) {
            /*
            String userName_prefix = userProfile.getFirstName().trim().toLowerCase()
                    + "_" + userProfile.getLastName().trim().toLowerCase();
      
            //String userName = this.findAvailableUserName(userName_prefix);
            String username = email;
            */
            
            // XXX Deal with this! XXX
            // 
            // Random Password! TODO: Need send email to User!
            //
            String randomPassword = UUID.randomUUID().toString().substring(0, 5);
            String encryptedPassword = EncryptedPasswordUtils.encryptPassword(randomPassword);
            
            vglUser = new ANVGLUser();
            //vglUser.setEnabled(true);
            vglUser.setEncryptedPassword(encryptedPassword);
            //vglUser.setUsername(email);
            vglUser.setEmail(email);
            //vglUser.setFirstName(userProfile.getFirstName());
            //vglUser.setLastName(userProfile.getLastName());
            String fullName = userProfile.getFirstName() + userProfile.getLastName();
            vglUser.setFullName(fullName);
            
            // Create default Role
            //List<String> roleNames = new ArrayList<String>();
            List<ANVGLAuthority> roleNames = new ArrayList<ANVGLAuthority>();
            // XXX Do it properly
            //roleNames.add(AppRole.ROLE_USER);
            roleNames.add(new ANVGLAuthority("ROLE_USER"));
            //this.appRoleDAO.createRoleFor(appUser, roleNames);
            vglUser.setAuthorities(roleNames);
      
            this.userRepository.save(vglUser);
        }
        
        // XXX Email, ID?
        //return account.getUsername();
        return vglUser.getId();
    }

}
