package infosupport.be.util;

public class EmbeddingVector {
    private final float[] vector;

    public EmbeddingVector(float[] vector) {
        this.vector = vector;
    }

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

    // Access to the raw float array
    public float[] getVector() {
        return vector;
    }
}
