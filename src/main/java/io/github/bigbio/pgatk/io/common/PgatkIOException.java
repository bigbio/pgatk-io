package io.github.bigbio.pgatk.io.common;

@SuppressWarnings("serial")
public class PgatkIOException extends Exception {

	public PgatkIOException(String format, Exception e) {
		super();
	}

	public PgatkIOException(String arg0) {
		super(arg0);
	}

	public PgatkIOException(Throwable arg0) {
		super(arg0);
	}

	public PgatkIOException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
