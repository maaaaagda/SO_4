/**
 * Created by Magdalena Polak on 09.05.2016.
 */
public class Results {

    public static void main (String [] args)
    {
        Algotithms a = new Algotithms(1000, 10000, 250, 1000);
        System.out.println("Assumption of simulation: \nIn system has to be two more free frames than processes," +
                "(minimal free frames == 3,\n Page References:10000, Frames:100, Processes:10" +
                "\nsteering page faults: max page faults: 60% references\n ");
        System.out.println("Equal: " + a.EQUAL());
        System.out.println("******************************************************************************************");
        System.out.println("Proportional: " + a.PROPORTIONAL());
        System.out.println("******************************************************************************************");
        System.out.println("Zone Model: " + a.ALG_4(30));
        System.out.println("******************************************************************************************");
        System.out.println("Steering PF: " + a.ALG_3());


    }
}
