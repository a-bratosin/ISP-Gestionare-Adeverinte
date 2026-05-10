import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utilizator {

    private String id;
    private String nume;
    private String prenume;
    private String telefon;
    private String email;
    private String parola;

    //TODO: pune ceva valori mai ok
    public Utilizator() {
        this.nume = "heheheha";
        this.prenume = "heheheha";
        this.telefon = "heheheha";
        this.email = "heheheha";
        this.parola = "heheheha";
    }

    public Utilizator(
        String id,
        String nume,
        String prenume,
        String telefon,
        String email,
        String parola
    ) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.telefon = telefon;
        this.email = email;
        this.parola = parola;
    }

    public Utilizator(String[] entry) {
        this(
            (entry != null && entry.length > 0) ? entry[0] : "",
            (entry != null && entry.length > 1) ? entry[1] : "",
            (entry != null && entry.length > 2) ? entry[2] : "",
            (entry != null && entry.length > 3) ? entry[3] : "",
            (entry != null && entry.length > 4) ? entry[4] : "",
            (entry != null && entry.length > 5) ? entry[5] : ""
        );
    }

    String getNume() {
        return nume;
    }

    String getId() {
        return id;
    }

    String getPrenume() {
        return prenume;
    }

    String getTelefon() {
        return telefon;
    }

    String getEmail() {
        return email;
    }

    String getParola() {
        return parola;
    }

    void setNume(String nume) {
        this.nume = nume;
    }

    void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    void setEmail(String email) {
        this.email = email;
    }

    void setParola(String parola) {
        this.parola = parola;
    }

    void setId(String id) {
        this.id = id;
    }

    public void modificareDate(Scanner scanner) {
        System.out.println("--- Modificare date cont ---");
        System.out.println("Email actual: " + email);
        System.out.print("Email nou (enter pentru a pastra): ");
        String emailNou = scanner.nextLine().trim();
        if (!emailNou.isEmpty()) {
            this.email = emailNou;
        }

        System.out.println("Telefon actual: " + telefon);
        System.out.print("Telefon nou (enter pentru a pastra): ");
        String telNou = scanner.nextLine().trim();
        if (!telNou.isEmpty()) {
            this.telefon = telNou;
        }

        updateInCsv();
        System.out.println("Datele au fost actualizate cu succes.");
    }

    public void resetareParola(Scanner scanner) {
        System.out.println("--- Resetare parola ---");
        System.out.print("Introduceti parola actuala: ");
        String pwdActuala = scanner.nextLine().trim();

        if (!pwdActuala.equals(this.parola)) {
            System.out.println("Parola incorecta. Resetare anulata.");
            return;
        }

        System.out.print("Introduceti parola noua: ");
        String pwdNoua = scanner.nextLine().trim();
        System.out.print("Confirmati parola noua: ");
        String pwdConfirm = scanner.nextLine().trim();

        if (pwdNoua.isEmpty()) {
            System.out.println("Parola nu poate fi goala.");
            return;
        }

        if (!pwdNoua.equals(pwdConfirm)) {
            System.out.println("Parolele nu coincid. Resetare anulata.");
            return;
        }

        this.parola = pwdNoua;
        updateInCsv();
        System.out.println("Parola a fost resetata cu succes.");
    }

    private void updateInCsv() {
        Path csvPath = Path.of("db", "utilizatori.csv");
        try {
            List<String> lines = Files.readAllLines(
                csvPath,
                StandardCharsets.UTF_8
            );
            List<String> newLines = new ArrayList<>();
            if (lines.isEmpty()) return;

            newLines.add(lines.get(0)); // header

            boolean found = false;
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] entry = CsvManager.parseCsvLine(line, ',');
                if (entry[0].equals(this.id)) {
                    // indicii din csv: 0 - id, 1- nume, 2-prenume, 3-telefon, 4-email, 5-parola, 6-rol
                    String row = String.format("%s,%s,%s,%s,%s,%s,%s,\n",
                        CsvManager.csvEscape(id),
                        CsvManager.csvEscape(nume),
                        CsvManager.csvEscape(prenume),
                        CsvManager.csvEscape(telefon),
                        CsvManager.csvEscape(email),
                        CsvManager.csvEscape(parola),
                        CsvManager.csvEscape(entry[6]) // Rolul ramane neschimbat
                    ).trim(); // trim to avoid double newline if added later
                    newLines.add(row);
                    found = true;
                } else {
                    newLines.add(line);
                }
            }

            if (found) {
                Files.write(csvPath, newLines, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
