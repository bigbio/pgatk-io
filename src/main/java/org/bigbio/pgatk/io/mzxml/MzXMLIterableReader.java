package org.bigbio.pgatk.io.mzxml;


import lombok.extern.slf4j.Slf4j;
import org.bigbio.pgatk.io.common.MzIterableChannelReader;
import org.bigbio.pgatk.io.common.MzIterableReader;
import org.bigbio.pgatk.io.common.PgatkIOException;
import org.bigbio.pgatk.io.common.Spectrum;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

@Slf4j
public class MzXMLIterableReader extends MzIterableChannelReader implements MzIterableReader {

    /**
     * Source File containing all the spectra.
     */
    private File sourceFile;

    private int channelCursor = 0;
    private int specIndex = 1;
    private MzXMLSpectrum spectrum = null;

    public MzXMLIterableReader(File file) throws PgatkIOException {
        super(file);
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
            String line = stringBuffer.toString();
            if(ch == '\n' && line.contains("scan num")){
                spectrum = new MzXMLSpectrum();
                Matcher matcher = Pattern.compile("num=\"([A-Za-z0-9_]*)\"").matcher(line);
                while (matcher.find()) {
                    spectrum.setId(matcher.group(1));
                }
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
        int scanLevel = -1;

        byte[] peakBytes = null;
        String compressionType = null;
        Integer precision = null;
        String byteOrder = null;

        log.debug("Start reading the following spectrum -- ");
        if(spectrum == null)
            throw new NoSuchElementException("First check if the file contains an spectum using hasNext()");

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

                if(line.contains("</scan") && scanLevel == 2) {
                    spectrum.setIndex((long)specIndex);
                    specIndex++;
                    scanLevel = -1;
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
                    }
                    if (line.contains("<precursorMz") && scanLevel > 1){
                        Matcher matcher = Pattern.compile("precursorIntensity=\"([A-Za-z0-9_]*)\"").matcher(line);
                        while (matcher.find()) {
                            spectrum.setPrecursorIntesity(Double.parseDouble(matcher.group(1)));
                        }
                    }
                    if (line.contains("<precursorMz") && scanLevel > 1){
                        Matcher matcher = Pattern.compile("activationMethod=\"([A-Za-z0-9_]*)\"").matcher(line);
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
                        String mzArray = null;
                        if (line.contains("==</peaks")) mzArray = line.substring(line.indexOf(">")+1, line.indexOf("==</peaks"));
                        else if (line.contains("=</peaks")) mzArray = line.substring(line.indexOf(">")+1, line.indexOf("=</peaks"));
                        else if (line.contains("</peaks")) mzArray = line.substring(line.indexOf(">")+1, line.indexOf("</peaks"));

                        try {
                            if (mzArray != null){
                                Map<Double, Double> peaks = convertPeaksToMap(mzArray, compressionType, byteOrder, precision);
                                spectrum.setPeaks(peaks);
                            }
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
     * @param arrayPeaks A peaks object.
     * @return Map containing the m/z as key and the intensity as value.
     */
    public static Map<Double, Double> convertPeaksToMap(String arrayPeaks, String compressType, String byteOrder, Integer precision) throws MzXMLParsingException {

        byte[] peaks = Base64.getDecoder().decode(arrayPeaks);
        // make sure the scan is not null
        if (peaks == null)
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

