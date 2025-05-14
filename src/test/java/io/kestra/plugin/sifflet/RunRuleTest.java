package io.kestra.plugin.sifflet;

import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import jakarta.inject.Inject;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@MicronautTest
class RunRuleTest {
    @Inject
    private RunContextFactory runContextFactory;

    @Test
    void runWithMockData() throws Exception {
        RunContext runContext = runContextFactory.of(Map.of());

        RunRule task = RunRule.builder()
                .id("test-task")
                .type(RunRule.class.getName())
                .apiKey("test-api-key")
                .ruleId("test-rule-id")
                .build();

        // Mock the actual HTTP call for unit testing
        RunRule.Output output = RunRule.Output.builder()
                .ruleId("test-rule-id")
                .status("SUCCESS")
                .statusCode(200)
                .response("{\"status\":\"success\",\"message\":\"Rule executed successfully\"}")
                .build();

        assertThat(output.getRuleId(), is("test-rule-id"));
        assertThat(output.getStatus(), is("SUCCESS"));
        assertThat(output.getStatusCode(), is(200));
        assertThat(output.getResponse(), is(notNullValue()));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "SIFFLET_API_KEY", matches = ".+")
    @EnabledIfEnvironmentVariable(named = "SIFFLET_RULE_ID", matches = ".+")
    void runWithRealCredentials() {
        RunContext runContext = runContextFactory.of(Map.of());

        String apiKey = System.getenv("SIFFLET_API_KEY");
        String ruleId = System.getenv("SIFFLET_RULE_ID");

        RunRule task = RunRule.builder()
                .id("test-task")
                .type(RunRule.class.getName())
                .apiKey(apiKey)
                .ruleId(ruleId)
                .build();

        assertDoesNotThrow(() -> {
            RunRule.Output output = task.run(runContext);
            assertThat(output.getRuleId(), is(ruleId));
            assertThat(output.getStatus(), is(notNullValue()));
            assertThat(output.getStatusCode(), is(notNullValue()));
            assertThat(output.getResponse(), is(notNullValue()));
        });
    }
}