package com.matamercer.microblog.configuration

import org.hibernate.search.mapper.orm.Search
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Configuration
class SearchEngineConfig(
    private val entityManager: EntityManager
): CommandLineRunner{

    @Transactional
    override fun run(vararg args: String?) {
        val searchSession = Search.session(entityManager)
        val massIndexer = searchSession.massIndexer().threadsToLoadObjects(12)
        massIndexer.startAndWait()
    }

}