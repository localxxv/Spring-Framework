public class LagrangeMethod {

    public double[] x;
    public double X;
    public double[]y;



    public double LagrangeMethod(double[] x, double[]y, double X) {

        for (int i = 0; i < x.length; i++) {
        double d = y[i];
            for (int j = 0; j < x.length; j++) {
if(i != j) {
    d = d * (X - x[j]) / (x[i] - x[j]);
}
            }
        }


        return 0;

    }

}
