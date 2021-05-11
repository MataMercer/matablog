package com.matamercer.microblog.thymeleaf.markdown;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.spring5.expression.SPELContextPropertyAccessor;

import java.util.HashSet;
import java.util.Set;


public class MarkdownDialect extends AbstractProcessorDialect {

    private final static String NAME = "markdownDialect";
    private final static String DIALECT_PREFIX = "th";
    private final static int PROCESSOR_PRECEDENCE = 1000;
    public MarkdownDialect() {
        super(NAME, DIALECT_PREFIX, PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new MarkdownTagProcessor(dialectPrefix));
        return processors;
    }
}
