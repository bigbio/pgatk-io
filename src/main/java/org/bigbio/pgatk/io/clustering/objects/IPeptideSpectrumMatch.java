package org.bigbio.pgatk.io.clustering.objects;

import java.util.List;

/**
 * Created by jg on 24.09.14.
 */
public interface IPeptideSpectrumMatch {
    String getSequence();

    List<IModification> getModifications();


}
