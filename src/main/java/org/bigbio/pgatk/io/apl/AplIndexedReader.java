package org.bigbio.pgatk.io.apl;

import lombok.extern.slf4j.Slf4j;
import org.bigbio.pgatk.io.braf.BufferedRandomAccessFile;
import org.bigbio.pgatk.io.common.*;
import org.bigbio.pgatk.io.common.spectra.Spectrum;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This class implement the JmzReader Interface to provide the information of
 * apl spectra.
 *
 * @author ypriverol
 *
 */
@Slf4j
public class AplIndexedReader implements MzReader, MzIterableReader {


    public String getFormat() {
        return "Andromeda peaklist file";
    }

    /**
     * Regex to recognize a attribute and extract its name and value
     */
    public static final Pattern attributePattern = Pattern.compile("(\\w+)=(.*)\\s*");

    /**
     * The source file if this object was generated from a file
     */
    private File sourceFile;
    /**
     * Position from the "BEGIN IONS" fields in the file to
     * the "END IONS"
     */
    private List<IndexElement> index = new ArrayList<>();
    /**
     * MS2 peak lists. The index of the query in the file as key
     * and the respective query as value.
     */
    private HashMap<Integer, AplSpectrum> peakLists;
    /**
     * Indicates whether the cache should be used
     */
    private boolean useCache = false;

    private int currentPosition = 0;

    private List<Integer> keys;

    /**
     * Loads a (MS2) spectrum from an  APL file who's
     * position in the file is already known.
     *
     * @param sourcefile   The APL file to load the spectrum from.
     * @param indexElement IndexElement specifying the position of the MS2 spectrum in the APL file.
     * @return The unmarshalled spectrum object.
     * @throws PgatkIOException Error processing the file..
     */
    public static Spectrum getIndexedSpectrum(File sourcefile, IndexElement indexElement) throws PgatkIOException {
        // make sure the parameters are set
        if (sourcefile == null)
            throw new PgatkIOException("Required parameter sourcefile must not be null.");
        if (indexElement == null)
            throw new PgatkIOException("Required parameter indexElement must not be null.");

        // load the spectrum from the file
        return loadIndexedQueryFromFile(sourcefile, indexElement, 1);
    }

    /**
     * Default constructor generating an empty APL file object.
     */
    public AplIndexedReader() {

    }

    /**
     * Creates the APL file object from an existing
     * APL file.
     *
     * @param file The APL file
     * @throws PgatkIOException Error processing the file.
     */
    public AplIndexedReader(File file) throws PgatkIOException {

        // open the file
        try {
            // save the file
            sourceFile = file;

            String path = file.getAbsolutePath();

            BufferedRandomAccessFile braf = new BufferedRandomAccessFile(path, "r", 1024 * 100);

            // process the file line by line
            String line;
            boolean inMs2 = false;
            long lastPosition = 0;
            long beginIonsIndex = 0; // the index where the last "peaklist end"" was encountered

            while ((line = braf.getNextLine()) != null) {

                // ignore empty lines
                if (line.length() < 1) {

                    //always update file pointer before continue
                    lastPosition = braf.getFilePointer();
                    continue;
                }

                // check if a ms2 block started
                if (!inMs2 && line.contains("peaklist start")) {
                    // save the offset of the spectrum
                    beginIonsIndex = lastPosition;
                    inMs2 = true;
                }
                if (inMs2 && line.contains("peaklist end")) {
                    inMs2 = false;

                    //index.add(new IndexElement(beginIonsIndex, reader.getFilePointer()));
                    int size = (int) (braf.getFilePointer() - beginIonsIndex);
                    index.add(new IndexElementImpl(beginIonsIndex, size));

                    //always update file pointer before continue
                    lastPosition = braf.getFilePointer();
                    continue;
                }

                //always update file pointer before continue
                lastPosition = braf.getFilePointer();
            }
            peakLists= new HashMap<>(index.size());
            keys = new ArrayList<>(index.size());
            braf.close();
        } catch (FileNotFoundException e) {
            throw new PgatkIOException("APLFile does not exist.", e);
        } catch (IOException e) {
            throw new PgatkIOException("Failed to read from APL file.", e);
        }
    }

    /**
     * Creates the APL file object from an existing
     * APL file with a pre-parsed index of ms2 spectra.
     * The index must hold the offsets of all "BEGIN IONS"
     * lines in the order they appear in the file.
     *
     * @param file  The APL file
     * @param index An ArrayList holding the
     * @throws PgatkIOException Error
     */
    public AplIndexedReader(File file, List<IndexElement> index) throws PgatkIOException {
        // open the file
        try {
            // save the file
            sourceFile = file;
            // save the index
            this.index = index;

            BufferedRandomAccessFile reader = new BufferedRandomAccessFile(sourceFile, "r", 1024 * 1000);

            // process the file line by line
            String line;

            while ((line = reader.getNextLine()) != null) {

                // ignore empty lines
                if (line.length() < 1) {
                    continue;
                }

                // break the loop as soon as a ms2 query is encountered
                if (line.contains("peaklist start"))
                    break;
            }
            peakLists = new HashMap<>();
            keys = new ArrayList<>();
            reader.close();
        } catch (FileNotFoundException e) {
            throw new PgatkIOException("AplIndexedReader does not exist.", e);
        } catch (IOException e) {
            throw new PgatkIOException("Failed to read from APL file.", e);
        }
    }

    /**
     * Set the MS2 queries of the APL file. If this object was generated
     * from an existing APL file the connection to this APL file is lost.
     *
     * @param aplSpectrums Set list of Spectra.
     */
    public void setPeakLists(List<AplSpectrum> aplSpectrums) {
        // remove the source file link
        sourceFile = null;
        index.clear();

        // save the queries in the HashMap
        for (int index = 0; index < aplSpectrums.size(); index++){
            this.peakLists.put(index, aplSpectrums.get(index));
            this.keys.add(index);
        }

    }

    /**
     * Returns the number of Ms2 queries in the file.
     *
     * @return The number of MS2 queries.
     */
    public int getPeakListCount() {
        return (sourceFile != null) ? index.size() : peakLists.size();
    }

    /**
     * Returns the MS2 query with the given (0-based) index
     * in the file. To get the number of queries call
     * getPeakListCount().
     *
     * @param nIndex Index to be search.
     * @return Spectrum
     */
    public AplSpectrum getPeakList(int nIndex) throws PgatkIOException {
        // check if the ms2 query was already loaded
        if (peakLists.containsKey(nIndex))
            return peakLists.get(nIndex);

        // if there is no file to load the query from throw an Exception
        if (sourceFile == null)
            throw new PgatkIOException("MS2 query with index " + (nIndex + 1) + " does not exist");

        // make sure the index is valid
        if (nIndex < 0 || nIndex > index.size() - 1)
            throw new PgatkIOException("MS2 query with index " + (nIndex + 1) + " does not exist in the APL file");

        // load the query from the file
        AplSpectrum query;

        query = loadIndexedQueryFromFile(nIndex);

        // save the query in the HashMap
        if (useCache)
            peakLists.put(nIndex, query);

        return query;
    }

    /**
     * Loads a query from the APL file.
     *
     * @param file         The file to read the query from.
     * @param indexElement The index element pointing to that specific ms2 query.
     * @return Spectrum
     * @oaram index The query's 1-based index in the APL file. This index is stored in the returned Ms2Query object.
     */
    private static AplSpectrum loadIndexedQueryFromFile(File file, IndexElement indexElement, int index) throws PgatkIOException {
        try (RandomAccessFile accFile = new RandomAccessFile(file, "r")) {

            // read the indexed element
            byte[] byteBuffer = new byte[indexElement.getSize()];

            // read the file from there
            accFile.seek(indexElement.getStart());
            accFile.read(byteBuffer);
            String ms2Buffer = new String(byteBuffer);
            // create the query
            return new AplSpectrum(ms2Buffer, (long)index);
        } catch (FileNotFoundException e) {
            throw new PgatkIOException("APL file could not be found.", e);
        } catch (IOException e) {
            throw new PgatkIOException("Failed to read from APL file", e);
        }
        // ignore
    }

    /**
     * Loads a query from the APL file who's index was buffered.
     *
     * @param nQueryIndex The queries index.
     * @return
     */
    private AplSpectrum loadIndexedQueryFromFile(int nQueryIndex) throws PgatkIOException {
        if (nQueryIndex < 0 || nQueryIndex > index.size() - 1)
            throw new PgatkIOException("Tried to load non existing query from file");

        // read the indexed element
        IndexElement indexElement = index.get(nQueryIndex);

        return loadIndexedQueryFromFile(sourceFile, indexElement, nQueryIndex + 1);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        // write the spectra
        for (Integer index = 0; index < 1000000; index++) {
            if (!peakLists.containsKey(index))
                continue;

            string.append(peakLists.get(index).toString()).append('\n');
        }

        return string.toString();
    }

    /**
     * Returns the index of ms2 queries in the APL file.
     * This ArrayList contains the offsets of all "BEGIN IONS"
     * lines until the end of the "END IONS" lines
     * in the file in the order they are present.
     *
     * @return An array of "BEGIN IONS" lines offsets.
     */
    public List<IndexElement> getIndex() {
        return new ArrayList<>(index);
    }

    /**
     * Functions required by the
     * PeakListParser interface.
     */

    public int getSpectraCount() {
        return getPeakListCount();
    }

    public boolean acceptsFile() {
        return true;
    }

    public boolean acceptsDirectory() {
        return false;
    }

    public List<String> getSpectraIds() {
        // simply create a list of ids 1..size
        List<String> ids = new ArrayList<>(getPeakListCount());

        for (int id = 1; id <= getPeakListCount(); id++)
            ids.add(Integer.toString(id));

        return ids;
    }

    // ToDo PSI ID (in case of APL) is: id = 'index=12' for the 13th spectrum
    public Spectrum getSpectrumById(String id) throws PgatkIOException {
        // create an integer
        int index = Integer.parseInt(id);

        return getPeakList(index - 1);
    }

    public Spectrum getSpectrumByIndex(int index) throws PgatkIOException {
        return getPeakList(index - 1);
    }

    @Override
    public List<IndexElement> getMsNIndexes(
            int msLevel) {
        if (msLevel != 2)
            return Collections.emptyList();

        return new ArrayList<>(index);
    }

    @Override
    public List<Integer> getMsLevels() {
        // APL files can only contain MS 2
        List<Integer> msLevels = new ArrayList<>(1);
        msLevels.add(2);

        return msLevels;
    }

    @Override
    public Map<String, IndexElement> getIndexElementForIds() {
        Map<String, IndexElement> idToIndexMap = new HashMap<>(index.size());

        for (int i = 0; i < index.size(); i++) {
            idToIndexMap.put(String.format("%d", i + 1), index.get(i));
        }

        return idToIndexMap;
    }

    @Override
    public AplSpectrum next() {
        // if there is not file set, get the object from the HashMap
        if (sourceFile == null) {
            // make sure the current position is valid
            if (currentPosition < 0 || currentPosition >= peakLists.size())
                throw new IllegalStateException(new IndexOutOfBoundsException());

            // get the key
            Integer key = keys.get(currentPosition++);

            // make sure the key exists
            if (!peakLists.containsKey(key))
                throw new IllegalStateException("Key not found in hashmap");

            return peakLists.get(key);
        } else {
            // check if the object was cached
            if (peakLists.containsKey(currentPosition))
                return peakLists.get(currentPosition);

            // read the query from file
            AplSpectrum query;
            try {
                query = loadIndexedQueryFromFile(currentPosition);

                // if caching is enabled do so
                if (useCache)
                    peakLists.put(currentPosition, query);

                // move to the next position
                currentPosition++;

                // return the query
                return query;
            } catch (PgatkIOException e) {
                throw new RuntimeException("Failed to load query from file.", e);
            }
        }
    }

    @Override
    public void close() {}

    @Override
    public boolean hasNext() {
        if (sourceFile == null) {
            return currentPosition < peakLists.size();
        } else {
            return currentPosition < index.size();
        }
    }
}

