package org.bigbio.pgatk.io.common;

import java.io.Serializable;

/**
 * Interface describing IndexElements
 * that represent the position of
 * an object in a file.
 * @author jg
 *
 */
public interface IndexElement extends Serializable {
	/**
	 * Returns the starting position (in bytes) of the
	 * indexed object.
	 * @return Byte offset of the indexed offset.
	 */
    long getStart();
	
	/**
	 * Returns the size of the indexed object
	 * in the file.
	 * 
	 * @return Size of the indexed object in bytes.
	 */
	int getSize();
}
