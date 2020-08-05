/**
 * Copyright (c) 2010, SIB. All rights reserved.
 *
 * SIB (Swiss Institute of Bioinformatics) - http://www.isb-sib.ch Host -
 * http://mzjava.expasy.org
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the SIB/GENEBIO nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL SIB/GENEBIO BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.github.bigbio.pgatk.io.msp;

import io.github.bigbio.pgatk.io.common.MzIterableChannelReader;
import io.github.bigbio.pgatk.io.common.MzIterableReader;
import io.github.bigbio.pgatk.io.common.PgatkIOException;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;
import io.github.bigbio.pgatk.io.mgf.MgfUtils;
import io.github.bigbio.pgatk.io.utils.StringUtils;

import java.io.*;
import java.util.NoSuchElementException;

/**
 * An IterativeReader that can read nist msp files. See http://peptide.nist.gov/
 */
public class MspIterableReader extends MzIterableChannelReader implements MzIterableReader {

    private final File sourceFile;
    private int channelCursor = 0;

    // The index (1-based) is used to know in the order of the spectrum in the file.
    private long specIndex = 1;

    private MspSpectrum spectrum = null;
    private LibrarySpectrumBuilder builder;

    public MspIterableReader(File source) throws PgatkIOException {
        super(source);
        this.sourceFile = source;

        try {
            RandomAccessFile accessFile = new RandomAccessFile(this.sourceFile, "r");
            accessChannel = accessFile.getChannel();
        } catch (IOException e) {
            throw new PgatkIOException("Error reading the following file " + source.getAbsolutePath(), e);
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
            if(ch == '\n' && stringBuffer.toString().contains("Name:")){
                try {
                    builder = new LibrarySpectrumBuilder();
                    String value = stringBuffer.toString().split(":")[1].trim();
                    MspAttributeReader.parseName(value, builder);
                    spectrum = new MspSpectrum(builder.getPeptideSequence(), builder.getCharge());
                    return true;
                } catch (PgatkIOException e) {
                    e.printStackTrace();
                }
            }
            if(!buffer.hasRemaining()){
                readBuffer();
            }
        }

        return false;
    }

    @Override
    public Spectrum next() throws NoSuchElementException {
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
                if(line.trim().length() == 0) {
                    spectrum.setIndex(specIndex);
                    specIndex++;
                    spectrum.setProperties(builder);
                    return spectrum;
                }else if(spectrum != null){
                    if(line.contains(":")){
                        if(line.contains("Comment:")){
                            int index = line.indexOf(":");
                            MspAttributeReader.parseComment(line.substring(index + 2), builder);
                        }

                    }else{
                        double[] peakArray = MgfUtils.parsePeakLine(line);
                        if (peakArray != null && peakArray.length == 2) {
                            spectrum.addPeak(peakArray[0], peakArray[1]);
                        } else {  // no index could be found
                            inAttributeSection = false;
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
        spectrum.setIndex(specIndex);
        specIndex++;
        spectrum.setProperties(builder);
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
