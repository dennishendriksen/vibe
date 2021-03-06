package org.molgenis.vibe.rdf_processing.querying;

import org.molgenis.vibe.rdf_processing.query_string_creation.DisgenetQueryStringGenerator;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RetrieveGeneByPhenotypeSpeedComparison extends QuerySpeedComparison {
    private static final int testRepeats = 3;

    // timeOut gives "org.apache.jena.tdb.transaction.TDBTransactionException: Not in a transaction".
    // Can't run on big tests because too slow.
    @Test(groups = {"benchmarking"}, enabled = false)
    public void checkSpeedGdaFirst() {
        String query = "SELECT ?hpo ?disease ?geneTitle \n" +
                "WHERE { ?type rdfs:subClassOf* sio:SIO_000983 . \n" +
                "?gda rdf:type ?type ; \n" +
                "sio:SIO_000628 ?gene , ?disease . \n" +
                "?gene rdf:type ncit:C16612 ; \n" +
                "dcterms:title ?geneTitle . \n" +
                "?disease rdf:type ncit:C7057 . \n" +
                "?pda rdf:type sio:SIO_000897 ;" +
                "sio:SIO_000628 ?hpo , ?disease ." +
                "?hpo rdf:type sio:SIO_010056 ;" +
                "dcterms:identifier \"hp:0009811\"^^xsd:string }";
        String[] times = runQuery(DisgenetQueryStringGenerator.getPrefixes() + query, testRepeats);
        System.out.println("gda times: " + Arrays.stream(times).map(String::toString).collect(Collectors.joining(", ")));
    }

    @Test(groups = {"benchmarking"})
    public void checkSpeedPdaFirst() {
        String query = "SELECT ?hpo ?disease ?geneTitle \n" +
                "WHERE { ?pda rdf:type sio:SIO_000897 ; \n" +
                "sio:SIO_000628 ?hpo , ?disease . \n" +
                "?hpo rdf:type sio:SIO_010056 ; \n" +
                "dcterms:identifier \"hp:0009811\"^^xsd:string . \n" +
                "?disease rdf:type ncit:C7057 . \n" +
                "?gda sio:SIO_000628 ?disease, ?gene ; \n" +
                "rdf:type ?type . \n" +
                "?type rdfs:subClassOf* sio:SIO_000983 . \n" +
                "?gene rdf:type ncit:C7057 ; " +
                "dcterms:title ?geneTitle .}";
        String[] times = runQuery(DisgenetQueryStringGenerator.getPrefixes() + query, testRepeats);
        System.out.println("pda times: " + Arrays.stream(times).map(String::toString).collect(Collectors.joining(", ")));
    }

    @Test(groups = {"benchmarking"})
    public void checkSpeedHpoFirst() {
        String query = "SELECT ?hpo ?disease ?geneTitle \n" +
                "WHERE { ?hpo rdf:type sio:SIO_010056 ; \n" +
                "dcterms:identifier \"hp:0009811\"^^xsd:string . \n" +
                "?pda rdf:type sio:SIO_000897 ; \n" +
                "sio:SIO_000628 ?hpo , ?disease . \n" +
                "?disease rdf:type ncit:C7057 . \n" +
                "?gda sio:SIO_000628 ?disease, ?gene ; \n" +
                "rdf:type ?type . \n" +
                "?type rdfs:subClassOf* sio:SIO_000983 . \n" +
                "?gene rdf:type ncit:C7057 ; \n" +
                "dcterms:title ?geneTitle .}";
        String[] times = runQuery(DisgenetQueryStringGenerator.getPrefixes() + query, testRepeats);
        System.out.println("hpo times: " + Arrays.stream(times).map(String::toString).collect(Collectors.joining(", ")));
    }

    @Test(groups = {"benchmarking"})
    public void hpoFirstInitialV2() {
        String query = "SELECT ?diseaseTitle ?geneId ?geneSymbolTitle ?pdaSourceTitle ?gdaSourceTitle \n" +
                "WHERE { ?hpo rdf:type sio:SIO_010056 ; \n" +
                "dcterms:identifier \"hp:0009811\"^^xsd:string . \n" +
                "?pda rdf:type sio:SIO_000897 ; \n" +
                "sio:SIO_000628 ?hpo , ?disease ; \n" +
                "sio:SIO_000253 ?pdaSource . \n" +
                "?pdaSource rdf:type dctypes:Dataset ;" +
                "dcterms:title ?pdaSourceTitle ." +
                "?disease rdf:type ncit:C7057 ; \n" +
                "dcterms:title ?diseaseTitle . \n" +
                "?gda sio:SIO_000628 ?disease, ?gene ; \n" +
                "rdf:type ?type ; \n" +
                "sio:SIO_000253 ?gdaSource . \n" +
                "?gdaSource rdf:type dctypes:Dataset ; \n" +
                "dcterms:title ?gdaSourceTitle . \n" +
                "?type rdfs:subClassOf* sio:SIO_000983 . \n" +
                "?gene rdf:type ncit:C16612 ; \n" +
                "dcterms:identifier ?geneId ; \n" +
                "sio:SIO_000205 ?geneSymbol . \n" +
                "?geneSymbol rdf:type ncit:C43568 ; \n" +
                "dcterms:title ?geneSymbolTitle . }";
        String[] times = runQuery(DisgenetQueryStringGenerator.getPrefixes() + query, testRepeats);
        System.out.println("hpo V2 initial times: " + Arrays.stream(times).map(String::toString).collect(Collectors.joining(", ")));
    }

    @Test(groups = {"benchmarking"})
    public void hpoFirstReorderedV2() {
        String query = "SELECT ?diseaseTitle ?geneId ?geneSymbolTitle ?pdaSourceTitle ?gdaSourceTitle \n" +
                "WHERE { ?hpo rdf:type sio:SIO_010056 ; \n" +
                "dcterms:identifier \"hp:0009811\"^^xsd:string . \n" +
                "?pda rdf:type sio:SIO_000897 ; \n" +
                "sio:SIO_000628 ?hpo , ?disease ; \n" +
                "sio:SIO_000253 ?pdaSource . \n" +
                "?disease rdf:type ncit:C7057 ; \n" +
                "dcterms:title ?diseaseTitle . \n" +
                "?pdaSource rdf:type dctypes:Dataset ; \n" +
                "dcterms:title ?pdaSourceTitle . \n" +
                "?gda sio:SIO_000628 ?disease, ?gene ; \n" +
                "rdf:type ?type ; \n" +
                "sio:SIO_000253 ?gdaSource . \n" +
                "?type rdfs:subClassOf* sio:SIO_000983 . \n" +
                "?gene rdf:type ncit:C16612 ; \n" +
                "dcterms:identifier ?geneId ; \n" +
                "sio:SIO_000205 ?geneSymbol . \n" +
                "?geneSymbol rdf:type ncit:C43568 ; \n" +
                "dcterms:title ?geneSymbolTitle . \n" +
                "?gdaSource rdf:type dctypes:Dataset ; \n" +
                "dcterms:title ?gdaSourceTitle . \n" +
                "}";
        String[] times = runQuery(DisgenetQueryStringGenerator.getPrefixes() + query, testRepeats);
        System.out.println("hpo V2 reordered times: " + Arrays.stream(times).map(String::toString).collect(Collectors.joining(", ")));
    }
}
