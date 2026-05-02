import java.util.Scanner;

public class main {

    public static void main(String[] args) {
        // EXTRA STUFF
        Scanner myScanner = new Scanner(System.in);
        Byte mySelection;

        // APLICARE PENTRU ADEVERINTA

        System.out.println(
            "Alege categoria de adeverinta:\n1) Adeverinta de student\n2) Adeverinta de ???\n[1/2/(N)one]\n"
        );
        mySelection = myScanner.nextByte();
        if (mySelection == '1') {
            System.out.println("Adev de student aleasa!\n");
        } else if (mySelection == '2') {
            System.out.println("Adev de ???\n");
        } else if (mySelection == 'N') {
            System.out.println("None selected. Bye\n!");
            return;
        } else {
            System.out.println("Selectie gresita!\n[1/2/(N)one]\n");
            return;
        }
    }
}
