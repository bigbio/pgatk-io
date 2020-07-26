package io.github.bigbio.pgatk.io.mgf;

import io.github.bigbio.pgatk.io.common.MzIterableChannelReader;
import io.github.bigbio.pgatk.io.common.MzIterableReader;
import io.github.bigbio.pgatk.io.common.PgatkIOException;
import io.github.bigbio.pgatk.io.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;

/**
 * This implementation only allows to iterate over all the spectra in a file and retrieve the corresponding
 * spectra. This implementation is faster that the MgfIndexedReader for iterable read of files but can't be used for RandomAccess
 *
 * @author ypriverol
 */

@Slf4j
public class MgfIterableReader extends MzIterableChannelReader implements MzIterableReader {

    private boolean allowCustomTags;

    /**
     * If this option is set, comments are not removed
     * from MGF files. This speeds up parsing considerably
     * but causes problems if MGF files do contain comments.
     */
    private boolean disableCommentSupport;

    /**
     * This function helps to ignore peaks if the parser found parser errors in the peaks
     */
    private boolean ignoreWrongPeaks = MgfUtils.DEFAULT_IGNORE_WRONG_PEAKS;

    /**
     * Source File containing all the spectra.
     */
    private File sourceFile;

    private int channelCursor = 0;

    // The index (1-based) is used to know in the order of the spectrum in the file.
    private long specIndex = 1;

    private  Ms2Query spectrum = null;

    public MgfIterableReader(File file, boolean ignoreWrongPeaks, boolean disableCommentSupport, boolean allowCustomTags) throws PgatkIOException {

        super(file);
        this.ignoreWrongPeaks = ignoreWrongPeaks;
        this.disableCommentSupport = disableCommentSupport;
        this.allowCustomTags = allowCustomTags;
        this.sourceFile = file;

        try {
            RandomAccessFile accessFile = new RandomAccessFile(file, "r");
            accessChannel = accessFile.getChannel();
        } catch (IOException e) {
           throw new PgatkIOException("Error reading the following file " + file.getAbsolutePath(), e);
        }

    }

    @Override
    public boolean hasNext() {
        StringBuilder stringBuffer = new StringBuilder(100);
        if (buffer == null || !buffer.hasRemaining())
            readBuffer();
        char ch = '\n';
        channelCursor = buffer.position();
        while((ch != '\u0000') && (buffer.hasRemaining())){
            ch = ((char) buffer.get());
            channelCursor = buffer.position();
            stringBuffer.append(ch);
            if(ch == '\n' && stringBuffer.toString().contains("BEGIN IONS")){
                spectrum = new Ms2Query(this.disableCommentSupport);
                return true;
            }
            if(!buffer.hasRemaining()){
               readBuffer();
            }
        }

        return false;
    }

    @Override
    public Spectrum next() throws NoSuchElementException {
        log.debug("Start reading the following spectrum -- ");
        if(spectrum == null)
            throw new NoSuchElementException("First check if the file contains an spectum using hasNext()");

        if(buffer == null || !buffer.hasRemaining()){
            readBuffer();
        }

        boolean inAttributeSection = true;
        StringBuffer stringBuffer = new StringBuffer();

        while (buffer.hasRemaining()) {
            char ch = ((char) buffer.get());
            channelCursor = buffer.position();

            if(ch=='\n'){
                stringBuffer.append(ch);
                String line = StringUtils.removeBOMString(stringBuffer.toString().trim());
                if(line.contains("END IONS")) {
                    spectrum.setIndex(specIndex);
                    specIndex++;
                    return spectrum;
                }else if(spectrum != null){

                    // Some files can have a lot of empty and nonsense information between Spectrums
                    if (!disableCommentSupport)
                        line = line.replaceAll(MgfUtils.mgfCommentRegex, line);
                    if (line.length() < 1) { // ignore empty lines
                        continue;
                    }

                    Matcher attributeMatcher = MgfUtils.attributePattern.matcher(line); // check if it's a property
                    boolean matchesAttributePattern = false;
                    if (inAttributeSection) {
                        matchesAttributePattern = attributeMatcher.find();
                    }
                    if (matchesAttributePattern) {
                        if (attributeMatcher.groupCount() != 2) {
                            throw new NoSuchElementException("Invalid attribute line encountered in MS2 query: " + line);
                        }
                        String name = attributeMatcher.group(1);
                        String value = attributeMatcher.group(2);
                        spectrum.saveAttribute(name, value);
                    } else {
                        double[] peakArray = MgfUtils.parsePeakLine(line);
                        if (peakArray != null && peakArray.length == 2) {
                            spectrum.addPeak(peakArray[0], peakArray[1]);
                        } else {  // no index could be found
                            if (ignoreWrongPeaks) {
                                log.error("The following peaks and wronly annotated -- " + line);
                            } else
                                throw new NoSuchElementException("Unable to parse 'mz' and 'intensity' values for " + line);
                        }
                        inAttributeSection = false;
                    }

                }
                stringBuffer = new StringBuffer();

            }else{
                stringBuffer.append(ch);
            }
            if(!buffer.hasRemaining())
                readBuffer();
        }
        return spectrum;
    }

    @Override
    public void close() throws PgatkIOException {
        try {
            accessChannel.close();
        } catch (IOException e) {
            throw new PgatkIOException("The following file can't be close -- " + sourceFile, e);
        }

    }
}
