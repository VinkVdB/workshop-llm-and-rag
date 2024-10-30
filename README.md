# Workshop: LLM and RAG

## Setting Up

To set up the `application-secrets.properties` in the root, add the following values:

```
spring.ai.azure.openai.endpoint=${AZURE_OPENAI_ENDPOINT}
spring.ai.azure.openai.api-key=${AZURE_OPENAI_API_KEY}
```

## Modules Overview

### Module 1: Connecting to the LLM
**Objective:**
Learn how to connect to ChatGPT using an API key within a Spring Boot application.

**Contents:**
- Setting up Spring AI dependencies.
- Configuring API keys.
- Writing a simple CommandLineRunner to send and receive a single message from ChatGPT.

**Exercises:**
- Implement a CommandLineRunner that sends a prompt to ChatGPT and prints the response.
- Handle exceptions and errors from the API.

### Module 2: Prompt Engineering - Tips & Tricks
**Objective:**
Understand the fundamentals of prompt engineering, e.g. one-shot, multishot, chain of thought.

**Contents:**
- Best practices for crafting prompts.
- Using system prompts effectively.

**Exercises:**
- Craft prompt to achieve better responses, play around with tricky situations.
- Experiment with system prompts to guide the LLM's behavior.

### Module 3: Introduction to Spring AI
**Objective:**
Explore the features of Spring AI and how it integrates with Spring Boot applications.

**Contents:**
- Overview of Spring AI capabilities.
- Configuring Spring AI in a Spring Boot project.
- Dependency injection with Spring AI components.

**Exercises:**
- Set up a basic Spring AI application.
- Inject and use Spring AI beans in services and controllers.
- Provide a ChatMemory bean and use it in a PromptChatMemoryAdvisor.

### Module 4: Using Tools and Services
**Objective:**
Learn how to extend the LLM's capabilities by integrating custom tools and services.

**Contents:**
- Creating custom functions that the LLM can invoke.
- Understanding how Spring AI handles tool integration.

**Exercises:**
- Implement a method as a tool that retrieves a password.
- Implement a method as a tool that overrides the password.
- Configure the LLM to use this tool when generating responses.

### Module 5: Context Enrichment with Text Files
**Objective:**
We need to enrich the prompt by adding state, such as the current date and user name.
Understand context enrichment by reading and incorporating text file contents into prompts.

**Contents:**
- Reading files in Spring Boot applications.
- Enriching prompts with external data.
- Managing context size limitations.

**Exercises:**
- Enrich prompts with the **current date** and **user name**.
- Information Retrieval:
  - Enrich the prompt with contents from a single file.
  - Limit context size to 500 characters and observe the impact. This is to mimic the real-world scenario where the context size is limited.

### Module 6: Introduction to Retrieval Augmented Generation (RAG)
**Objective:**
Explore RAG concepts and understand when to apply them.

**Contents:**
- Challenges with large context sizes.
- Overview of RAG and its benefits.
- Introduction to chunking and vector databases.

**Exercises:**
- Discuss the differences between traditional search (e.g., Elasticsearch) and vector databases.
- Use Spring AI's QuestionAnswerAdvisor and a Vectorstore to retrieve relevant information.

**Notes:**
For this module we've chosen to exclude a reranker, as it's not necessary for the exercises. 
However, we recommend using a reranker in a production environment and return more documents from the vector store.
The reranker will filter out the most relevant documents from the vector store and rank them based on the user's query.

### Module 7: Working with Text Embeddings
**Objective:**
Learn about text embeddings and how to use them for similarity searches.

**Contents:**
- Explanation of embeddings and vector spaces.
- Performing arithmetic with embeddings (e.g., king - queen + woman = man).

**Exercises:**
- Embedding Arithmetic:
  - Use embeddings to perform analogy tasks.
  - Visualize embeddings in a vector space.
- Manual Vector Retrieval:
  - Manually fetch vectors to understand relevance scoring.
  - Observe issues with irrelevant data retrieval.

**Notes:**
See notebooks in https://github.com/lamyiowce/word2viz/tree/master/notebooks_migdal for more inspiration and graphs.

### Module 8: Advanced Configurations
**Objective:**
Explore various configuration options to fine-tune the LLM interactions.

**Contents:**
- Adjusting model parameters (temperature, max tokens, etc.).
- Handling rate limits and API quotas.
- Logging and monitoring LLM interactions.

**Exercises:**
- Experiment with different configuration settings.
- Implement logging to audit prompts and responses.

### Module 9: Integrating DALLE-3
**Objective:**
Learn how to generate images using DALLE-3 and integrate it into your application.

**Contents:**
- Overview of DALLE-3 capabilities.
- Configuring image generation in Spring AI.
- Using system prompts to guide image creation.

**Exercises:**
- Generate images for your Pokémon, recipe, attire, or monster.
- Customize prompts to influence the style and content of the images.

### Module 10: Prompt Injection and Security
**Objective:**
Understand the risks of prompt injection and how to mitigate them.

**Contents:**
- Explanation of prompt injection attacks.
- Best practices for securing LLM applications.
- Testing prompts for vulnerabilities.

**Exercises:**
- Prompt Injection Simulation:
  - Craft inputs that could lead to prompt injection.
  - Observe how the LLM responds and identify weaknesses.
- Mitigation Strategies:
  - Implement input validation and sanitization.
  - Use guardrails or prompt templates to enforce boundaries.

**Notes:**
There is currently no content in this module, the exercise is open to the user.
Combine the knowledge you've gathered over the workshop and play around with a bot that stores a password.
Attempt to use prompt injection to get past your defenses, and keep iterating on your security measures.

Do not simply use the Spring SafeGuardAdvisor, to understand prompt injection start from scratch to appreciate the risks and how to mitigate them.
