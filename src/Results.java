/**
 * Created by Magdalena Polak on 09.05.2016.
 */
public class Results {

    public static void main (String [] args)
    {
        Algotithms a = new Algotithms(100, 10000, 30, 10);
        System.out.println("Założenia symulacji: \nW systemie musi byc co najmniej 2 razy więcej wolnych ramek," +
                "\nniż procesów(minimalna liczba wolnych ramek == 3," +
                "\nsterowanie czestoscią błędów strony: maksymalna liczba błędów: 60% odwołan\n ");
        System.out.println("Equal: " + a.EQUAL());
        System.out.println("******************************************************************************************");
        System.out.println("Proportional: " + a.PROPORTIONAL());
        System.out.println("******************************************************************************************");
        System.out.println("Zone Model: " + a.ALG_4(30));
        System.out.println("******************************************************************************************");
        System.out.println("Steering PF: " + a.ALG_3());


    }
}
