package org.molgenis.vibe.rdf_querying;

import org.molgenis.vibe.formats.Hpo;

/**
 * Generates SPARQL queries specific for the DisGeNET RDF dataset.
 */
public final class DisgenetQueryGenerator extends SparqlQueryGenerator {
    // See: http://www.disgenet.org/web/DisGeNET/menu/rdf#sparql-queries -> DisGeNET NAMESPACES
    // Some namespaces contained 1 or more additional "http://". These were removed.
    // 18 lines (for debugging SPARQL queries)
    private static final String PREFIXES = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"+
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"+
            "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"+
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n"+
            "PREFIX dcterms: <http://purl.org/dc/terms/> \n"+
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n"+
            "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n"+
            "PREFIX void: <http://rdfs.org/ns/void#> \n"+
            "PREFIX sio: <http://semanticscience.org/resource/>\n"+
            "PREFIX ncit: <http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#> \n"+
            "PREFIX up: <http://purl.uniprot.org/core/> \n"+
            "PREFIX dcat: <http://www.w3.org/ns/dcat#> \n"+
            "PREFIX dctypes: <http://purl.org/dc/dcmitype/> \n"+
            "PREFIX wi: <http://purl.org/ontology/wi/core#> \n"+
            "PREFIX eco: <http://purl.obolibrary.org/obo/eco.owl#> \n"+
            "PREFIX prov: <http://www.w3.org/ns/prov#> \n"+
            "PREFIX pav: <http://purl.org/pav/> \n"+
            "PREFIX obo: <http://purl.obolibrary.org/obo/> \n";

    private static final String[] IRI_FOR_HPO = {"SELECT ?hpo \n", // SELECT is [0]
            // [1]
            "WHERE { ?hpo rdf:type sio:SIO_010056 ; \n" +
            "dcterms:identifier \"", "\"^^xsd:string . \n" + // HPO term is inserted between [1] and [2]
            "}"
    };

    private static final String[] HPO_CHILDREN_FOR_IRI = {"SELECT ?hpo \n", // SELECT is [0]
            // [1]
            "WHERE { ?hpo rdf:type sio:SIO_010056 ; \n" +
            "rdfs:subClassOf", " ?hpoParent . \n" + //  child range is inserted between [1] and [2]
            "{ SELECT (?hpo as ?hpoParent) \n" +
            IRI_FOR_HPO[1], IRI_FOR_HPO[2] + " } \n" + // HPO term is inserted between [2] and [3]
            "}"
    };

    private static final String[] HPO_GENES = {"SELECT ?diseaseTitle ?geneId ?geneSymbolTitle ?gdaScoreNumber ?pdaSource ?gdaSource ?gdaSourceLevelLabel ?pubmed \n" +
            "WHERE { ?hpo rdf:type sio:SIO_010056 ; \n" + // SIO_010056 -> phenotype
            "dcterms:identifier \"", "\"^^xsd:string . \n" + // HPO term is inserted here!!!
            "?pda rdf:type sio:SIO_000897 ; \n" + // SIO_000897 -> association
            "sio:SIO_000628 ?hpo , ?disease ; \n" + // SIO_000628 -> refers to
            "sio:SIO_000253 ?pdaSource . \n" + // SIO_000253 -> has source
            "?disease rdf:type ncit:C7057 ; \n" + // ncit:C7057 -> Disease
            "dcterms:title ?diseaseTitle . \n" +
            "?pdaSource rdf:type dctypes:Dataset . \n" + // dcterms:title sometimes has multiple hits
            "?gda sio:SIO_000628 ?disease, ?gene ; \n" + // SIO_000628 -> refers to
            "rdf:type ?type ; \n" +
            "sio:SIO_000216 ?gdaScore ; \n" + // SIO_000216 -> has measurement value
            "sio:SIO_000253 ?gdaSource . \n" + // SIO_000253 -> has source
            "?type rdfs:subClassOf* sio:SIO_000983 . \n" + // SIO_000983 -> gene-disease association
            "?gene rdf:type ncit:C16612 ; \n" + // ncit:C16612 -> Gene
            "dcterms:identifier ?geneId ; \n" +
            "sio:SIO_000205 ?geneSymbol . \n" + // SIO_000205 -> is represented by
            "?geneSymbol rdf:type ncit:C43568 ; \n" + // ncit:C43568 -> Gene Symbol
            "dcterms:title ?geneSymbolTitle . \n" +
            "?gdaScore rdf:type ncit:C25338 ; \n" + // ncit:C25338 -> Score
            "sio:SIO_000300 ?gdaScoreNumber . \n" + // SIO_000300 -> has value
            "?gdaSource rdf:type dctypes:Dataset ; \n" + // dcterms:title sometimes has multiple hits
            "wi:evidence ?gdaSourceLevel . \n" +
            "?gdaSourceLevel rdfs:label ?gdaSourceLevelLabel . \n" +
            "OPTIONAL { ?gda sio:SIO_000772 ?pubmed }" + // SIO_000772 -> has evidence
            "}"
    };

    public static String getPrefixes() {
        return PREFIXES;
    }

    public static String getIriForHpo(Hpo hpo) {
        return PREFIXES + IRI_FOR_HPO[0] + IRI_FOR_HPO[1] + hpo.getFormattedId() + IRI_FOR_HPO[2];
    }

    public static String getHpoChildren(Hpo hpo, SparqlRange range) {
        return PREFIXES + HPO_CHILDREN_FOR_IRI[0] + HPO_CHILDREN_FOR_IRI[1] + range.toString() + HPO_CHILDREN_FOR_IRI[2] + hpo.getFormattedId() + HPO_CHILDREN_FOR_IRI[3];
    }

//    public static String getPhenotypeDiseaseAssociations(String hpoTerm) {
//        return PREFIXES + RETRIEVE_PDA_FOR_HPO[0] + RETRIEVE_PDA_FOR_HPO[1];
//    }

    public static String getHpoGenes(String hpoTerm) {
        return PREFIXES + HPO_GENES[0] + hpoTerm + HPO_GENES[1];
    }

    public static String getHpoGenes(String hpoTerm, int limit) {
        return addLimit(PREFIXES + HPO_GENES[0] + hpoTerm + HPO_GENES[1], limit);
    }
}
