# Kestra Sifflet Plugin

## What

- Provides plugin components under `io.kestra.plugin.sifflet`.
- Includes classes such as `RunRule`.

## Why

- What user problem does this solve? Teams need to execute data quality rules on the Sifflet platform from orchestrated workflows instead of relying on manual console work, ad hoc scripts, or disconnected schedulers.
- Why would a team adopt this plugin in a workflow? It keeps Sifflet steps in the same Kestra flow as upstream preparation, approvals, retries, notifications, and downstream systems.
- What operational/business outcome does it enable? It reduces manual handoffs and fragmented tooling while improving reliability, traceability, and delivery speed for processes that depend on Sifflet.

## How

### Architecture

Single-module plugin. Source packages under `io.kestra.plugin`:

- `sifflet`

### Key Plugin Classes

- `io.kestra.plugin.sifflet.RunRule`

### Project Structure

```
plugin-sifflet/
├── src/main/java/io/kestra/plugin/sifflet/
├── src/test/java/io/kestra/plugin/sifflet/
├── build.gradle
└── README.md
```

## References

- https://kestra.io/docs/plugin-developer-guide
- https://kestra.io/docs/plugin-developer-guide/contribution-guidelines
