package io.github.bigbio.pgatk.io.common;


import io.github.bigbio.pgatk.io.common.spectra.Spectrum;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The {@link MzIterableReader} is an interface that only provides methods to read one Spectrum after another
 * This interface and the classes behind allow to read an entire file without needs to index the entire file. For
 * indexed and random access to peak list files please use the JmzReader interface.
 *
 * @author yperez
 */
public interface MzIterableReader extends Serializable, Iterator<Spectrum> {


    /**
     * This method returns if the collection has more spectra after read the current
     * {@link Spectrum}
     *
     * @return Boolean if the file contains another spectra
     */
    boolean hasNext();

    /**
     * Return the next Spectra
     * @return
     */
    @Override
    Spectrum next() throws NoSuchElementException;

    /**
     * Close the respective channel and File use to parse the Spectra
     */
    void close() throws PgatkIOException;

}
