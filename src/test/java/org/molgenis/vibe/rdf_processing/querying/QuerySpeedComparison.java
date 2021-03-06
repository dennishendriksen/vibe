package org.molgenis.vibe.rdf_processing.querying;

import org.apache.jena.ext.com.google.common.base.Stopwatch;
import org.apache.jena.ext.com.google.common.io.ByteStreams;
import org.apache.jena.query.ResultSetFormatter;
import org.molgenis.vibe.TestData;
import org.molgenis.vibe.io.ModelReader;
import org.molgenis.vibe.io.TripleStoreDbReader;
import org.molgenis.vibe.rdf_processing.query_string_creation.DisgenetQueryStringGenerator;
import org.molgenis.vibe.rdf_processing.query_string_creation.QueryString;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;

public abstract class QuerySpeedComparison {
    private static ModelReader reader;

    @BeforeClass(groups = {"benchmarking"})
    public void beforeClass() throws IOException {
        reader = new TripleStoreDbReader(TestData.TDB_FULL.getDir());
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        reader.close();
    }

    protected String[] runQuery(String queryString, int testRepeats) {
        String[] times = new String[testRepeats];
        for(int i = 0; i<testRepeats;i++) {
            Stopwatch timer = Stopwatch.createStarted();
            QueryRunner runner = new QueryRunner(reader.getModel(), new QueryString(DisgenetQueryStringGenerator.getPrefixes() + queryString));
            ResultSetFormatter.out(ByteStreams.nullOutputStream(), runner.getResultSet());
            times[i] = timer.stop().toString();
            runner.close();
        }
        return times;
    }
}
