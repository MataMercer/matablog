package com.matamercer.microblog.configuration

import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class SearchEngineAnalyzerConfig: ElasticsearchAnalysisConfigurer {
    override fun configure(context: ElasticsearchAnalysisConfigurationContext) {
        context.analyzer("name")
            .custom()
            .tokenizer("standard")
            .tokenFilters("asciifolding", "lowercase")

        context.normalizer("sort")
            .custom()
            .tokenFilters("asciifolding", "lowercase")

    }

    @Bean
    fun analyzerConfigurer(): ElasticsearchAnalysisConfigurer{
        return this
    }
}