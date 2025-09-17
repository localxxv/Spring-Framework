public class Program {
    public static void main(String[] args) {
        CalculatedFunction coeff= new Polynomial(-1,-8,2,1);
        CalculatedFunction f = new AnyFunction(x ->Math.sin(x)*2);

        Integral intglr1 = new TrapezoidMethod(coeff, 0, 4, 16);
        System.out.println(intglr1.calculate());


    }
}