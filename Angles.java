public class Angles {

    private final double elbowAngleDeg;
    private final  double hipAngleDeg;
    private  final double kneeAngleDeg;




    public Angles(double wristElbow, double elbowShoulder, double shoulderWrist,
                  double kneeHip, double hipShoulder, double shoulderKnee, double ankleKnee, double hipAnkle, double kneeHip2) {


        this.elbowAngleDeg = angleDeg(wristElbow, elbowShoulder, shoulderWrist);
        this.hipAngleDeg   = angleDeg(kneeHip, hipShoulder, shoulderKnee);
        this.kneeAngleDeg  = angleDeg(ankleKnee, kneeHip2, hipAnkle);



    }
      public double angleDeg(double a, double b, double c) {
       double cos = (a*a + b*b - c*c) / (2 * a * b);


          if (cos > 1) cos = 1;
          if (cos < -1) cos = -1;

          return Math.toDegrees(Math.acos(cos));

     }




    public double getElbowAngleDeg() {
        return elbowAngleDeg;
    }
    public double getHipAngleDeg()   {
        return hipAngleDeg;
    }
    public double getKneeAngleDeg()  {
        return kneeAngleDeg;
    }


}
