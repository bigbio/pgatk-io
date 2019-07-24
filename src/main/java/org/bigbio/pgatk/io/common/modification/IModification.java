package org.bigbio.pgatk.io.common.modification;

/**
 * Created by jg on 24.09.14.
 */
public interface IModification extends Comparable<IModification> {
    int getPosition();

    String getAccession();
}
