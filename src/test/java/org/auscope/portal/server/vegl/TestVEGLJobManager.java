package org.auscope.portal.server.vegl;

import java.util.Arrays;
import java.util.List;

import org.auscope.portal.core.services.PortalServiceException;
import org.auscope.portal.core.test.PortalTestClass;
import org.auscope.portal.server.web.controllers.JobBuilderController;
import org.auscope.portal.server.web.security.ANVGLUser;
import org.auscope.portal.server.web.security.NCIDetails;
import org.auscope.portal.server.web.security.NCIDetailsRepository;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataRetrievalFailureException;

/**
 * Unit tests for VEGLJobDao
 * @author Richard Goh
 */
public class TestVEGLJobManager extends PortalTestClass {
    private VEGLJobManager jobManager;
    /*
    private VEGLJobDao mockJobDao;
    private VEGLSeriesDao mockSeriesDao;
    private VGLJobAuditLogDao mockJobAuditLogDao;
    private NCIDetailsDao mockNciDetailsDao;
    */
    private VEGLJobRepository mockJobRepository;
    private VEGLSeriesRepository mockSeriesRepository;
    private VGLJobAuditLogRepository mockJobAuditLogRepository;
    private NCIDetailsRepository mockNciDetailsRepository;
    
    //private final String TEST_ENC_KEY = "unit-testing-key";

    /**
     * Load our mock objects
     */
    @Before
    public void init() {
        // Setting up mock objects needed for Object Under Test (OUT)
        mockJobRepository = context.mock(VEGLJobRepository.class);
        mockSeriesRepository = context.mock(VEGLSeriesRepository.class);
        mockJobAuditLogRepository = context.mock(VGLJobAuditLogRepository.class);
        mockNciDetailsRepository= context.mock(NCIDetailsRepository.class);
        // Object Under Test
        jobManager = new VEGLJobManager();
        jobManager.setVeglJobRepository(mockJobRepository);
        jobManager.setVeglSeriesRepository(mockSeriesRepository);
        jobManager.setVglJobAuditLogRepository(mockJobAuditLogRepository);
        jobManager.setNciDetailsRepository(mockNciDetailsRepository);
    }

    /**
     * Tests that querying job series of a given criteria succeeds.
     */
    @Test
    public void testQuerySeries() {
        final String user = "user@email.com";
        final String name = "user";
        final String desc = "series description";
        final VEGLSeries mockSeries = context.mock(VEGLSeries.class);
        final List<VEGLSeries> seriesList = Arrays.asList(mockSeries);

        context.checking(new Expectations() {{
            oneOf(mockSeriesRepository).findByUserAndNameAndDescription(user, name, desc);
            will(returnValue(seriesList));
        }});

        Assert.assertNotNull(jobManager.querySeries(user, name, desc));
    }

    /**
     * Tests that retrieving jobs of a given series succeeds.
     * @throws PortalServiceException
     */
    @Test
    public void testGetSeriesJobs() throws PortalServiceException {
        final int seriesId = 1;
        final VEGLJob mockJob = context.mock(VEGLJob.class);
        final List<VEGLJob> jobList = Arrays.asList(mockJob);
        final ANVGLUser user = new ANVGLUser();

        context.checking(new Expectations() {{
            oneOf(mockJobRepository).findByIdAndEmailAddress(seriesId, user.getEmail());will(returnValue(jobList));
            oneOf(mockNciDetailsRepository).findByUser(user);will(returnValue(null));
        }});

        Assert.assertNotNull(jobManager.getSeriesJobs(seriesId, user));
    }

    /**
     * Tests that retrieving job of a given id succeeds.
     * null is return when a job cannot be found.
     */
    @Test
    public void testGetJobById() throws Exception {
        final int jobId1 = 1;
        final int jobId2 = 2;
        final VEGLJob mockJob = context.mock(VEGLJob.class);
        final ANVGLUser user = new ANVGLUser();
        final NCIDetails nciDetails = new NCIDetails();

        nciDetails.setKey("mykey");
        nciDetails.setProject("myproj");
        nciDetails.setUsername("myuser");

        context.checking(new Expectations() {{
            oneOf(mockJobRepository).findByIdAndEmailAddress(jobId1, user.getEmail());will(returnValue(mockJob));
            oneOf(mockJobRepository).findByIdAndEmailAddress(jobId2, user.getEmail());will(returnValue(null));
            oneOf(mockNciDetailsRepository).findByUser(user);will(returnValue(nciDetails));

            oneOf(mockJob).setProperty(NCIDetails.PROPERTY_NCI_KEY, "mykey");
            oneOf(mockJob).setProperty(NCIDetails.PROPERTY_NCI_PROJECT, "myproj");
            oneOf(mockJob).setProperty(NCIDetails.PROPERTY_NCI_USER, "myuser");
        }});

        Assert.assertNotNull(jobManager.getJobById(jobId1, user));
        Assert.assertNull(jobManager.getJobById(jobId2, user));
    }

    /**
     * Tests that the deleting of a given job succeeds.
     */
    @Test
    public void testDeleteJob() {
        final VEGLJob mockJob = context.mock(VEGLJob.class);

        context.checking(new Expectations() {{
            oneOf(mockJobRepository).delete(mockJob);
        }});

        jobManager.deleteJob(mockJob);
    }

    /**
     * Tests that retrieving series of a give id succeeds.
     * null is returned when a series cannot be found.
     */
    @Test
    public void testGetSeriesById() {
        final String userEmail= "dummy@dummy.comn";
        final int series1 = 1;
        final int series2 = 2;
        final VEGLSeries mockSeries = context.mock(VEGLSeries.class);

        context.checking(new Expectations() {{
            oneOf(mockSeriesRepository).findByIdAndUser(series1, userEmail);
            will(returnValue(mockSeries));
            oneOf(mockSeriesRepository).findByIdAndUser(series2, userEmail);
            will(returnValue(null));
        }});

        Assert.assertNotNull(jobManager.getSeriesById(series1, userEmail));
        // Test to ensure null is returned when user's signature
        // cannot be found.
        Assert.assertNull(jobManager.getSeriesById(series2, userEmail));
    }

    /**
     * Tests that the storing of a given job succeeds.
     */
    @Test
    public void testSaveJob() {
        final VEGLJob mockJob = context.mock(VEGLJob.class);

        context.checking(new Expectations() {{
            oneOf(mockJobRepository).save(mockJob);
        }});

        jobManager.saveJob(mockJob);
    }

    /**
     * Tests that creating job audit trail succeeds.
     */
    @Test
    public void testCreateJobAuditTrail() {
        final String oldJobStatus = JobBuilderController.STATUS_UNSUBMITTED;
        final VEGLJob mockCurJob = context.mock(VEGLJob.class);
        final String message = "Job submitted";

        context.checking(new Expectations() {{
            oneOf(mockCurJob).getId();
            will(returnValue(1));
            oneOf(mockCurJob).getStatus();
            will(returnValue(JobBuilderController.STATUS_PENDING));
            oneOf(mockJobAuditLogRepository).save(with(any(VGLJobAuditLog.class)));
        }});

        jobManager.createJobAuditTrail(oldJobStatus, mockCurJob, message);
    }

    /**
     * Tests that creating job audit trail fails.
     */
    @Test
    public void testCreateJobAuditTrail_Exception() {
        final String oldJobStatus = JobBuilderController.STATUS_UNSUBMITTED;
        final VEGLJob mockCurJob = context.mock(VEGLJob.class);
        final String message = "Job submitted";

        context.checking(new Expectations() {{
            oneOf(mockCurJob).getId();
            will(returnValue(1));
            oneOf(mockCurJob).getStatus();
            will(returnValue(JobBuilderController.STATUS_PENDING));
            oneOf(mockJobAuditLogRepository).save(with(any(VGLJobAuditLog.class)));
            will(throwException(new DataRetrievalFailureException("")));
        }});

        jobManager.createJobAuditTrail(oldJobStatus, mockCurJob, message);
    }

    /**
     * Tests that deleting a given series succeeds.
     */
    @Test
    public void testDeleteSeries() {
        final VEGLSeries mockSeries = context.mock(VEGLSeries.class);

        context.checking(new Expectations() {{
            oneOf(mockSeriesRepository).delete(mockSeries);
        }});

        jobManager.deleteSeries(mockSeries);
    }

    /**
     * Tests that storing a given series succeeds.
     */
    @Test
    public void testSaveSeries() {
        final VEGLSeries mockSeries = context.mock(VEGLSeries.class);

        context.checking(new Expectations() {{
            oneOf(mockSeriesRepository).save(mockSeries);
        }});

        jobManager.saveSeries(mockSeries);
    }
}