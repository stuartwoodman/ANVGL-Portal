package org.auscope.portal.server.web.security.social;

import java.io.Serializable;
import java.util.Objects;

/**
 * UserConnection uses a compound key, so it needs an ID class
 * 
 * @author woo392
 *
 */
public class UserConnectionId implements Serializable {

    private static final long serialVersionUID = 3602411743343500799L;
    
    private String userId;
    private String providerId;
    private String providerUserId;
    
    public UserConnectionId() {}
    
    public UserConnectionId(String userId, String providerId, String providerUserId) {
        this.userId = userId;
        this.providerId = providerId;
        this.providerUserId = providerUserId;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!(o instanceof UserConnection)) {
            return false;
        }
        UserConnection uc = (UserConnection)o;
        return (userId.equals(uc.getUserId())) &&
                    (providerId.equals(uc.getProviderId())) &&
                    (providerUserId.equals(uc.getProviderUserId()));
    }
    
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, providerId, providerUserId);
    }
}