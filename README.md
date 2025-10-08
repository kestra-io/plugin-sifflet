<p align="center">
  <a href="https://www.kestra.io">
    <img src="https://kestra.io/banner.png"  alt="Kestra workflow orchestrator" />
  </a>
</p>

<h1 align="center" style="border-bottom: none">
    Event-Driven Declarative Orchestrator
</h1>

<div align="center">
 <a href="https://github.com/kestra-io/kestra/releases"><img src="https://img.shields.io/github/tag-pre/kestra-io/kestra.svg?color=blueviolet" alt="Last Version" /></a>
  <a href="https://github.com/kestra-io/kestra/blob/develop/LICENSE"><img src="https://img.shields.io/github/license/kestra-io/kestra?color=blueviolet" alt="License" /></a>
  <a href="https://github.com/kestra-io/kestra/stargazers"><img src="https://img.shields.io/github/stars/kestra-io/kestra?color=blueviolet&logo=github" alt="Github star" /></a> <br>
<a href="https://kestra.io"><img src="https://img.shields.io/badge/Website-kestra.io-192A4E?color=blueviolet" alt="Kestra infinitely scalable orchestration and scheduling platform"></a>
<a href="https://kestra.io/slack"><img src="https://img.shields.io/badge/Slack-Join%20Community-blueviolet?logo=slack" alt="Slack"></a>
</div>

<br />

<p align="center">
  <a href="https://twitter.com/kestra_io" style="margin: 0 10px;">
        <img src="https://kestra.io/twitter.svg" alt="twitter" width="35" height="25" /></a>
  <a href="https://www.linkedin.com/company/kestra/" style="margin: 0 10px;">
        <img src="https://kestra.io/linkedin.svg" alt="linkedin" width="35" height="25" /></a>
  <a href="https://www.youtube.com/@kestra-io" style="margin: 0 10px;">
        <img src="https://kestra.io/youtube.svg" alt="youtube" width="35" height="25" /></a>
</p>

<br />
<p align="center">
    <a href="https://go.kestra.io/video/product-overview" target="_blank">
        <img src="https://kestra.io/startvideo.png" alt="Get started in 4 minutes with Kestra" width="640px" />
    </a>
</p>
<p align="center" style="color:grey;"><i>Get started with Kestra in 4 minutes.</i></p>


# Kestra Sifflet Plugin

This plugin provides tasks to interact with Sifflet, a data quality and observability platform. It allows you to run rules and manage data quality checks directly from your Kestra workflows.

## Tasks

### RunRule

The `RunRule` task allows you to execute Sifflet rules and monitor their execution status. It provides the following features:

- Execute rules by their ID
- Monitor rule execution status
- Get detailed execution results
- Handle rule execution failures

#### Example

```yaml
id: sifflet-rule-execution
namespace: dev

tasks:
  - id: run-sifflet-rule
    type: io.kestra.plugin.sifflet.RunRule
    url: "https://your-sifflet-instance.com"
    token: "{{ secret('SIFFLET_TOKEN') }}"
    ruleId: "rule-123"
    wait: true
    maxDuration: "PT5M"
```

## Installation

The plugin is available on the Kestra marketplace. You can install it by adding the following to your `kestra.yml`:

```yaml
plugins:
  - io.kestra.plugin.sifflet
```

## Configuration

The plugin requires the following configuration:

- `url`: The URL of your Sifflet instance
- `token`: Your Sifflet API token (should be stored as a secret)

## License

Apache 2.0 © [Kestra Technologies](https://kestra.io)

We release new versions every month. Give the [main repository](https://github.com/kestra-io/kestra) a star to stay up to date with the latest releases and get notified about future updates.

![Star the repo](https://kestra.io/star.gif)


