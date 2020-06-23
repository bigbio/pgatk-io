package io.github.bigbio.pgatk.io.clustering;


import io.github.bigbio.pgatk.io.common.cluster.ICluster;

/**
 * Created by jg on 10.07.14.
 */
public interface IClusterSourceListener {
    void onNewClusterRead(ICluster newCluster);
}
