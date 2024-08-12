public class StringLiteral implements Expression<String> {

    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public String eval() {
        return value;
    }

}
