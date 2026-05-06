import java.util.Scanner;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Email: ");
        if (!scanner.hasNextLine()) {
            System.out.println("Nu s-a primit email. Bye!");
            return;
        }
        String email = scanner.nextLine().trim();

        System.out.print("Parola: ");
        if (!scanner.hasNextLine()) {
            System.out.println("Nu s-a primit parola. Bye!");
            return;
        }
        String parola = scanner.nextLine().trim();

        Utilizator user = Autentificare.login(email, parola);
        if (user == null) {
            System.out.println("Autentificare esuata (email/parola gresite sau user inexistent).");
            return;
        }

        System.out.println("Autentificat ca: " + user.getNume() + " " + user.getPrenume());

        Adeverinta adeverinta = new Adeverinta(
            new Date(),
            null,
            StareCerere.incarcataDeStudent,
            null,
            ""
        );
        adeverinta.alegere_categorie(scanner);

        System.out.println("Categorie selectata: " + adeverinta.getCategorieCerere());
    }
}
