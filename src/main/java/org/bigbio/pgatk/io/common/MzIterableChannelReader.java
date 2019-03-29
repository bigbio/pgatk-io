package org.bigbio.pgatk.io.common;

import org.bigbio.pgatk.io.mgf.MgfUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public abstract class MzIterableChannelReader {


    protected FileChannel accessChannel;
    protected MappedByteBuffer buffer;
    protected long nextPosition = 0;


    public MzIterableChannelReader(File file) throws PgatkIOException {
        try {
            RandomAccessFile accessFile = new RandomAccessFile(file, "r");
            accessChannel = accessFile.getChannel();
        } catch (
                IOException e) {
            throw new PgatkIOException("Error reading the following file " + file.getAbsolutePath(), e);
        }
    }

    public void readBuffer(){
        try {
            if(nextPosition >= accessChannel.size()) {
            } else {
                long remSize = Math.min(MgfUtils.BUFFER_SIZE_100MB, accessChannel.size() - nextPosition);
                buffer = accessChannel.map(FileChannel.MapMode.READ_ONLY, nextPosition, remSize);
                nextPosition += remSize;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
