package io.github.bigbio.pgatk.io.objectdb;

/**
 * Placeholder for the db mutex.
 */
public class DbMutex {

    /**
     * The db mutex.
     */
    public static final SimpleSemaphore dbMutex = new SimpleSemaphore(1);

    /**
     * The cache load objects mutex.
     */
    public static final SimpleSemaphore loadObjectMutex = new SimpleSemaphore(1);

}
