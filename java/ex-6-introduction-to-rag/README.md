# Module 6: Introduction to Retrieval Augmented Generation (RAG)

## Objective
Explore RAG concepts and understand when to apply them.

## Contents
- Challenges with large context sizes.
- Overview of RAG and its benefits.
- Introduction to chunking and vector databases.

## Exercises
1. Discuss the differences between traditional search (e.g., Elasticsearch) and vector databases.
2. Use Spring AI's QuestionAnswerAdvisor and a Vectorstore to retrieve relevant information.

## Notes
For this module we've chosen to exclude a reranker, as it's not necessary for the exercises. 
However, we recommend using a reranker in a production environment and return more documents from the vector store.
The reranker will filter out the most relevant documents from the vector store and rank them based on the user's query.
