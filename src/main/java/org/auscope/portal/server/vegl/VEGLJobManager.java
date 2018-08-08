package org.auscope.portal.server.vegl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.auscope.portal.core.cloud.CloudJob;
import org.auscope.portal.core.services.PortalServiceException;
import org.auscope.portal.server.web.security.ANVGLUser;
import org.auscope.portal.server.web.security.NCIDetails;
import org.auscope.portal.server.web.security.NCIDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class that talks to the data objects to retrieve or save data
 *
 * @author Cihan Altinay
 * @author Josh Vote
 * @author Richard Goh
 */
public class VEGLJobManager {
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private VEGLJobRepository veglJobRepository;
    
    @Autowired
    private VEGLSeriesRepository veglSeriesRepository;
    
    @Autowired
    private VGLJobAuditLogRepository vglJobAuditLogRepository;
    
    @Autowired
    private NCIDetailsRepository nciDetailsRepository;

    public List<VEGLSeries> querySeries(String user, String name, String description) {
        //return veglSeriesDao.query(user, name, desc);
        return veglSeriesRepository.findByUserAndNameAndDescription(user, name, description);
    }

    public List<VEGLJob> getSeriesJobs(int seriesId, ANVGLUser user) throws PortalServiceException {
        //List<VEGLJob> jobs = veglJobDao.getJobsOfSeries(seriesId, user);
        // XXX Do we need to pass User and JPA relationship will know what we're talking about?
        List<VEGLJob> jobs = veglJobRepository.findBySeriesIdAndEmail(seriesId, user.getEmail());
        return applyNCIDetails(jobs, user);
    }

    public List<VEGLJob> getUserJobs(ANVGLUser user) throws PortalServiceException {
        //List<VEGLJob> jobs = veglJobDao.getJobsOfUser(user);
        List<VEGLJob> jobs = veglJobRepository.findByEmail(user.getEmail());
        return applyNCIDetails(jobs, user);
    }

    public List<VEGLJob> getPendingOrActiveJobs() {
        //return veglJobRepository.getPendingOrActiveJobs();
        return veglJobRepository.findPendingOrActive();
    }

    public List<VEGLJob> getInQueueJobs() {
        //return veglJobRepository.getInQueueJobs();
        return veglJobRepository.findInQueue();
    }

    public VEGLJob getJobById(int jobId, ANVGLUser user) throws PortalServiceException {
        //return applyNCIDetails(veglJobDao.get(jobId, user), user);
        return applyNCIDetails(veglJobRepository.findByIdAndEmailAddress(jobId, user.getEmail()), user);
    }

    public VEGLJob getJobById(int jobId, String stsArn, String clientSecret, String s3Role, String userEmail, String nciUser, String nciProj, String nciKey) {
        //return veglJobRepository.get(jobId, stsArn, clientSecret, s3Role, userEmail, nciUser, nciProj, nciKey);
        VEGLJob job = veglJobRepository.findByIdAndEmailAddress(jobId, userEmail);
        // This was moved out of VEGLJobDao 
        job.setProperty(CloudJob.PROPERTY_STS_ARN, stsArn);
        job.setProperty(CloudJob.PROPERTY_CLIENT_SECRET, clientSecret);
        job.setProperty(CloudJob.PROPERTY_S3_ROLE, s3Role);
        job.setProperty(NCIDetails.PROPERTY_NCI_USER, nciUser);
        job.setProperty(NCIDetails.PROPERTY_NCI_PROJECT, nciProj);
        job.setProperty(NCIDetails.PROPERTY_NCI_KEY, nciKey);
        return job;
    }

    public void deleteJob(VEGLJob job) {
        veglJobRepository.delete(job);
    }

    public VEGLSeries getSeriesById(int seriesId, String userEmail) {
        return veglSeriesRepository.findByIdAndUser(seriesId, userEmail);
    }

    public void saveJob(VEGLJob veglJob) {
        veglJobRepository.save(veglJob);
    }

    /**
     * Create the job life cycle audit trail. If the creation is unsuccessful, it
     * will silently fail and log the failure message to error log.
     * @param oldJobStatus
     * @param curJob
     * @param message
     */
    public void createJobAuditTrail(String oldJobStatus, VEGLJob curJob, String message) {
        VGLJobAuditLog vglJobAuditLog = null;
        try {
            vglJobAuditLog = new VGLJobAuditLog();
            vglJobAuditLog.setJobId(curJob.getId());
            vglJobAuditLog.setFromStatus(oldJobStatus);
            vglJobAuditLog.setToStatus(curJob.getStatus());
            vglJobAuditLog.setTransitionDate(new Date());
            vglJobAuditLog.setMessage(message);

            // Failure in the creation of the job life cycle audit trail is
            // not critical hence we allow it to fail silently and log it.
            vglJobAuditLogRepository.save(vglJobAuditLog);
        } catch (Exception ex) {
            logger.warn("Error creating audit trail for job: " + vglJobAuditLog, ex);
        }
    }

    /**
     * Create the job life cycle audit trail. If the creation is unsuccessful, it
     * will silently fail and log the failure message to error log.
     * @param oldJobStatus
     * @param curJob
     * @param message
     */
    public void createJobAuditTrail(String oldJobStatus, VEGLJob curJob, Throwable exception) {
        String message = ExceptionUtils.getStackTrace(exception);
        if(message.length() > 1000){
            message = message.substring(0,1000);
        }
        VGLJobAuditLog vglJobAuditLog = null;
        try {
            vglJobAuditLog = new VGLJobAuditLog();
            vglJobAuditLog.setJobId(curJob.getId());
            vglJobAuditLog.setFromStatus(oldJobStatus);
            vglJobAuditLog.setToStatus(curJob.getStatus());
            vglJobAuditLog.setTransitionDate(new Date());
            vglJobAuditLog.setMessage(message);

            // Failure in the creation of the job life cycle audit trail is
            // not critical hence we allow it to fail silently and log it.
            vglJobAuditLogRepository.save(vglJobAuditLog);
        } catch (Exception ex) {
            logger.warn("Error creating audit trail for job: " + vglJobAuditLog, ex);
        }
    }

    public void deleteSeries(VEGLSeries series) {
        veglSeriesRepository.delete(series);
    }

    public void saveSeries(VEGLSeries series) {
        veglSeriesRepository.save(series);
    }

    // Injected, but methods needed for tests
    public void setVeglJobRepository(VEGLJobRepository veglJobRepository) {
        this.veglJobRepository = veglJobRepository;
    }

    public void setVeglSeriesRepository(VEGLSeriesRepository veglSeriesRepository) {
        this.veglSeriesRepository = veglSeriesRepository;
    }

    public void setVglJobAuditLogRepository(VGLJobAuditLogRepository vglJobAuditLogRepository) {
        this.vglJobAuditLogRepository = vglJobAuditLogRepository;
    }

    public void setNciDetailsRepository(NCIDetailsRepository nciDetailsRepository) {
        this.nciDetailsRepository = nciDetailsRepository;
    }

    /*
    public NCIDetailsRepository getNciDetailsRepository() {
        return nciDetailsRepository;
    }
    */
    
    private VEGLJob applyNCIDetails(VEGLJob job, NCIDetails nciDetails) {
        if (nciDetails != null) {
            try {
                nciDetails.applyToJobProperties(job);
            } catch (Exception e) {
                logger.error("Unable to apply nci details to job:", e);
                throw new RuntimeException("Unable to decrypt NCI Details", e);
            }
        }

        return job;
    }

    private VEGLJob applyNCIDetails(VEGLJob job, ANVGLUser user) throws PortalServiceException {
        if (job == null) {
            return null;
        }
        return applyNCIDetails(job, nciDetailsRepository.findByUser(user));
    }

    private List<VEGLJob> applyNCIDetails(List<VEGLJob> jobs, ANVGLUser user) throws PortalServiceException {
        NCIDetails nciDetails = nciDetailsRepository.findByUser(user);

        if (nciDetails != null) {
            for (VEGLJob job: jobs) {
                applyNCIDetails(job, nciDetails);
            }
        }
        return jobs;
    }

}