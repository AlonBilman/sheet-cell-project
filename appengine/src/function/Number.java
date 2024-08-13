package function;

public class Number implements Expression<Double> {
    private final Double value;
    public Number(Double value) {
        this.value = value;
    }
    public Number(Integer value) {
        this.value = Double.valueOf(value);
    }
    @Override
    public Double eval() {
        return value;
    }
}
