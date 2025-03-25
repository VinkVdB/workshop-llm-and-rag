# Workshop: LLM and RAG

## Setting Up API Key

Add given API key to a central properties file, e.g. `application-secrets.properties` file in the root directory.

Or alternatively, add the API key as an environment variable in your run configuration.

### OPTION A (recommended): application-secrets.properties

To set up the `application-secrets.properties` in the root, add the following and replace `api_key_here` with the actual key:

#### For OpenAI:
```
spring.ai.openai.api-key='api_key_here'
```
#### For Azure:
```
spring.ai.azure.openai.api-key='api_key_here'
spring.ai.azure.openai.endpoint='endpoint_here'
```

### OPTION B: Environment Variables

Alternatively, you can set the environment variables directly in your run configurations:

To set the environment variable, add ```OPENAI_API_KEY='api_key_here'``` to your run config's environment variables.
