package lt.esdc.mtsishchykau.task2.container;

public class Container {
    private final long id;

    public Container(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }

    @Override
    public String toString() {
        return "C" + id;
    }
}
