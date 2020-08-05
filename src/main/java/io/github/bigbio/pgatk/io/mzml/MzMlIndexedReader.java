package io.github.bigbio.pgatk.io.mzml;

import io.github.bigbio.pgatk.io.common.IndexElementImpl;
import io.github.bigbio.pgatk.io.common.MzIterableReader;
import io.github.bigbio.pgatk.io.common.MzReader;
import io.github.bigbio.pgatk.io.common.PgatkIOException;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;
import psidev.psi.tools.xxindex.index.IndexElement;
import uk.ac.ebi.jmzml.MzMLElement;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import java.io.File;
import java.util.*;

/**
 * Wrapper class around the jmzml parser library that's implementing the JMzReader interface.
 *
 * @author jg
 * @author ypriverol
 */
public class MzMlIndexedReader implements MzReader, MzIterableReader {

    private HashMap<String, psidev.psi.tools.xxindex.index.IndexElement> idToIndexElementMap;
    private HashMap<Integer, List<psidev.psi.tools.xxindex.index.IndexElement>> msNScans;

    private Iterator<String> idIterator;
    /**
     * MzML cvParams to be used to extract
     * required parameters from the spectra.
     *
     * @author jg
     */
    public enum MZML_PARAMS {
        SELECTED_MZ("MS:1000744"),
        PEAK_INTENSITY("MS:1000042"),
        CHARGE_STATE("MS:1000041"),
        MS_LEVEL("MS:1000511");

        MZML_PARAMS(String accession) {
            this.accession = accession;
        }

        private final String accession;

        public String getAccess() {
            return accession;
        }
    }

    /**
     * The unmarshaller to use to unmarshall
     * the mzML objects.
     */
    private final MzMLUnmarshaller unmarshaller;
    /**
     * List of spectra found in the mzML
     * file.
     */
    private final List<String> spectraIds;

    /**
     * Creates a new MzMlIndexedReader object parsing
     * the passed mzML file.
     *
     * @param sourcefile The mzML file to parse.
     * @throws PgatkIOException Thrown in case the mzML file cannot be parsed correctly.
     */
    public MzMlIndexedReader(File sourcefile) throws PgatkIOException {
        // unmarshal the file
        try {
            unmarshaller = new MzMLUnmarshaller(sourcefile);

            // save the spectra ids
            spectraIds = new ArrayList<>(unmarshaller.getSpectrumIDs());

            //initialize spectrum maps
            initializeSpectrumMaps();
            idIterator = spectraIds.iterator();

        } catch (RuntimeException e) {
            throw new PgatkIOException("Failed to parse mzML file.", e);
        }
    }

    /**
     * Init spectra map for reading.
     */
    private void initializeSpectrumMaps() {

        List<psidev.psi.tools.xxindex.index.IndexElement> spectra = unmarshaller.getMzMLIndexer().getIndexElements(MzMLElement.Spectrum.getXpath());

        idToIndexElementMap = new HashMap<>(spectra.size());
        msNScans = new HashMap<>(spectra.size());

        for (psidev.psi.tools.xxindex.index.IndexElement element : spectra) {

            //unmarshall spectrum
            uk.ac.ebi.jmzml.model.mzml.Spectrum spectrum = unmarshaller.unmarshalFromIndexElement(element, uk.ac.ebi.jmzml.model.mzml.Spectrum.class);

                //store id-indexElement
            idToIndexElementMap.put(spectrum.getId(), element);

            int msLevel = -1;
            for (uk.ac.ebi.jmzml.model.mzml.CVParam param : spectrum.getCvParam()) {
                if (param.getAccession().equals("MS:1000511")) {
                    msLevel = Integer.parseInt(param.getValue());
                }
            }
            if (!msNScans.containsKey(msLevel))
                msNScans.put(msLevel, new ArrayList<>());
                msNScans.get(msLevel).add(element);
        }


    }

    public int getSpectraCount() {
        return spectraIds.size();
    }

    public boolean acceptsFile() {
        return true;
    }

    public boolean acceptsDirectory() {
        return false;
    }

    public List<String> getSpectraIds() {
        return spectraIds;
    }

    public Spectrum getSpectrumById(String id) throws PgatkIOException {
        try {
            uk.ac.ebi.jmzml.model.mzml.Spectrum mzMlSpectrum = unmarshaller.getSpectrumById(id);
            int index = spectraIds.indexOf(id);

            return new MzMLSpectrum(mzMlSpectrum, (long)index);
        } catch (MzMLUnmarshallerException e) {
            throw new PgatkIOException("Failed to load spectrum " + id + " from mzML file.", e);
        }
    }

    public Spectrum getSpectrumByIndex(int index) throws PgatkIOException {

        if (index < 1 || index > spectraIds.size())
            throw new PgatkIOException("Index out of range.");

        String id = spectraIds.get(index - 1);

        return getSpectrumById(id);
    }

    @Override
    public List<io.github.bigbio.pgatk.io.common.IndexElement> getMsNIndexes(
            int msLevel) {
        if (!msNScans.containsKey(msLevel))
            return Collections.emptyList();

        return convertIndexElements(msNScans.get(msLevel));
    }

    @Override
    public List<Integer> getMsLevels() {
        return new ArrayList<>(msNScans.keySet());
    }

    @Override
    public Map<String, io.github.bigbio.pgatk.io.common.IndexElement> getIndexElementForIds() {
        Map<String, io.github.bigbio.pgatk.io.common.IndexElement> idToIndex =
                new HashMap<>(idToIndexElementMap.size());

        for (Map.Entry<String, IndexElement> stringIndexElementEntry : idToIndexElementMap.entrySet()) {
            psidev.psi.tools.xxindex.index.IndexElement e = stringIndexElementEntry.getValue();
            int size = (int) (e.getStop() - e.getStart());
            idToIndex.put(stringIndexElementEntry.getKey(), new IndexElementImpl(e.getStart(), size));
        }

        return idToIndex;
    }

    /**
     * Converts a list of xxindex IndexElementS to a
     * list of JMzReader IndexElementS.
     *
     * @param index
     * @return
     */
    private List<io.github.bigbio.pgatk.io.common.IndexElement> convertIndexElements(List<psidev.psi.tools.xxindex.index.IndexElement> index) {
        List<io.github.bigbio.pgatk.io.common.IndexElement> convertedIndex =
                new ArrayList<>(index.size());

        for (psidev.psi.tools.xxindex.index.IndexElement e : index) {
            int size = (int) (e.getStop() - e.getStart());
            convertedIndex.add(new IndexElementImpl(e.getStart(), size));
        }

        return convertedIndex;
    }

    @Override
    public boolean hasNext() {
            return idIterator.hasNext();
    }

    @Override
    public Spectrum next() {
        try {
            return getSpectrumById(idIterator.next());
        } catch (PgatkIOException e) {
            throw new RuntimeException("Failed to parse mzML spectrum.", e);
        }
    }

    @Override
    public void close() {

    }


}
