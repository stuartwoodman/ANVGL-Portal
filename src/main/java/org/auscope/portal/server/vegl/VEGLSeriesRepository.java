package org.auscope.portal.server.vegl;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * VEGLSeries repository interface
 * 
 * @author woo392
 *
 */
public interface VEGLSeriesRepository extends CrudRepository<VEGLSeries, Integer> {

    // Was VEGLSeriesDao.query(final String user, final String name, final String desc)
    // XXX We may need to make description otional
    List<VEGLSeries> findByUserAndNameAndDescription(String user, String name, String description);
    
    // Was VEGLSeries.get(final int id, String userEmail)
    // XXX This may need to be ANVGLUser, but VEGLSeries refers to user (email)
    VEGLSeries findByIdAndUser(Integer id, String user);
}
