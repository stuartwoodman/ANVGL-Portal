package org.auscope.portal.server.vegl;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


/**
 * VGLJobAuditLog repository interface
 * 
 * @author woo392
 *
 */
public interface VGLJobAuditLogRepository extends CrudRepository<VGLJobAuditLog, Integer> {
    
    List<VGLJobAuditLog> findByJobId(Integer jobId);

}
