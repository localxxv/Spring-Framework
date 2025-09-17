import java.util.function.DoubleUnaryOperator;

public class AnyFunction implements CalculatedFunction {

    public DoubleUnaryOperator  dubl11;

    @Override
    public double f(double x) {
        dubl11.applyAsDouble(x);
        return dubl11.applyAsDouble(x);
    }


    public AnyFunction(DoubleUnaryOperator dubl11) {
        this. dubl11 = dubl11;
    }




}
