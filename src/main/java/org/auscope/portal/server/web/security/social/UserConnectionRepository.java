package org.auscope.portal.server.web.security.social;

import org.springframework.data.repository.CrudRepository;


public interface UserConnectionRepository extends CrudRepository<UserConnection, Integer> {
    
    // XXX This was userProviderId in tutorial, but doesn't exist
    UserConnection findByProviderUserId(String providerUserId);
}
