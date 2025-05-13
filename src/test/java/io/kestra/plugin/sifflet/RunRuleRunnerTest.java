package io.kestra.plugin.sifflet;

import io.kestra.core.runners.RunnerUtils;
import io.kestra.core.storages.StorageInterface;
import io.kestra.core.utils.IdUtils;
import io.kestra.core.utils.TestsUtils;
import io.kestra.plugin.scripts.exec.scripts.runners.CommandsWrapper;
import io.kestra.plugin.scripts.exec.scripts.services.ScriptService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.net.URI;
import java.util.Objects;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@MicronautTest
class RunRuleRunnerTest {
    @Inject
    private RunnerUtils runnerUtils;

    @Inject
    private StorageInterface storageInterface;

    @Inject
    private ScriptService scriptService;

    @Test
    void run() throws Exception {
        // Skip this test by default as it requires a real Sifflet API key
        // To run this test, create a file at src/test/resources/application-test.properties
        // with the following properties:
        // sifflet.url=https://app.siffletdata.com
        // sifflet.apiKey=your-api-key
        // sifflet.ruleId=your-rule-id
        
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(Objects.requireNonNull(
            getClass().getClassLoader().getResource("application-test.properties")).getPath())) {
            properties.load(fis);
        } catch (Exception e) {
            assumeTrue(false, "Skipping test as application-test.properties is not available");
            return;
        }
        
        String url = properties.getProperty("sifflet.url");
        String apiKey = properties.getProperty("sifflet.apiKey");
        String ruleId = properties.getProperty("sifflet.ruleId");
        
        assumeTrue(url != null && apiKey != null && ruleId != null, 
            "Skipping test as Sifflet credentials are not available");
        
        URI flowUri = Objects.requireNonNull(RunRuleRunnerTest.class.getClassLoader()
            .getResource("flows/sifflet-run-rule.yaml")).toURI();
        
        TestsUtils.runFlow(
            runnerUtils,
            flowUri,
            (flow, execution) -> {
                assertThat(execution.getTaskRunList().size(), is(1));
                assertThat(execution.getTaskRunList().get(0).getState().getCurrent(), is("SUCCESS"));
            }
        );
    }
}
