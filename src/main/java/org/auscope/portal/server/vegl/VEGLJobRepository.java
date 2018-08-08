package org.auscope.portal.server.vegl;

import java.util.List;

import org.auscope.portal.server.web.controllers.JobBuilderController;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


/**
 * VEGLJob repository interface
 * 
 * @author woo392
 *
 */
public interface VEGLJobRepository extends CrudRepository<VEGLJob, Integer> {
    //List<VEGLJob> findBySeriesIdAndStatusNot(Integer seriesId, String status);
    //List<VEGLJob> findByUser(String userId);    
    
    // Was VEGLJobDao.getJobsOfSeries(seriesId, user)
    // More complex query required because VEGLJob stores User's name and email, not an ANVGLUser for some reason 
    @Query("select j from VEGLJob j where j.seriesId = ?1 and j.emailAddress = ?2 and lower(j.status)!='deleted'")
    List<VEGLJob> findBySeriesIdAndEmail(Integer seriesId, String email);
    
    // Was getJobsOfUser(String user)
    @Query("select j from VEGLJob j where j.emailAddress = ?1 and lower(j.status)!='deleted'")
    List<VEGLJob> findByEmail(String email);
    
    // Was VEGLJobDao.getPendingOrActiveJobs()
    @Query("select j from VEGLJob j where lower(j.status)='"
                + JobBuilderController.STATUS_PENDING +
                "' or lower(j.status)='"
                + JobBuilderController.STATUS_ACTIVE + "'")
    List<VEGLJob> findPendingOrActive();
    
    // Was VEGLJobDao.getInQueueJobs()
    @Query("select j from VEGLJob j where lower(j.status)='"
                + JobBuilderController.STATUS_INQUEUE + "'")
    List<VEGLJob> findInQueue();
    
    // Was veglJobRepository.get(int jobId, String stsArn, String clientSecret, String s3Role, String userEmail,
    //                           String nciUser, String nciProj, String nciKey);
    // XXX Not needed I think, basically the following method, but the extra
    // params were added to the job by the VEGLJobDao, shifted to calling class
    //VEGLJob findByIdAndStsArnAndClientSecretAndS3RoleAndUserEmailAndNciUserAndNciProjAndNciKey(Integer jobId, String stsArn, String clientSecret, String s3Role, String userEmail, String nciUser, String nciProj, String nciKey);
    
    // Was VEGLJobDao.get(int jobId, ANVGLUser user)
    VEGLJob findByIdAndEmailAddress(Integer id, String email);
}
