package com.cyan.rssanalyser.creation;

import com.cyan.rssanalyser.persistence.AnalysisRepository;
import com.cyan.rssanalyser.persistence.Analysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class FeedAnalysisCreator {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AnalysisRepository analysisRepository;
    private final FeedWordUtils feedWordUtils;
    private final AnalysisReferenceGenerator analysisReferenceGenerator;
    private final int analysisRuntimeMaxSeconds;

    public FeedAnalysisCreator(AnalysisRepository analysisRepository, FeedWordUtils feedWordUtils,
                               AnalysisReferenceGenerator analysisReferenceGenerator,
                               @Value("${analyser.analysisRuntimeMaxSeconds}") int analysisRuntimeMaxSeconds) {

        this.analysisRepository = analysisRepository;
        this.feedWordUtils = feedWordUtils;
        this.analysisReferenceGenerator = analysisReferenceGenerator;
        this.analysisRuntimeMaxSeconds = analysisRuntimeMaxSeconds;
    }

    public Analysis analyseRssFeeds(List<String> feedUrls) {
        AnalysisContext analysisContext = new AnalysisContext();

        List<FeedAnalyseTask> feedAnalyseTasks = createFeedAnalyseTasks(feedUrls, analysisContext);
        executeAll(feedAnalyseTasks);

        guardAnyItemsAnalysed(analysisContext);

        Analysis analysis = analysisRepository.createAnalysis(
                analysisReferenceGenerator.generate(),
                analysisContext.getItems(),
                analysisContext.getWords().values(),
                analysisContext.getWordOccurences()
        );
        return analysis;
    }

    private void executeAll(List<FeedAnalyseTask> feedAnalyseTasks) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            executorService.invokeAll(feedAnalyseTasks, analysisRuntimeMaxSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn("Interrupted", e);
        } finally {
            executorService.shutdownNow();
        }
    }

    private void guardAnyItemsAnalysed(AnalysisContext analysisContext) {
        if (analysisContext.getItems().isEmpty()) {
            throw new RuntimeException("Failed to gather any data");
        }
    }

    private List<FeedAnalyseTask> createFeedAnalyseTasks(List<String> feedUrls, AnalysisContext analysisContext) {
        return feedUrls.stream()
                    .map(feedUrl -> new FeedAnalyseTask(createFeedHandler(analysisContext), feedUrl))
                    .collect(Collectors.toList());
    }

    FeedHandler createFeedHandler(AnalysisContext analysisContext) {
        return new FeedHandler(analysisContext, feedWordUtils);
    }

    static class FeedAnalyseTask implements Callable<Void> {
        private FeedHandler handler;
        private String feedUrl;

        public FeedAnalyseTask(FeedHandler handler, String feedUrl) {
            this.handler = handler;
            this.feedUrl = feedUrl;
        }

        @Override
        public Void call() throws Exception {
            try {
                XMLReader reader = XMLReaderFactory.createXMLReader();
                reader.setContentHandler(handler);
                URL url = new URL(feedUrl);
                InputStream stream = url.openStream();
                reader.parse(new InputSource(stream));
            } catch (SAXException | IOException | RuntimeException e) {
                logger.info("Failed on feed=" + feedUrl, e);
            }

            return null;
        }
    }


}
