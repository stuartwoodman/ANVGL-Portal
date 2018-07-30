package org.auscope.portal.server.vegl;

import org.springframework.data.repository.CrudRepository;


/**
 * VLScmSnapshot repository interface
 * 
 * @author woo392
 *
 */
public interface VLScmSnapshotRepository extends CrudRepository<VLScmSnapshot, Integer> {

    VLScmSnapshot findByScmEntryIdAndComputeServiceId(String scmEntryId, String computeServiceId);
}
