public abstract class StringExpression implements Expression<String> {

    protected Expression<String>[] arguments;
    protected abstract int numberOfArguments();
    protected abstract String execute(Expression<String>[] argument);

    protected StringExpression(Expression<String>... arguments){
        this.arguments = arguments;
        checkArguments();
    }

    private void checkArguments(){
        if(arguments.length != numberOfArguments()) {
            throw new IllegalArgumentException("Number of arguments does not match the required number of arguments : " + numberOfArguments());
        }
    }

    @Override
    public String eval(){
        return execute(arguments);
    }

}
