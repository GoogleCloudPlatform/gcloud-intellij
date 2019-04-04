# Cloud Code for IntelliJ 
<img src="docs/images/cloud_code.png" alt="Cloud Code" width="150" />

Cloud Code for IntelliJ is a plugin that helps facilitates cloud-native in the JetBrains 
family of IDEs. The plugin adds support for Kubernetes applications, as well as support for various
Google Cloud Platform products.

## Features

* [Kubernetes](https://github.com/GoogleCloudPlatform/google-cloud-intellij/tree/master/kubernetes) Streamline your Kubernetes development process in the JetBrains family of IDEs.
* [Google Cloud Java Client Libraries](https://cloud.google.com/tools/intellij/docs/client-libraries) 
  Add Java client libraries to your project, enable Google Cloud APIs, and create service accounts.
* [Google Cloud Storage](https://cloud.google.com/storage/) 
  Browse your Google Cloud Storage buckets.
* [Google Cloud Source Repositories](https://cloud.google.com/tools/cloud-repositories/) 
  Fully-featured private Git repositories hosted on Google Cloud Platform.
* The [Google Cloud Debugger](https://cloud.google.com/tools/cloud-debugger/) 
  The Cloud Debugger can inspect the state of a Java or Kotlin application running on 
  GCP without stopping or slowing down the application.
* [Google App Engine](https://cloud.google.com/appengine/docs/) deployment via the Cloud SDK.

    (_For detailed user documentation on GCP features, visit our documentation_
 [website](https://cloud.google.com/tools/intellij/docs/?utm_source=github&utm_medium=google-cloud-intellij&utm_campaign=ToolsforIntelliJ)).
 
## Supported Platforms

The Cloud Code for IntelliJ plugin supports the entire JetBrains family of IDEs, versions 2018.2 or 
later. Both the free and paid editions of the
IDEs are supported. 

For GCP functionality, full support is available for IntelliJ IDEA Ultimate Edition, with limited
support for the other platforms. See this [feature matrix](docs/gcp-feature-matrix.md) 
for more details.

## Installation

You can find our plugin in the Jetbrains plugin repository by going to IntelliJ -> Settings -> Browse Repositories, and search for `Cloud Code`. 

## Resources
* [Learn More](https://cloud.google.com/code): Learn more about the Cloud Code Project and what it has to offer.
* [Documentation](https://cloud.google.com/code/docs/intellij/): Visit our official documentation to learn more.
* [Kubernetes Sample Applications](https://github.com/GoogleCloudPlatform/cloud-code-samples): Starter applications for working with Kubernetes; available in Java, Node, Python, and Go.
* [File an Issue](https://github.com/GoogleCloudPlatform/google-cloud-intellij/issues): If you discover an issue please file a bug and we will address it. 
* [Request a Feature](https://github.com/GoogleCloudPlatform/google-cloud-intellij/issues): If you have any feature requests, please file a request.

### Pre-releases 

The pre-release binaries are being deployed to the Jetbrains plugin repository on an alpha
channel. To install them please perform the following steps:

1. Install the Cloud Code plugin
    1. Copy this URL `https://plugins.jetbrains.com/plugins/alpha/8079`
    1. Use the copied URL as the Custom Plugin URL when following [these instrucions](https://www.jetbrains.com/idea/help/managing-enterprise-plugin-repositories.html)
    1. Search for the 'Cloud Code' plugin and install it.

You can also grab the latest nightly build of the plugin by following the same steps as above but 
replacing 'alpha' with 'nightly' in the URLs.

If you wish to build this plugin from source, please see the
[contributor instructions](https://github.com/GoogleCloudPlatform/google-cloud-intellij/blob/master/CONTRIBUTING.md).

## FAQ

See the [Cloud Code Kubernetes FAQ](kubernetes/docs/faq.md).
