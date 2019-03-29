package org.bigbio.pgatk.io.mzxml;


import org.bigbio.pgatk.io.common.MzIterableChannelReader;
import org.bigbio.pgatk.io.common.MzIterableReader;
import org.bigbio.pgatk.io.common.PgatkIOException;
import org.bigbio.pgatk.io.common.Spectrum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class MzXMLIterableReader extends MzIterableChannelReader implements MzIterableReader {

    public static final Logger logger = LoggerFactory.getLogger(MzXMLIterableReader.class);

    /**
     * Source File containing all the spectra.
     */
    private File sourceFile;

    private int channelCursor = 0;
    private int specIndex = 1;

    public MzXMLIterableReader(File file) throws PgatkIOException {
        super(file);
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
                if(stringBuffer.toString().contains("scan num")){
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
        int scanLevel = -1;
        byte[] peakBytes = null;
        String compressionType = null;
        Integer precision = null;
        String byteOrder = null;
        MzXMLSpectrum spectrum = null;
        if(buffer == null || !buffer.hasRemaining()){
            readBuffer();
        }

        StringBuffer stringBuffer = new StringBuffer();

        while (buffer.hasRemaining()) {
            char ch = ((char) buffer.get());
            channelCursor = buffer.position();

            if(ch=='\n'){
                stringBuffer.append(ch);
                String line = stringBuffer.toString().trim();

                if(line.contains("scan num")) {
                    logger.debug("Start reading the following spectrum -- ");
                    scanLevel = -1;
                    spectrum = new MzXMLSpectrum();
                    spectrum.setId(Integer.valueOf(line.substring(line.indexOf("=")+2, line.lastIndexOf("\""))).toString());
                }else if(line.contains("</scan>") && scanLevel == 2) {
                    spectrum.setIndex((long)specIndex);
                    specIndex++;
                    return spectrum;
                }else if(spectrum != null){
                    if (line.contains("msLevel")) {
                        scanLevel = Integer.valueOf(line.substring(line.indexOf("=")+2, line.lastIndexOf("\"")));
                        spectrum.setMsLevel(scanLevel);
                    }

                    if (line.contains("polarity"))
                        spectrum.setPolarity(line.substring(line.indexOf("=")+2, line.lastIndexOf("\"")));
                    if (line.contains("retentionTime"))
                        spectrum.setRetentionTime(line.substring(line.indexOf("PT")+2, line.lastIndexOf("S\"")));

                    if (line.contains("<precursorMz") && scanLevel > 1){
                        spectrum.setPrecursorMz(Double.valueOf(line.substring(line.indexOf(">")+1, line.indexOf("</precursorMz>"))));
                        Matcher matcher = Pattern.compile("precursorCharge=\"([A-Za-z0-9_]*)\"").matcher(line);

                        while (matcher.find()) {
                            spectrum.setPrecursorCharge(Integer.parseInt( matcher.group(1)));
                        }
                        matcher = Pattern.compile("precursorIntensity=\"([A-Za-z0-9_]*)\"").matcher(line);
                        while (matcher.find()) {
                            spectrum.setPrecursorIntesity(Double.parseDouble(matcher.group(1)));
                        }

                        matcher = Pattern.compile("activationMethod=\"([A-Za-z0-9_]*)\"").matcher(line);
                        while (matcher.find()) {
                            spectrum.setActivationMethod(matcher.group(1));
                        }
                    }

                    if(line.contains("compressionType=")){
                        Matcher matcher = Pattern.compile("compressionType=\"([A-Za-z0-9_]*)\"").matcher(line);
                        while (matcher.find()) {
                            compressionType = (matcher.group(1));
                        }
                    }
                    if(line.contains("precision=")){
                        Matcher matcher = Pattern.compile("precision=\"([A-Za-z0-9_]*)\"").matcher(line);
                        while (matcher.find()) {
                            precision = Integer.parseInt(matcher.group(1));
                        }
                    }
                    if(line.contains("byteOrder=")){
                        Matcher matcher = Pattern.compile("byteOrder=\"([A-Za-z0-9_]*)\"").matcher(line);
                        while (matcher.find()) {
                            byteOrder = (matcher.group(1));
                        }
                    }

                    if (line.contains("m/z-int"))
                    {
                        if (line.contains("==</peaks>"))
                            peakBytes = line.substring(line.indexOf(">")+1, line.indexOf("</peaks>")).getBytes();
                        try {
                            Map<Double, Double> peaks = convertPeaksToMap(peakBytes, compressionType, byteOrder, precision);
                            spectrum.setPeaks(peaks);
                        } catch (MzXMLParsingException e) {
                            throw new NoSuchElementException("Error parsing the peak list in mzXML spectra line --" + line);
                        }
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

    /**
     * Extracts the peak list from the given
     * Peaks object and returns it as a Map with
     * the m/z as key and the intensity as value.
     *
     * @param peaks A peaks object.
     * @return Map containing the m/z as key and the intensity as value.
     */
    public static Map<Double, Double> convertPeaksToMap(byte[] peaks, String compressType, String byteOrder, Integer precision) throws MzXMLParsingException {
        // make sure the scan is not null
        if (peaks == null || peaks == null)
            return Collections.emptyMap();

        // wrap the data with a ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.wrap(peaks);

        // check if the string is compressed
        boolean zlibCompression = (compressType != null && "zlib".equalsIgnoreCase(compressType));

        // handle compressed peak lists
        if (zlibCompression) {
            Inflater decompresser = new Inflater();

            // TODO: make sure the call to .array() is working
            decompresser.setInput(byteBuffer.array());

            // allocate 10x the memory of the original one
            byte[] decompressedData = new byte[byteBuffer.capacity() * 10];

            try {
                int usedLength = decompresser.inflate(decompressedData);

                // save it as the new byte buffer
                byteBuffer = ByteBuffer.wrap(decompressedData, 0, usedLength);
            } catch (DataFormatException e) {
                throw new MzXMLParsingException("Failed to decompress spectra data.", e);
            }
        }

        if ("network".equalsIgnoreCase(byteOrder))
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
        else
            throw new MzXMLParsingException("Peak lists must be encoded using network (big-endian) byte order");

        double[] values;

        // get the precision
        if (precision != null && precision == 64) {
            values = new double[byteBuffer.asDoubleBuffer().capacity()];
            byteBuffer.asDoubleBuffer().get(values);
        }
        // if no precision is set, expect 32bit
        else {
            // need to convert the floats to doubles
            FloatBuffer floats = byteBuffer.asFloatBuffer();
            values = new double[floats.capacity()];

            for (int index = 0; index < floats.capacity(); index++)
                values[index] = (double) floats.get(index);
        }

        // make sure there's an even number of values (2 for every peak)
        if (values.length % 2 > 0)
            throw new MzXMLParsingException("Different number of m/z and intensity values encountered in peak list.");

        // create the Map
        HashMap<Double, Double> peakList = new HashMap<>(values.length / 2);

        for (int peakIndex = 0; peakIndex < values.length - 1; peakIndex += 2) {
            // get the two value
            Double mz = values[peakIndex];
            Double intensity = values[peakIndex + 1];

            peakList.put(mz, intensity);
        }

        return peakList;
    }



}

