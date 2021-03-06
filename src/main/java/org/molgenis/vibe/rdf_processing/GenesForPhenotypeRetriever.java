package org.molgenis.vibe.rdf_processing;

import static java.util.Objects.requireNonNull;

import org.apache.jena.query.QuerySolution;
import org.molgenis.vibe.formats.*;
import org.molgenis.vibe.io.ModelReader;
import org.molgenis.vibe.rdf_processing.query_string_creation.DisgenetQueryStringGenerator;
import org.molgenis.vibe.rdf_processing.querying.QueryRunner;

import java.net.URI;
import java.util.*;

/**
 * Retrieves all required information for further processing regarding the genes belonging to a given phenotype.
 */
public class GenesForPhenotypeRetriever extends DisgenetRdfDataRetriever {
    /**
     * The {@link Phenotype}{@code s} to be processed.
     */
    private Set<Phenotype> phenotypes;

    /**
     * {@link Gene}{@code s} storage for further processing.
     */
    private Set<Gene> genes = new HashSet<>();

    /**
     * {@link Gene}{@code s} storage for easy retrieval.
     */
    private Map<URI, Gene> genesByUri = new HashMap<>();

    /**
     * {@link Disease}{@code s} storage for easy retrieval.
     */
    private Map<URI, Disease> diseasesByUri = new HashMap<>();

    /**
     * The final output to be retrieved for further usage after querying.
     */
    private GeneDiseaseCollection geneDiseaseCollection = new GeneDiseaseCollection();

    public GeneDiseaseCollection getGeneDiseaseCollection() {
        return geneDiseaseCollection;
    }

    public GenesForPhenotypeRetriever(ModelReader modelReader, Set<Phenotype> phenotypes) {
        super(modelReader);
        this.phenotypes = requireNonNull(phenotypes);
    }

    @Override
    public void run() {
        retrieveSources();
        retrieveGenes();
        retrieveGdasWithDiseases();
    }

    private void retrieveGenes() {
        QueryRunner query = new QueryRunner(getModelReader().getModel(),
                DisgenetQueryStringGenerator.getGenesForPhenotypes(phenotypes));

        while(query.hasNext()) {
            QuerySolution result = query.next();

            URI geneUri = URI.create(result.get("gene").asResource().getURI());
            String geneId = result.get("geneId").asLiteral().getString();
            String geneTitle= result.get("geneTitle").asLiteral().getString();
            String geneSymbol = result.get("geneSymbolTitle").asLiteral().getString();
            double diseaseSpecificityIndex = result.get("dsiValue").asLiteral().getDouble();
            double diseasePleiotropyIndex = result.get("dpiValue").asLiteral().getDouble();

            Gene gene = new Gene(geneId, geneTitle, geneSymbol, diseaseSpecificityIndex, diseasePleiotropyIndex, geneUri);
            genes.add(gene);
            genesByUri.put(geneUri, gene);
        }

        query.close();
    }

    private void retrieveGdasWithDiseases() {
        QueryRunner query = new QueryRunner(getModelReader().getModel(),
                DisgenetQueryStringGenerator.getGdasWithDiseasesForGenes(genes));

        while(query.hasNext()) {
            QuerySolution result = query.next();

            // Check if disease is already stored, and if not, stores it (using URI as key).
            URI diseaseUri = URI.create(result.get("disease").asResource().getURI());
            Disease disease = diseasesByUri.get(diseaseUri);

            if(disease == null) {
                disease = new Disease(result.get("diseaseId").asLiteral().getString(),
                        result.get("diseaseTitle").asLiteral().getString(),
                        diseaseUri);

                diseasesByUri.put(diseaseUri, disease);
            }

            // Retrieves gene.
            URI geneUri = URI.create(result.get("gene").asResource().getURI());
            Gene gene = genesByUri.get(geneUri);

            // Retrieves score belonging to the gene-disease combination.
            double score = result.get("gdaScoreNumber").asLiteral().getDouble();

            // The gene-disease combination belonging to the single query result.
            GeneDiseaseCombination comparisonGdc = new GeneDiseaseCombination(gene, disease, score);

            // Retrieves it from the collection (if it already exists).
            GeneDiseaseCombination gdc = geneDiseaseCollection.get(comparisonGdc);

            // If the gene-disease combination is not present yet, uses the comparison gdc and also adds it to the collection.
            if(gdc == null) {
                gdc = comparisonGdc;
                geneDiseaseCollection.add(gdc);
            }

            // Retrieves source belonging to match. If this causes an error, this might indicate a corrupt database (as
            // retrieveSources() should retrieve all possible sources available).
            Source source = getSources().get(URI.create(result.get("gdaSource").asResource().getURI()));

            // Adds source to gene-disease combination (with evidence if available).
            if(result.get("evidence") != null) {
                gdc.add(source, URI.create(result.get("evidence").asResource().getURI()));
            } else {
                gdc.add(source);
            }
        }

        query.close();
    }
}
