package infosupport.be.util;

public record EmbeddingVector(float[] vector) {

    // Addition with another EmbeddingVector
    public EmbeddingVector plus(EmbeddingVector other) {
        float[] result = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = this.vector[i] + other.vector[i];
        }
        return new EmbeddingVector(result);
    }

    // Subtraction with another EmbeddingVector
    public EmbeddingVector minus(EmbeddingVector other) {
        float[] result = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = this.vector[i] - other.vector[i];
        }
        return new EmbeddingVector(result);
    }
}
