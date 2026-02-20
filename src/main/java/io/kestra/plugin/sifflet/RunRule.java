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
import jakarta.validation.constraints.NotNull;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Schema(title = "Trigger a Sifflet data quality rule", description = "Call Sifflet's `/api/v1/rules/{ruleId}/_run` endpoint to launch a rule run. Inputs are rendered with the Run Context; defaults include `https://api.siffletdata.com` and a 30s timeout.")
@Plugin(examples = {
        @Example(title = "Run a Sifflet rule", code = """
            id: sifflet_flow
            namespace: company.team
            tasks:
              - id: run_rule
                type: io.kestra.plugin.sifflet.RunRule
                apiKey: "{{ secret('SIFFLET_API_KEY') }}"
                ruleId: "rule-123"
                baseUrl: "https://api.siffletdata.com"
            """
        ),
        @Example(title = "Run with default base URL and custom timeout", code = """
            id: sifflet_flow
            namespace: company.team
            tasks:
              - id: run_rule_timeout
                type: io.kestra.plugin.sifflet.RunRule
                apiKey: "{{ secret('SIFFLET_API_KEY') }}"
                ruleId: "rule-456"
                requestTimeout: 60
            """
        )
})
public class RunRule extends Task implements RunnableTask<RunRule.Output> {

    @Schema(title = "Sifflet API key", description = "Bearer token for the Sifflet API; renderable and should be stored as a secret", required = true)
    @NotNull
    private String apiKey;

    @Schema(title = "Rule ID", description = "Identifier of the Sifflet rule to execute; required for a valid call and rendered before the request")
    private String ruleId;

    @Schema(title = "Sifflet API base URL", description = "Base URL for the Sifflet API; defaults to `https://api.siffletdata.com` when omitted")
    private String baseUrl;

    @Schema(title = "Request timeout", description = "Timeout in seconds for both connection and request; defaults to 30", defaultValue = "30")
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
        String apiUrl = renderedBaseUrl + "/api/v1/rules/" + renderedRuleId + "/_run";

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
