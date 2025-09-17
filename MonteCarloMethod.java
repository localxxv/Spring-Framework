import java.util.Random;


public class MonteCarloMethod extends Integral {
    private int n;
    private Random rand;

    public MonteCarloMethod(CalculatedFunction f, double a, double b, int n) {
        super(f, a, b);
        this.n = n;
        rand = new Random();
    }

    @Override
    public double calculate() {

        double sum = 0.0;
        double len = (b - a);
        for (int i = 0; i < n; i++) {
            double x = a + rand.nextDouble() * len;
            sum += func.f(x);


        }
        return (sum / n) * len;
    }


}
