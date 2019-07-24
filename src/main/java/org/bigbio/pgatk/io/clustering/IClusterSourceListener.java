package org.bigbio.pgatk.io.clustering;


import org.bigbio.pgatk.io.clustering.objects.ICluster;

/**
 * Created by jg on 10.07.14.
 */
public interface IClusterSourceListener {
    public void onNewClusterRead(ICluster newCluster);
}
