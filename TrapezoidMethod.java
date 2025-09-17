public class TrapezoidMethod extends Integral{
    public int n;
    public TrapezoidMethod(CalculatedFunction f, double a, double b, int n) {
        super(f, a, b);
        if (n <= 0) throw new IllegalArgumentException("n must be positive");
        this.n = n;
    }

    @Override
    public double calculate() {
        double intg = (b - a)/n;
        double summ = (func.f(a) + func.f(b))*(0.5);
        for (int i = 1; i < n; i++) {
            summ += func.f(a + i*intg);
        }
        return summ * intg;
    }





}
