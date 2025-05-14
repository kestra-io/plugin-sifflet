package io.kestra.plugin.sifflet;

import io.kestra.core.models.annotations.Example;
import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.tasks.RunnableTask;
import io.kestra.core.models.tasks.Task;
import io.kestra.core.runners.RunContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Schema(title = "Run a Sifflet data quality rule", description = "Execute a Sifflet rule to validate data quality")
@Plugin(examples = {
        @Example(title = "Run a Sifflet rule", code = {
                "apiKey: \"{{ secret('SIFFLET_API_KEY') }}\"",
                "ruleId: \"rule-123\"",
                "baseUrl: \"https://api.siffletdata.com\""
        })
})
public class RunRule extends Task implements RunnableTask<RunRule.Output> {

    @Schema(title = "Sifflet API key", description = "API key for authenticating with Sifflet")
    private String apiKey;

    @Schema(title = "Rule ID", description = "ID of the Sifflet rule to run")
    private String ruleId;

    @Schema(title = "Sifflet API base URL", description = "Base URL for the Sifflet API", defaultValue = "https://api.siffletdata.com")
    private String baseUrl;

    @Schema(title = "Request timeout", description = "Timeout for the API request in seconds", defaultValue = "30")
    private Integer requestTimeout;

    @Override
    public Output run(RunContext runContext) throws Exception {
        Logger logger = runContext.logger();

        String renderedApiKey = runContext.render(apiKey);
        String renderedRuleId = runContext.render(ruleId);
        String renderedBaseUrl = runContext.render(baseUrl != null ? baseUrl : "https://api.siffletdata.com");
        int timeout = requestTimeout != null ? requestTimeout : 30;

        logger.info("Running Sifflet rule with ID: {}", renderedRuleId);

        // Create HTTP client
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeout))
                .build();

        // Build the API endpoint URL
        String apiUrl = renderedBaseUrl + "/api/v1/rules/" + renderedRuleId + "/run";

        // Create HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + renderedApiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(timeout))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        // Execute the request
        logger.debug("Sending request to: {}", apiUrl);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Process the response
        int statusCode = response.statusCode();
        String responseBody = response.body();

        logger.debug("Received response with status code: {}", statusCode);

        if (statusCode >= 200 && statusCode < 300) {
            logger.info("Rule execution completed successfully");
            return Output.builder()
                    .ruleId(renderedRuleId)
                    .status("SUCCESS")
                    .statusCode(statusCode)
                    .response(responseBody)
                    .build();
        } else {
            logger.error("Rule execution failed with status code: {}", statusCode);
            return Output.builder()
                    .ruleId(renderedRuleId)
                    .status("FAILED")
                    .statusCode(statusCode)
                    .response(responseBody)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {
        @Schema(description = "The ID of the executed rule")
        private final String ruleId;

        @Schema(description = "Status of the rule execution (SUCCESS or FAILED)")
        private final String status;

        @Schema(description = "HTTP status code from the API response")
        private final Integer statusCode;

        @Schema(description = "Raw response from the Sifflet API")
        private final String response;
    }
}