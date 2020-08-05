package io.github.bigbio.pgatk.io.objectdb;

import java.text.SimpleDateFormat;


public interface WaitingHandler {

    /**
     * Convenience date format.
     */
    SimpleDateFormat SIMPLE_DATA_FORMAT = new SimpleDateFormat("dd MMM yyyy, HH:mm");
    /**
     * The tab space to add when using HTML.
     */
    String TAB_HTML = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
    /**
     * The tab space to add when not using HTML.
     */
    String TAB_NON_HTML = "        ";

    /**
     * Sets whether the primary progress counter is indeterminate or not.
     *
     * @param indeterminate a boolean indicating whether the primary progress
     * counter is indeterminate or not
     */
    void setPrimaryProgressCounterIndeterminate(boolean indeterminate);

    /**
     * Set the maximum value for the primary progress counter.
     *
     * @param maxProgressValue the max value
     */
    void setMaxPrimaryProgressCounter(int maxProgressValue);

    /**
     * Increase the primary progress counter by one "counter".
     */
    void increasePrimaryProgressCounter();

    /**
     * Increase the primary progress counter by the given increment.
     *
     * @param increment the increment to increase the value by
     */
    void increasePrimaryProgressCounter(int increment);

    /**
     * Sets the primary progress counter to the given value.
     *
     * @param value the progress value
     */
    void setPrimaryProgressCounter(int value);

    /**
     * Set the maximum value for the secondary progress counter.
     *
     * @param maxProgressValue the max value
     */
    void setMaxSecondaryProgressCounter(int maxProgressValue);

    /**
     * Reset the primary progress counter to 0.
     */
    void resetPrimaryProgressCounter();

    /**
     * Reset the secondary progress counter to 0.
     */
    void resetSecondaryProgressCounter();

    /**
     * Increase the secondary progress counter by one "counter".
     */
    void increaseSecondaryProgressCounter();

    /**
     * Sets the secondary progress counter to the given value.
     *
     * @param value the progress value
     */
    void setSecondaryProgressCounter(int value);

    /**
     * Increase the secondary progress counter by the given value.
     *
     * @param value the value to increase the value by
     */
    void increaseSecondaryProgressCounter(int value);

    /**
     * Sets the secondary progress counter to indeterminate or not.
     *
     * @param indeterminate if true, set to indeterminate
     */
    void setSecondaryProgressCounterIndeterminate(boolean indeterminate);

    /**
     * Set the process as finished.
     */
    void setRunFinished();

    /**
     * Set the process as canceled.
     */
    void setRunCanceled();

    /**
     * Append text to the report.
     *
     * @param report the text to append
     * @param includeDate if the date and time is to be added to the front of
     * the text
     * @param addNewLine add a new line after the text?
     */
    void appendReport(String report, boolean includeDate, boolean addNewLine);

    /**
     * Append two tabs to the report. No new line.
     */
    void appendReportNewLineNoDate();

    /**
     * Append a new line to the report.
     */
    void appendReportEndLine();

    /**
     * Returns true if the run is canceled.
     *
     * @return true if the run is canceled
     */
    boolean isRunCanceled();

    /**
     * Returns true if the process is finished.
     *
     * @return true if the process is finished
     */
    boolean isRunFinished();

    /**
     * Set the secondary progress counter text.
     *
     * @param text the text to set
     */
    void setSecondaryProgressText(String text);

    /**
     * Indicates whether this waiting handler supports reports.
     *
     * @return a boolean indicating whether this waiting handler supports
     * reports
     */
    boolean isReport();

    /**
     * Sets the text describing what is currently being waited for.
     *
     * @param text a text describing what is currently waited for
     */
    void setWaitingText(String text);

    /**
     * Returns the primary progress counter.
     *
     * @return primary progress counter
     */
    int getPrimaryProgressCounter();

    /**
     * Returns the max primary progress counter.
     *
     * @return max primary progress counter
     */
    int getMaxPrimaryProgressCounter();

    /**
     * Returns the secondary progress counter.
     *
     * @return secondary progress counter
     */
    int getSecondaryProgressCounter();

    /**
     * Returns the max secondary progress counter.
     *
     * @return max secondary progress counter
     */
    int getMaxSecondaryProgressCounter();

    /**
     * Set if the waiting handler is to show the progress for the current
     * process or not. Useful when running subprocesses that one wants to be
     * able to cancel but do not want to show the progress for.
     *
     * @param displayProgress if the waiting handler is to show the progress for
     * the current process or not
     */
    void setDisplayProgress(boolean displayProgress);

    /**
     * Returns if the waiting handler is to show the progress for the current
     * process or not. Useful when running subprocesses that one wants to be
     * able to cancel but do not want to show the progress for.
     *
     * @return if the waiting handler is to show the progress for the current
     * process or not
     */
    boolean getDisplayProgress();
}
