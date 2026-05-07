// Clasă care înglobează funcționalitățile de autentificare și înregistrare ale aplicației
// Într-o implementare reală, ar fi doar un wrapper pentru API-ul serviciului extern de autentificare al facultății
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;

class Autentificare {

    private static CsvManager csvManager = new CsvManager();
    private static final String DB_DIR = "db";

    public static void inregistrare(Scanner scanner) {
        System.out.println("------- INREGISTRARE -------");
        System.out.println("Selectati tipul de utilizator:");
        System.out.println("1 - Student");
        System.out.println("2 - Secretar");
        System.out.print("Optiune: ");
        
        int tip = -1;
        if (scanner.hasNextInt()) {
            tip = scanner.nextInt();
        }
        if (scanner.hasNextLine()) scanner.nextLine(); // consume newline

        if (tip != 1 && tip != 2) {
            System.out.println("Optiune invalida.");
            return;
        }

        System.out.print("Nume: ");
        String nume = scanner.nextLine().trim();
        System.out.print("Prenume: ");
        String prenume = scanner.nextLine().trim();
        System.out.print("Telefon: ");
        String telefon = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Parola: ");
        String parola = scanner.nextLine().trim();

        // Get next ID
        String nextId = "0";
        try {
            List<String> lines = Files.readAllLines(Path.of(DB_DIR, "utilizatori.csv"), StandardCharsets.UTF_8);
            if (lines.size() > 1) {
                String lastLine = lines.get(lines.size() - 1);
                String lastId = lastLine.split(",")[0];
                nextId = String.valueOf(Integer.parseInt(lastId) + 1);
            }
        } catch (IOException | NumberFormatException e) {
            // keep "0" or handle error
        }

        String rol = (tip == 1) ? "Student" : "Secretar";
        
        // Save to utilizatori.csv
        String baseRow = String.format("%s,%s,%s,%s,%s,%s,%s,\n", 
            CsvManager.csvEscape(nextId),
            CsvManager.csvEscape(nume),
            CsvManager.csvEscape(prenume),
            CsvManager.csvEscape(telefon),
            CsvManager.csvEscape(email),
            CsvManager.csvEscape(parola),
            CsvManager.csvEscape(rol)
        );

        try {
            Files.writeString(Path.of(DB_DIR, "utilizatori.csv"), baseRow, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            
            if (tip == 1) {
                System.out.print("Nr. Matriceal: ");
                String nrMat = scanner.nextLine().trim();
                System.out.print("Serie: ");
                String serie = scanner.nextLine().trim();
                System.out.print("Grupa: ");
                String grupa = scanner.nextLine().trim();
                
                String studentRow = String.format("%s,%s,%s,%s\n",
                    CsvManager.csvEscape(nextId),
                    CsvManager.csvEscape(nrMat),
                    CsvManager.csvEscape(serie),
                    CsvManager.csvEscape(grupa)
                );
                Files.writeString(Path.of(DB_DIR, "studenti.csv"), studentRow, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            } else {
                System.out.print("An gestionat: ");
                String an = scanner.nextLine().trim();
                System.out.print("Program de lucru: ");
                String progL = scanner.nextLine().trim();
                System.out.print("Program public: ");
                String progP = scanner.nextLine().trim();

                String secRow = String.format("%s,%s,%s,%s\n",
                    CsvManager.csvEscape(nextId),
                    CsvManager.csvEscape(an),
                    CsvManager.csvEscape(progL),
                    CsvManager.csvEscape(progP)
                );
                Files.writeString(Path.of(DB_DIR, "secretari.csv"), secRow, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            }
            System.out.println("Inregistrare reusita! ID: " + nextId);
        } catch (IOException e) {
            System.err.println("Eroare la salvarea datelor.");
            e.printStackTrace();
        }
    }

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

    public static Utilizator getUserById(String userId) {
        String[] dateDeBaza = csvManager.findRow(
            DB_DIR + File.separator + "utilizatori.csv",
            "userID",
            userId
        );

        if (dateDeBaza == null) {
            return null;
        }

        String rol = dateDeBaza[6];
        String[] dateSubclasa = null;

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
