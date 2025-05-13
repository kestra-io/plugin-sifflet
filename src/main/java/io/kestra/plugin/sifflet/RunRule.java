package io.kestra.plugin.sifflet;

import io.kestra.core.models.annotations.Example;
import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.annotations.PluginProperty;
import io.kestra.core.models.tasks.RunnableTask;
import io.kestra.core.models.tasks.Task;
import io.kestra.core.runners.RunContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.Builder;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Schema(title = "Run a Sifflet rule and get the results.")
@Plugin(examples = {
        @Example(title = "Run a Sifflet rule", code = {
                "url: \"https://app.siffletdata.com\"",
                "apiKey: \"{{ secret('SIFFLET_API_KEY') }}\"",
                "ruleId: \"rule-123456\"",
                "httpTimeout: PT10M"
        })
})
public class RunRule extends Task implements RunnableTask<RunRule.Output> {
    @Schema(title = "The Sifflet API URL", description = "The base URL of the Sifflet API (e.g., https://app.siffletdata.com)")
    @PluginProperty(dynamic = true)
    @NotEmpty
    private String url;

    @Schema(title = "The Sifflet API key", description = "The API key used for authentication with Sifflet")
    @PluginProperty(dynamic = true)
    @NotEmpty
    private String apiKey;

    @Schema(title = "The ID of the rule to run", description = "The unique identifier of the Sifflet rule to execute")
    @PluginProperty(dynamic = true)
    @NotEmpty
    private String ruleId;

    @Schema(title = "HTTP timeout duration", description = "Maximum duration to wait for rule execution to complete")
    @PluginProperty
    @NotNull
    @Builder.Default
    private Duration httpTimeout = Duration.ofMinutes(10);

    @Schema(title = "Additional parameters", description = "Additional parameters to pass to the Sifflet API")
    @PluginProperty(dynamic = true)
    private Map<String, Object> parameters;

    @Override
    public Output run(RunContext runContext) throws Exception {
        String resolvedUrl = runContext.render(url);
        String resolvedApiKey = runContext.render(apiKey);
        String resolvedRuleId = runContext.render(ruleId);

        // Construct the API endpoint URL
        String apiEndpoint = resolvedUrl + "/api/v1/rules/" + resolvedRuleId + "/run";

        // Create connection
        URL apiUrl = new URL(apiEndpoint);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

        // Set request method and headers
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + resolvedApiKey);
        connection.setConnectTimeout((int) httpTimeout.toMillis());
        connection.setReadTimeout((int) httpTimeout.toMillis());

        // Enable output for POST
        connection.setDoOutput(true);

        // Send request
        connection.getOutputStream().write("{}".getBytes());

        // Get response
        int responseCode = connection.getResponseCode();

        if (responseCode >= 200 && responseCode < 300) {
            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Return successful output
            return Output.builder()
                    .status("SUCCESS")
                    .ruleId(resolvedRuleId)
                    .responseCode(responseCode)
                    .response(response.toString())
                    .build();
        } else {
            // Handle error response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Return error output
            return Output.builder()
                    .status("ERROR")
                    .ruleId(resolvedRuleId)
                    .responseCode(responseCode)
                    .response(response.toString())
                    .build();
        }
    }

    @SuperBuilder
    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {
        @Schema(title = "The status of the rule execution", description = "SUCCESS or ERROR")
        private String status;

        @Schema(title = "The ID of the rule that was executed")
        private String ruleId;

        @Schema(title = "The HTTP response code from the Sifflet API")
        private int responseCode;

        @Schema(title = "The response body from the Sifflet API")
        private String response;
    }
}
