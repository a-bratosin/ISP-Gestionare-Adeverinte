// Clasă care înglobează funcționalitățile de autentificare și înregistrare ale aplicației
// Într-o implementare reală, ar fi doar un wrapper pentru API-ul serviciului extern de autentificare al facultății
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

class Autentificare {

    private static CsvManager csvManager = new CsvManager();
    private static final String DB_DIR = "db";

    public static Utilizator login(Scanner scanner) {
        System.out.print("Email: ");
        if (!scanner.hasNextLine()) {
            System.out.println("Nu s-a primit email. Bye!");
            return null;
        }
        String email = scanner.nextLine().trim();

        System.out.print("Parola: ");
        if (!scanner.hasNextLine()) {
            System.out.println("Nu s-a primit parola. Bye!");
            return null;
        }
        String parola = scanner.nextLine().trim();

        return login(email, parola);
    }

    public static Utilizator login(String email, String parola) {
        // indicii din csv:
        // 0 - id, 1- nume, 2-prenume, 3-telefon, 4-email, 5-parola, 6-rol,
        String[] dateDeBaza = csvManager.findRow(
            DB_DIR + File.separator + "utilizatori.csv",
            "email",
            email
        );

        if (dateDeBaza == null) {
            return null; // User not found
        }

        String storedPassword = dateDeBaza[5];

        if (!storedPassword.equals(parola)) {
            return null; // Incorrect password
        }

        String userId = dateDeBaza[0];
        String rol = dateDeBaza[6];
        String[] dateSubclasa = null;

        // Student
        if (rol.equals("Student")) {
            dateSubclasa = csvManager.findRow(
                DB_DIR + File.separator + "studenti.csv",
                "id",
                userId
            );
            return new Student(dateDeBaza, dateSubclasa);
        } else if (rol.equals("Secretar")) {
            dateSubclasa = csvManager.findRow(
                DB_DIR + File.separator + "secretari.csv",
                "id",
                userId
            );
            return new SecretarDeAn(dateDeBaza, dateSubclasa);
        } else if (rol.equals("Decan")) {
            dateSubclasa = csvManager.findRow(
                DB_DIR + File.separator + "decani.csv",
                "id",
                userId
            );
            return new Decan(dateDeBaza, dateSubclasa);
        }
        return new Utilizator(dateDeBaza);
    }
}
