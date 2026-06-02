# How to use the Sifflet plugin

Run Sifflet data quality rules from Kestra flows.

## Authentication

Set `apiKey` (required) to your Sifflet API key. Optionally set `baseUrl` (default `https://api.siffletdata.com`) and `requestTimeout` in seconds (default 30). Store secrets in [secrets](https://kestra.io/docs/concepts/secret) and apply connection properties globally with [plugin defaults](https://kestra.io/docs/workflow-components/plugin-defaults).

## Tasks

`RunRule` triggers a Sifflet rule run — set `ruleId` to specify which rule to execute. The output includes `ruleId`, `status` (`SUCCESS` or `FAILED`), `statusCode`, and `response` (raw API response).
