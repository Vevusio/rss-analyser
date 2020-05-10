package com.cyan.rssanalyser.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AnalysisResourceSystemTest {
    private static final String FEED_EN_US_3_X_BOB_XML = "en-us_3xBob.xml";
    private static final String FEED_EN_US_2_X_ALICE_XML = "en-us_2xAlice.xml";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private WireMockServer wireMockServer;

    private String baseUrl;

    @BeforeEach
    public void setup () {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        this.baseUrl = "http://localhost:" + wireMockServer.port() + "/feeds";

        mockFeed(FEED_EN_US_3_X_BOB_XML);
        mockFeed(FEED_EN_US_2_X_ALICE_XML);
    }

    @AfterEach
    public void teardown () {
        wireMockServer.stop();
    }

    @Test
    public void create_and_retrieve_analysis_result() throws Exception {
        NewAnalysisRequest newAnalysisRequest = new NewAnalysisRequest();
        newAnalysisRequest.setFeedUrls(asList(
                baseUrl + "/en-us_3xBob.xml",
                baseUrl + "/en-us_2xAlice.xml"));

        mockMvc.perform(post("/analyse/new")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newAnalysisRequest)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.analysisId", equalTo("analysis_1")));


        mockMvc.perform(get("/frequency/analysis_1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frequentWords[0].word", equalTo("bob")))
                .andExpect(jsonPath("$.frequentWords[0].seenIn", hasSize(3)))
                .andExpect(jsonPath("$.frequentWords[0].seenIn[0].title", equalTo("hey bob")))
                .andExpect(jsonPath("$.frequentWords[0].seenIn[0].link", equalTo("https://bob.com/bob1")))
                .andExpect(jsonPath("$.frequentWords[0].seenIn[1].title", equalTo("bob wins")))
                .andExpect(jsonPath("$.frequentWords[0].seenIn[1].link", equalTo("https://bob.com/bob2")))
                .andExpect(jsonPath("$.frequentWords[0].seenIn[2].title", equalTo("look over there Bob")))
                .andExpect(jsonPath("$.frequentWords[0].seenIn[2].link", equalTo("https://bob.com/bob3")))
                .andExpect(jsonPath("$.frequentWords[1].word", equalTo("alice")))
                .andExpect(jsonPath("$.frequentWords[1].seenIn", hasSize(2)))
                .andExpect(jsonPath("$.frequentWords[1].seenIn[0].title", equalTo("alice in wonderland")))
                .andExpect(jsonPath("$.frequentWords[1].seenIn[0].link", equalTo("https://alice.com/alice1")))
                .andExpect(jsonPath("$.frequentWords[1].seenIn[1].title", equalTo("where's Alice")))
                .andExpect(jsonPath("$.frequentWords[1].seenIn[1].link", equalTo("https://alice.com/alice2")));
    }

    @Test
    public void analysis_with_less_than_2_feeds_results_in_error() throws Exception {
        NewAnalysisRequest newAnalysisRequest = new NewAnalysisRequest();
        newAnalysisRequest.setFeedUrls(singletonList(baseUrl + "/en-us_3xBob.xml"));

        mockMvc.perform(post("/analyse/new")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newAnalysisRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private void mockFeed(String feed) {
        try {
            String feedXml = new String(Files.readAllBytes(
                    Paths.get(getClass().getClassLoader().getResource("feeds/" + feed).toURI())),
                    StandardCharsets.UTF_8);

            stubFor(WireMock.get(WireMock.urlEqualTo("/feeds/" + feed))
                        .willReturn(aResponse()
                        .withHeader("Content-Type", "application/xml")
                        .withBody(feedXml)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}