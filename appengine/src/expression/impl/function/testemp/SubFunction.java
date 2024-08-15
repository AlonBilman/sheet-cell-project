//package function;
//
//public class SubFunction extends StringExpression {
//
//    private final Expression<Double> startIndex;
//    private final Expression<Double> endIndex;
//    private final String name;
//
//    public SubFunction(Expression<String> source, Expression<Double> startIndex, Expression<Double> endIndex) {
//        super(source);
//        this.startIndex = startIndex;
//        this.endIndex = endIndex;
//        name = "SUB";
//    }
//    @Override
//    protected int numberOfArguments() {
//        return 1;
//    }
//
//    @Override
//    protected String execute(Expression<String>[] arguments) {
//        String source = arguments[0].eval();
//        int start = startIndex.eval().intValue();
//        int end = endIndex.eval().intValue();
//
//        if (start < 0 || end >= source.length() || start > end) {
//            return "!UNDEFINED!";
//        }
//
//        return source.substring(start, end + 1);
//    }
//
//    @Override
//    public String getName() {
//        return name;
//    }
//}
