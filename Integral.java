public abstract class Integral {
    protected  CalculatedFunction func;
    protected final double a, b;




    public Integral(CalculatedFunction f, double a, double b) {
        if (f == null) throw new IllegalArgumentException("Function cannot be null");
        this.func = f; this.a = a; this.b = b;
    }

    public abstract double calculate();

}


