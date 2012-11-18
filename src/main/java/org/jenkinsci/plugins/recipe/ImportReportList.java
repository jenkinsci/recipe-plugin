package org.jenkinsci.plugins.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Keeps track of the record of what was actually done during the import process,
 * which can be thought of a collection of {@link ImportReport}s.
 *
 * @author Kohsuke Kawaguchi
 */
public class ImportReportList implements Iterable<ImportReport> {
    private final List<ImportReport> reports = new ArrayList<ImportReport>();

    public Iterator<ImportReport> iterator() {
        return reports.iterator();
    }

    public void add(ImportReport report) {
        reports.add(report);
    }
}
