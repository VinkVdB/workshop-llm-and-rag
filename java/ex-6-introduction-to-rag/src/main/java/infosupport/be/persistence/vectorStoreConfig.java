package infosupport.be.persistence;

import infosupport.be.util.CustomTextSplitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Slf4j
@Configuration
public class vectorStoreConfig {

    @Value("classpath:rag/pokedex.txt")
    private Resource pokedex;
    @Value("classpath:rag/recipes.txt")
    private Resource recipes;
    @Value("classpath:rag/dnd_bestiary.txt")
    private Resource bestiary;

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        var vectorStore = new SimpleVectorStore(embeddingModel);

        /* The TokenTextSplitter is a simple text splitter that attempts to retrieve paragraphs up to X amount of tokens before splitting.
        The problem with the TokenTextSplitter is that it has no knowledge of the structure of the text. Meaning it
        could for example split a recipe halfway. Needing to rely on the embeddings to find the missing half. */
        var defaultTextSplitter = new TokenTextSplitter();
        /* For this reason I made a custom splitter */
        var pokemonTextSplitter = new CustomTextSplitter("Name: ");
        // var recipesTextSplitter = new CustomTextSplitter("(02) 8188 8722 | HelloFresh.com.au");
        // var bestiaryTextSplitter = new CustomTextSplitter("====");

        log.info("Reading documents from resources");
        var documents = new TextReader(pokedex).read();
        // var documents = new TextReader(recipes).read();
        // var documents = new TextReader(bestiary).read();
        /* You could also use a PDFReader for this, e.g. implementing a DocumentReader and using Apache PDFBox */

        log.info("Splitting documents");
        // var chunks = new TokenTextSplitter().transform(documents);
        var chunks = pokemonTextSplitter.transform(documents);
        // var chunks = recipesTextSplitter.transform(documents);
        // var chunks = bestiaryTextSplitter.transform(documents);

        // Ingest the split document chunks into the vector store
        log.info("Ingesting {} chunks into the vector store", chunks.size());
        vectorStore.write(chunks);

        return vectorStore;
    }
}
