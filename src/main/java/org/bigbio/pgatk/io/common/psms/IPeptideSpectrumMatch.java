package org.bigbio.pgatk.io.common.psms;

import org.bigbio.pgatk.io.common.modification.IModification;

import java.util.List;

/**
 * Created by jg on 24.09.14.
 */
public interface IPeptideSpectrumMatch {

    String getSequence();

    List<IModification> getModifications();


}
