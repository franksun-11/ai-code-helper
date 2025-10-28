package com.example.aicodehelper.ai.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 加载RAG
 */
@Configuration
public class RagConfig {

    @Resource
    private EmbeddingModel githubEmbeddingModel;

    @Resource
    private EmbeddingStore<TextSegment>  embeddingStore;

    @Bean
    public ContentRetriever contentRetriever() {
        // 1.加载文档
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/docs");
        // 2.文档切割, 每个文档按照段落分割, 最大1000个字符, 每次最多重叠200个字符
        DocumentByParagraphSplitter documentByParagraphSplitter =
                new DocumentByParagraphSplitter(1000, 200);
        // 3.自定义文档加载器, 把文档转换成向量并保存到向量数据库
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentByParagraphSplitter)
                .textSegmentTransformer(textSegment ->
                        TextSegment.from(
                                textSegment.metadata().getString("file_name") + "\n" + textSegment.text(),
                                textSegment.metadata()
                        )
                )
                .embeddingModel(githubEmbeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        // 加载文档
        ingestor.ingest(documents);
        // 4. 自定义内容加载器
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(githubEmbeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(2) // 最多返回2个相关内容 (减少token使用)
                .minScore(0.75) // 过滤掉分数低于0.75的内容
                .build();
        return contentRetriever;
    }
}