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
        String nume = dateDeBaza[1];
        String prenume = dateDeBaza[2];
        String telefon = dateDeBaza[3];
        String rol = dateDeBaza[6];
        String[] dateSubclasa = null;

        // Student
        // 0-id, 1-nrmatriceal, 2-serie, 3-grupa
        if (rol.equals("Student")) {
            dateSubclasa = csvManager.findRow(
                DB_DIR + File.separator + "studenti.csv",
                "id",
                userId
            );

            int nrMatriceal = 0;
            String serie = "";
            int grupa = 0;

            if (dateSubclasa != null && dateSubclasa.length >= 4) {
                try {
                    nrMatriceal = Integer.parseInt(dateSubclasa[1]);
                    serie = dateSubclasa[2];
                    grupa = Integer.parseInt(dateSubclasa[3]);
                } catch (NumberFormatException e) {
                    System.err.println("Eroare format date student: " + userId);
                }
            }

            return new Student(
                userId,
                nume,
                prenume,
                telefon,
                email,
                parola,
                nrMatriceal,
                serie,
                grupa
            );
        } else if (rol.equals("Secretar")) {
            dateSubclasa = csvManager.findRow(
                DB_DIR + File.separator + "secretari.csv",
                "id",
                userId
            );

            int an = 0;
            String programDeLucru = "";
            String programPublic = "";

            if (dateSubclasa != null && dateSubclasa.length >= 4) {
                try {
                    an = Integer.parseInt(dateSubclasa[1]);
                    programDeLucru = dateSubclasa[2];
                    programPublic = dateSubclasa[3];
                } catch (NumberFormatException e) {
                    System.err.println("Eroare format date secretar: " + userId);
                }
            }

            return new SecretarDeAn(
                userId,
                nume,
                prenume,
                telefon,
                email,
                parola,
                an,
                programDeLucru,
                programPublic
            );
        } else if (rol.equals("Decan")) {
            dateSubclasa = csvManager.findRow(
                DB_DIR + File.separator + "decani.csv",
                "id",
                userId
            );

            BufferedImage semnatura = null;
            String facultate = "";
            String idMandat = "";

            if (dateSubclasa != null && dateSubclasa.length >= 4) {
                try {
                    semnatura = ImageIO.read(new File(dateSubclasa[1]));
                } catch (IOException e) {
                    System.err.println("Eroare incarcare semnatura decan: " + userId);
                }
                facultate = dateSubclasa[2];
                idMandat = dateSubclasa[3];
            }

            return new Decan(
                userId,
                nume,
                prenume,
                telefon,
                email,
                parola,
                semnatura,
                facultate,
                idMandat
            );
        }
        return null;
    }
}
