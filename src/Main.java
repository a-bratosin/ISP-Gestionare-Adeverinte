import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Benvenuti!");
        System.out.println("1 - Login");
        System.out.println("2 - Inregistrare");
        System.out.print("Alegere: ");
        
        int alegere = 0;
        if (scanner.hasNextInt()) {
            alegere = scanner.nextInt();
        }
        if (scanner.hasNextLine()) scanner.nextLine(); // consume newline

        if (alegere == 2) {
            Autentificare.inregistrare(scanner);
            System.out.println("Acum va puteti loga.");
            System.out.println("--------------------");
        } else if (alegere != 1) {
            System.out.println("Optiune invalida. Iesire.");
            return;
        }

        Utilizator user = Autentificare.login(scanner);
        if (user == null) {
            System.out.println("Autentificare esuata (email/parola gresite sau user inexistent).");
            return;
        }

        System.out.println("Autentificat ca: " + user.getNume() + " " + user.getPrenume());

        if (user instanceof Student) {
            Student student = (Student) user;
            student.incarcareAdeverinta(scanner);
        } else if (user instanceof SecretarDeAn) {
            SecretarDeAn secretar = (SecretarDeAn) user;
            secretar.valideazaCerere(scanner);
        } else if (user instanceof Decan) {
            Decan decan = (Decan) user;
            System.out.println("Meniu Decan:");
            System.out.println("1 - Gestioneaza adeverinte");
            System.out.print("Optiune: ");
            int opt = 0;
            if (scanner.hasNextInt()) opt = scanner.nextInt();
            if (scanner.hasNextLine()) scanner.nextLine();

            if (opt == 1) {
                decan.gestioneazaAdeverinte(scanner);
            } else {
                System.out.println("Optiune invalida.");
            }
        } else {
            System.out.println("Rolul utilizatorului nu are actiuni implementate in Main.");
        }
    }
}
