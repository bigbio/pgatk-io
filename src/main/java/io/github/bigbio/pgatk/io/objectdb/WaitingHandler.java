package io.github.bigbio.pgatk.io.objectdb;

import java.text.SimpleDateFormat;


public interface WaitingHandler {

    /**
     * Convenience date format.
     */
    public static final SimpleDateFormat SIMPLE_DATA_FORMAT = new SimpleDateFormat("dd MMM yyyy, HH:mm");
    /**
     * The tab space to add when using HTML.
     */
    public static final String TAB_HTML = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
    /**
     * The tab space to add when not using HTML.
     */
    public static final String TAB_NON_HTML = "        ";

    /**
     * Sets whether the primary progress counter is indeterminate or not.
     *
     * @param indeterminate a boolean indicating whether the primary progress
     * counter is indeterminate or not
     */
    public void setPrimaryProgressCounterIndeterminate(boolean indeterminate);

    /**
     * Set the maximum value for the primary progress counter.
     *
     * @param maxProgressValue the max value
     */
    public void setMaxPrimaryProgressCounter(int maxProgressValue);

    /**
     * Increase the primary progress counter by one "counter".
     */
    public void increasePrimaryProgressCounter();

    /**
     * Increase the primary progress counter by the given increment.
     *
     * @param increment the increment to increase the value by
     */
    public void increasePrimaryProgressCounter(int increment);

    /**
     * Sets the primary progress counter to the given value.
     *
     * @param value the progress value
     */
    public void setPrimaryProgressCounter(int value);

    /**
     * Set the maximum value for the secondary progress counter.
     *
     * @param maxProgressValue the max value
     */
    public void setMaxSecondaryProgressCounter(int maxProgressValue);

    /**
     * Reset the primary progress counter to 0.
     */
    public void resetPrimaryProgressCounter();

    /**
     * Reset the secondary progress counter to 0.
     */
    public void resetSecondaryProgressCounter();

    /**
     * Increase the secondary progress counter by one "counter".
     */
    public void increaseSecondaryProgressCounter();

    /**
     * Sets the secondary progress counter to the given value.
     *
     * @param value the progress value
     */
    public void setSecondaryProgressCounter(int value);

    /**
     * Increase the secondary progress counter by the given value.
     *
     * @param value the value to increase the value by
     */
    public void increaseSecondaryProgressCounter(int value);

    /**
     * Sets the secondary progress counter to indeterminate or not.
     *
     * @param indeterminate if true, set to indeterminate
     */
    public void setSecondaryProgressCounterIndeterminate(boolean indeterminate);

    /**
     * Set the process as finished.
     */
    public void setRunFinished();

    /**
     * Set the process as canceled.
     */
    public void setRunCanceled();

    /**
     * Append text to the report.
     *
     * @param report the text to append
     * @param includeDate if the date and time is to be added to the front of
     * the text
     * @param addNewLine add a new line after the text?
     */
    public void appendReport(String report, boolean includeDate, boolean addNewLine);

    /**
     * Append two tabs to the report. No new line.
     */
    public void appendReportNewLineNoDate();

    /**
     * Append a new line to the report.
     */
    public void appendReportEndLine();

    /**
     * Returns true if the run is canceled.
     *
     * @return true if the run is canceled
     */
    public boolean isRunCanceled();

    /**
     * Returns true if the process is finished.
     *
     * @return true if the process is finished
     */
    public boolean isRunFinished();

    /**
     * Set the secondary progress counter text.
     *
     * @param text the text to set
     */
    public void setSecondaryProgressText(String text);

    /**
     * Indicates whether this waiting handler supports reports.
     *
     * @return a boolean indicating whether this waiting handler supports
     * reports
     */
    public boolean isReport();

    /**
     * Sets the text describing what is currently being waited for.
     *
     * @param text a text describing what is currently waited for
     */
    public void setWaitingText(String text);

    /**
     * Returns the primary progress counter.
     *
     * @return primary progress counter
     */
    public int getPrimaryProgressCounter();

    /**
     * Returns the max primary progress counter.
     *
     * @return max primary progress counter
     */
    public int getMaxPrimaryProgressCounter();

    /**
     * Returns the secondary progress counter.
     *
     * @return secondary progress counter
     */
    public int getSecondaryProgressCounter();

    /**
     * Returns the max secondary progress counter.
     *
     * @return max secondary progress counter
     */
    public int getMaxSecondaryProgressCounter();

    /**
     * Set if the waiting handler is to show the progress for the current
     * process or not. Useful when running subprocesses that one wants to be
     * able to cancel but do not want to show the progress for.
     *
     * @param displayProgress if the waiting handler is to show the progress for
     * the current process or not
     */
    public void setDisplayProgress(boolean displayProgress);

    /**
     * Returns if the waiting handler is to show the progress for the current
     * process or not. Useful when running subprocesses that one wants to be
     * able to cancel but do not want to show the progress for.
     *
     * @return if the waiting handler is to show the progress for the current
     * process or not
     */
    public boolean getDisplayProgress();
}
