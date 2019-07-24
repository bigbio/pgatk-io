package org.bigbio.pgatk.io.clustering.objects;

/**
 * Created by jg on 24.09.14.
 */
public interface IModification extends Comparable<IModification> {
    public int getPosition();

    public String getAccession();
}
