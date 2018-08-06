# Feing based generic client
A generic feign based http client implementation that may be used to invoke any services directly or via Eureka

To use:
Set a dependency for this jar/project.
Declare the configuration ServiceClientConfiguration as a configuration in your application.
Inject ClientConfigurer into the services that need to invoke other services.
Configure a client using any of the methods in the ClientConfigurer.
Use the direct client for direct calls, or the load balanced client for invocations using Eureka based service ID.

