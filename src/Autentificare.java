// Clasă care înglobează funcționalitățile de autentificare și înregistrare ale aplicației
// Într-o implementare reală, ar fi doar un wrapper pentru API-ul serviciului extern de autentificare al facultății
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class Autentificare {

    private static CsvManager csvManager = new CsvManager();

    public static Utilizator login(String email, String parola) {
        // indicii din csv:
        // 0 - id, 1- nume, 2-prenume, 3-telefon, 4-email, 5-parola, 6-rol,
        String[] dateDeBaza = csvManager.findRow(
            "utilizatori.csv",
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
            dateSubclasa = csvManager.findRow("studenti.csv", "id", userId);

            // Autentificare is now responsible for data type conversion
            int nrMatriceal = Integer.parseInt(dateSubclasa[1]);
            String serie = dateSubclasa[2];
            int grupa = Integer.parseInt(dateSubclasa[3]);

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
            dateSubclasa = csvManager.findRow("secretari.csv", "id", userId);

            int an = Integer.parseInt(dateSubclasa[1]);
            String programDeLucru = dateSubclasa[2];
            String programPublic = dateSubclasa[3];

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
            dateSubclasa = csvManager.findRow("decani.csv", "id", userId);

            BufferedImage semnatura;
            try {
                semnatura = ImageIO.read(new File(dateSubclasa[1]));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            String facultate = dateSubclasa[2];
            String idMandat = dateSubclasa[3];

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
