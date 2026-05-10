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

        boolean running = true;
        while (running) {
            if (user instanceof Student) {
                Student student = (Student) user;
                System.out.println("\nMeniu Student:");
                System.out.println("1 - Depune cerere noua");
                System.out.println("2 - Vizualizeaza status cereri");
                System.out.println("0 - Iesire");
                System.out.print("Optiune: ");
                int opt = -1;
                if (scanner.hasNextInt()) opt = scanner.nextInt();
                if (scanner.hasNextLine()) scanner.nextLine();

                if (opt == 1) {
                    student.incarcareAdeverinta(scanner);
                } else if (opt == 2) {
                    student.vizualizareStatusAdeverinte(scanner);
                } else if (opt == 0) {
                    running = false;
                } else {
                    System.out.println("Optiune invalida.");
                }
            } else if (user instanceof SecretarDeAn) {
                SecretarDeAn secretar = (SecretarDeAn) user;
                System.out.println("\nMeniu Secretar:");
                System.out.println("1 - Valideaza cerere");
                System.out.println("0 - Iesire");
                System.out.print("Optiune: ");
                int opt = -1;
                if (scanner.hasNextInt()) opt = scanner.nextInt();
                if (scanner.hasNextLine()) scanner.nextLine();

                if (opt == 1) {
                    secretar.valideazaCerere(scanner, user);
                } else if (opt == 0) {
                    running = false;
                } else {
                    System.out.println("Optiune invalida.");
                }
            } else if (user instanceof Decan) {
                Decan decan = (Decan) user;
                System.out.println("\nMeniu Decan:");
                System.out.println("1 - Gestioneaza adeverinte");
                System.out.println("0 - Iesire");
                System.out.print("Optiune: ");
                int opt = -1;
                if (scanner.hasNextInt()) opt = scanner.nextInt();
                if (scanner.hasNextLine()) scanner.nextLine();

                if (opt == 1) {
                    decan.gestioneazaAdeverinte(scanner);
                } else if (opt == 0) {
                    running = false;
                } else {
                    System.out.println("Optiune invalida.");
                }
            } else {
                System.out.println("Rolul utilizatorului nu are actiuni implementate in Main.");
                running = false;
            }
        }
        System.out.println("La revedere!");
    }
}
