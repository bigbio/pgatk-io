package org.bigbio.pgatk.io.clustering;

import org.bigbio.pgatk.io.common.cluster.ClusteringFileSpectrumReference;

import java.util.Comparator;

/**
 * Created by jg on 05.01.15.
 */
@Deprecated
public class PeakMzComparator implements Comparator<ClusteringFileSpectrumReference.Peak> {
    @Override
    public int compare(ClusteringFileSpectrumReference.Peak o1, ClusteringFileSpectrumReference.Peak o2) {
        return Double.compare(o1.getMz(), o2.getMz());
    }
}
