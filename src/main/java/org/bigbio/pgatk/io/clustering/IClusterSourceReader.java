package org.bigbio.pgatk.io.clustering;

import org.bigbio.pgatk.io.common.MzIterableReader;
import org.bigbio.pgatk.io.common.cluster.ICluster;

import java.util.Collection;
import java.util.List;

/**
 * Created by jg on 10.07.14.
 */
public interface IClusterSourceReader extends MzIterableReader {
    /**
     * This function reads all clusters from the clustering source. Spectra
     * are never included in this output.
     * @return
     * @throws Exception
     */
    List<ICluster> readAllClusters() throws Exception;

    boolean supportsReadAllClusters();

    /**
     * This function includes spectra if they are available.
     * @param listeners
     * @throws Exception
     */
    void readClustersIteratively(Collection<IClusterSourceListener> listeners) throws Exception;

    /**
     * Read a specific cluster.
     * @param id The cluster's id
     * @return The cluster.
     */
    ICluster readCluster(String id) throws Exception;
}
