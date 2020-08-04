package io.github.bigbio.pgatk.io.pride;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paranamer.ParanamerModule;
import io.github.bigbio.pgatk.io.braf.BufferedRandomAccessFile;
import io.github.bigbio.pgatk.io.common.MzIterableReader;
import io.github.bigbio.pgatk.io.common.PgatkIOException;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PrideJsonIterableReader implements MzIterableReader {

    File source;
    private static final ObjectMapper objectMapper;
    private BufferedRandomAccessFile braf;
    private String line = null;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParanamerModule());
    }

    /**
     * Constructor of {@link PrideJsonIndexedReader}
     * @param source File with the spectra
     * @throws PgatkIOException
     */
    public PrideJsonIterableReader(File source) throws PgatkIOException {
        this.source = source;
        try {
            braf = new BufferedRandomAccessFile(this.source.getAbsolutePath(),
                    "r", 1024 * 100);
        }catch (IOException e){
            throw new PgatkIOException("Failed to read from PrideJson file.", e);
        }

    }

    @Override
    public boolean hasNext() {
        // process the file line by line
        try {
            line = braf.getNextLine();
            return (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Spectrum next() throws NoSuchElementException {
        try {
            return objectMapper.readValue(line, ArchiveSpectrum.class);
        } catch (JsonProcessingException e) {
            throw new NoSuchElementException(e.getMessage());
        }
    }

    @Override
    public void close() throws PgatkIOException {
        try{
            braf.close();
        }catch (IOException e){
            throw new PgatkIOException("Error closing the file", e);
        }

    }
}
