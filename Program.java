import java.util.Scanner;

public class Program {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);



        System.out.println("od barku do lokcia: ");
        double elbowShoulder = sc.nextDouble();

        System.out.println("od lokcia do nadgarstka: ");
        double wristElbow = sc.nextDouble();

        System.out.println("od barku do nadgarstka: ");
        double shoulderWrist = sc.nextDouble();

        System.out.println("od biodra do kolana: ");
        double kneeHip = sc.nextDouble();

        System.out.println("od biodra do biorka: ");
        double hipShoulder = sc.nextDouble();

        System.out.println("od barku do kolana: ");
        double shoulderKnee = sc.nextDouble();


        System.out.println("od kolana do kostki: ");
        double ankleKnee = sc.nextDouble();

        System.out.println("od kolana do biodra: ");
        double kneeHip2 = sc.nextDouble();

        System.out.println("od biodra do kostki: ");
        double hipAnkle = sc.nextDouble();





        Angles angles = new Angles(
                wristElbow, elbowShoulder, shoulderWrist,
                kneeHip, hipShoulder, shoulderKnee,
                ankleKnee, kneeHip2, hipAnkle
        );

        System.out.println("Kąt w lokciu");
        System.out.println(angles.getElbowAngleDeg());

        System.out.println("Kąt w biodrze");
        System.out.println(angles.getHipAngleDeg());

        System.out.println("Kąt w kolanie");
        System.out.println(angles.getKneeAngleDeg());


    }
}