package infosupport.be.util;

/**
 * Represents an embedding vector as a float array, typically with 1536 dimensions.
 */
public record EmbeddingVector(float[] vector) {

    /**
     * Addition with another EmbeddingVector
     * @param other the vector to add
     * @return The result of element-wise addition
     */
    public EmbeddingVector plus(EmbeddingVector other) {
        float[] result = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = this.vector[i] + other.vector[i];
        }
        return new EmbeddingVector(result);
    }

    /**
     * Subtraction with another EmbeddingVector
     * @param other the vector to subtract
     * @return the result of element-wise subtraction
     */
    public EmbeddingVector minus(EmbeddingVector other) {
        float[] result = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = this.vector[i] - other.vector[i];
        }
        return new EmbeddingVector(result);
    }
}
