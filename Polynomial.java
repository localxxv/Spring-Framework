

class Polynomial implements CalculatedFunction{
    private double[] coeff;//1 2 -8 -1


    public Polynomial(double... coeff) {
        this.coeff = coeff;
    }


    @Override
    public double f(double x) {
       double result = 0;
       for (int i = 0; i < coeff.length; i++) {
        result += this.coeff[i] * Math.pow(x,i);
       }

        return result;
    }
    @Override
    public  String toString() {

        int Degree = this.coeff.length - 1;//3
        String res = "";
        int index = 0;
        for (int i = Degree; i > 0; i--) {
            if (this.coeff[index]<0) {
                res+= "-" + Math.abs( this.coeff[index]) + "x^"  + i;
            }
            else {
                res += "+" + this.coeff[index] + "x^" + i ;
            }
            index++;


        }
        if (this.coeff[this.coeff.length-1]<0) {
        res +=  + this.coeff[this.coeff.length-1];
        }else
            res += "+" + this.coeff[this.coeff.length-1];

        String trimmed = res.substring(1);
        return trimmed;


    }

}












