package infosupport.be.domain;

public record Joke(JokeCategory category, String joke) {
    @Override
    public String toString() {
        return "Joke{" +
                "category='" + category + '\'' +
                ", joke='" + joke + '\'' +
                '}';
    }
}
