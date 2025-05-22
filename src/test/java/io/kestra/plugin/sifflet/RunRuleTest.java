package io.kestra.plugin.sifflet;

import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
class RunRuleTest {
    @Inject
    private RunContextFactory runContextFactory;

    @Test
    void invalidUrl() {
        RunRule task = RunRule.builder()
                .id("test-task")
                .type(RunRule.class.getName())
                .baseUrl("invalid-url")
                .apiKey("test-api-key")
                .ruleId("test-rule-id")
                .httpTimeout(Duration.ofSeconds(10))
                .build();

        RunContext runContext = runContextFactory.of();

        Exception exception = assertThrows(Exception.class, () -> {
            task.run(runContext);
        });

        assertThat(exception, is(notNullValue()));
    }

    @Test
    void validBuilder() {
        RunRule.builder()
                .id("test-task")
                .type(RunRule.class.getName())
                .baseUrl("https://app.siffletdata.com")
                .apiKey("test-api-key")
                .ruleId("test-rule-id")
                .httpTimeout(Duration.ofMinutes(10))
                .parameters(new HashMap<>())
                .build();
    }
}
