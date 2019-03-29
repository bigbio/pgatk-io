package org.bigbio.pgatk.io.mgf;

import org.bigbio.pgatk.io.common.MzIterableChannelReader;
import org.bigbio.pgatk.io.common.MzIterableReader;
import org.bigbio.pgatk.io.common.PgatkIOException;
import org.bigbio.pgatk.io.mzxml.MzXMLParsingException;
import org.bigbio.pgatk.io.mzxml.mzxml.model.Peaks;
import org.bigbio.pgatk.io.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bigbio.pgatk.io.common.Spectrum;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * This implementation only allows to iterate over all the spectra in a file and retrieve the corresponding
 * spectra. This implementation is faster that the MgfIndexedReader for iterable read of files but can't be used for RandomAccess
 *
 * @author ypriverol
 */
public class MgfIterableReader extends MzIterableChannelReader implements MzIterableReader {

    public static final Logger logger = LoggerFactory.getLogger(MgfIterableReader.class);

    private boolean allowCustomTags = MgfUtils.DEFAULT_ALLOW_CUSTOM_TAGS;

    /**
     * If this option is set, comments are not removed
     * from MGF files. This speeds up parsing considerably
     * but causes problems if MGF files do contain comments.
     */
    private boolean disableCommentSupport = false;

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
        StringBuffer stringBuffer = new StringBuffer();
        if (buffer == null)
            readBuffer();
        char ch = '\n';
        channelCursor = buffer.position();
        while(buffer.hasRemaining() && (ch != '\u0000')){
            ch = ((char) buffer.get());
            channelCursor = buffer.position();
            stringBuffer.append(ch);
            if(ch == '\n'){
                if(stringBuffer.toString().contains("BEGIN IONS")){
                    channelCursor = channelCursor -  stringBuffer.toString().length();
                    buffer.position(channelCursor);
                    return true;
                }else
                    stringBuffer = new StringBuffer();
            }
        }
        return false;
    }

    @Override
    public Spectrum next() throws NoSuchElementException {
        Ms2Query spectrum = null;
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

                if(line.contains("BEGIN IONS")) {
                    logger.debug("Start reading the following spectrum -- ");
                    spectrum = new Ms2Query(this.disableCommentSupport);
                }else if(line.contains("END IONS")) {
                    spectrum.setIndex(specIndex);
                    specIndex++;
                    return spectrum;
                }else if(spectrum != null){
                    /**
                     * Some files can have a lot of empty and nonsense information between Spectrums
                     */
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
                        String cleanedLine = line.replaceAll("\\s+", " ");
                        int indexSpace = cleanedLine.indexOf(' ');
                        if (indexSpace >= 0) {
                            String firstHalf = cleanedLine.substring(0, indexSpace);
                            String secondHalf = cleanedLine.substring(indexSpace + 1);
                            int anotherSpace = secondHalf.indexOf(' ');
                            Double intensity;
                            if (anotherSpace < 0) {
                                intensity = Double.parseDouble(secondHalf);
                            } else { // ignore extra fragment charge number (3rd field), may be present
                                intensity = StringUtils.smartParseDouble((secondHalf.substring(0, anotherSpace)));
                            }
                            spectrum.addPeak(Double.parseDouble(firstHalf), intensity);
                        } else {  // no index could be found
                            if (ignoreWrongPeaks) {
                                logger.error("The following peaks and wronly annotated -- " + line);
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
