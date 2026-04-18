# Kestra Sifflet Plugin

## What

- Provides plugin components under `io.kestra.plugin.sifflet`.
- Includes classes such as `RunRule`.

## Why

- This plugin integrates Kestra with Sifflet.
- It provides tasks that execute data quality rules on the Sifflet platform.

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
