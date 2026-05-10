import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Student extends Utilizator {

    private int nrMatriceal;
    private String serie;
    private int grupa;

    public Student(
        String id,
        String nume,
        String prenume,
        String telefon,
        String email,
        String parola,
        int nrMatriceal,
        String serie,
        int grupa
    ) {
        super(id, nume, prenume, telefon, email, parola);
        this.nrMatriceal = nrMatriceal;
        this.serie = serie;
        this.grupa = grupa;
    }

    public Student(String[] baseEntry, String[] studentEntry) {
        this(
            (baseEntry != null && baseEntry.length > 0) ? baseEntry[0] : "",
            (baseEntry != null && baseEntry.length > 1) ? baseEntry[1] : "",
            (baseEntry != null && baseEntry.length > 2) ? baseEntry[2] : "",
            (baseEntry != null && baseEntry.length > 3) ? baseEntry[3] : "",
            (baseEntry != null && baseEntry.length > 4) ? baseEntry[4] : "",
            (baseEntry != null && baseEntry.length > 5) ? baseEntry[5] : "",
            (studentEntry != null && studentEntry.length > 1 && !studentEntry[1].isEmpty())
                ? Integer.parseInt(studentEntry[1])
                : 0,
            (studentEntry != null && studentEntry.length > 2) ? studentEntry[2] : "",
            (studentEntry != null && studentEntry.length > 3 && !studentEntry[3].isEmpty())
                ? Integer.parseInt(studentEntry[3])
                : 0
        );
    }

    public int getAn() {
        // A doua cifra a variabilei grupa (ex: 312 -> 1, 322 -> 2)
        return (this.grupa / 10) % 10;
    }

    public int getNrMatriceal() {
        return nrMatriceal;
    }

    public String getSerie() {
        return serie;
    }

    public int getGrupa() {
        return grupa;
    }

    private String pseudoHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.substring(0, 12);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void incarcareAdeverinta(Scanner scanner) {
        Adeverinta adeverinta = new Adeverinta(
            new Date(),
            null,
            StareCerere.incarcataDeStudent,
            null,
            "",
            this
        );
        adeverinta.alegere_categorie(scanner);

        System.out.println(
            "Categorie selectata: " + adeverinta.getCategorieCerere()
        );

        // consumam newline ramas dupa nextInt() din alegere_categorie
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        String motiv = "";
        while (true) {
            System.out.print("Introduceti motivul cererii (max 100 caractere): ");
            if (scanner.hasNextLine()) {
                motiv = scanner.nextLine().trim();
                if (!motiv.isEmpty() && motiv.length() < 100) {
                    break;
                } else if (motiv.isEmpty()) {
                    System.out.println("Motivul nu poate fi gol!");
                } else {
                    System.out.println("Motivul este prea lung (maxim 100 caractere)!");
                }
            }
        }
        adeverinta.SetComentariu(motiv);

        if (adeverinta.complete(scanner)) {
            String isoDataTrimitere = DateTimeFormatter.ISO_INSTANT.format(
                adeverinta.getDataTrimitere().toInstant()
            );
            String hashInput =
                this.getNume() + "|" + this.getPrenume() + "|" + isoDataTrimitere;
            String hash = pseudoHash(hashInput);

            Path srcTmp = Path.of(adeverinta.getPath());
            Path destDir = Path.of("db", "adeverinte");
            Path destFile = destDir.resolve(hash + ".adeverinta");

            try {
                Files.createDirectories(destDir);
                Files.move(
                    srcTmp,
                    destFile,
                    StandardCopyOption.REPLACE_EXISTING
                );
            } catch (IOException e) {
                System.out.println(
                    "Eroare la mutarea fisierului din '" +
                        srcTmp +
                        "' in '" +
                        destFile +
                        "'."
                );
                e.printStackTrace();
                return;
            }

            adeverinta.setPath(destFile.toString());

            Path csvPath = Path.of("db", "adeverinte.csv");
            try {
                if (!Files.exists(csvPath)) {
                    Files.writeString(
                        csvPath,
                        "userId,dataTrimitere,dataFinalizare,stareCerere,categorieCerere,comentariu,path\n",
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE
                    );
                }

                String row =
                    CsvManager.csvEscape(this.getId()) +
                        "," +
                        CsvManager.csvEscape(isoDataTrimitere) +
                        "," +
                        CsvManager.csvEscape(
                            adeverinta.getDataFinalizare() == null
                                ? ""
                                : DateTimeFormatter.ISO_INSTANT.format(
                                adeverinta.getDataFinalizare().toInstant()
                            )
                        ) +
                        "," +
                        CsvManager.csvEscape(
                            adeverinta.getStareCerere() == null
                                ? ""
                                : adeverinta.getStareCerere().name()
                        ) +
                        "," +
                        CsvManager.csvEscape(
                            adeverinta.getCategorieCerere() == null
                                ? ""
                                : adeverinta.getCategorieCerere().name()
                        ) +
                        "," +
                        CsvManager.csvEscape(adeverinta.getComentariu()) +
                        "," +
                        CsvManager.csvEscape(adeverinta.getPath()) +
                        "," +
                        CsvManager.csvEscape(adeverinta.getMotivRespingere()) +
                        "\n";

                Files.writeString(
                    csvPath,
                    row,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
                );
            } catch (IOException e) {
                System.out.println("Eroare la scrierea in '" + csvPath + "'.");
                e.printStackTrace();
                return;
            }

            System.out.println(
                "Adeverinta salvata: " + destFile + " (hash: " + hash + ")"
            );
        }
    }

    public void incarcareAdeverinta() {
        incarcareAdeverinta(new Scanner(System.in));
    }

    public void meniuVizualizareAdeverinta(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Vizualizare adeverinta ---");
            System.out.println("1 - Descarcare adeverinta");
            System.out.println("0 - Inapoi");
            System.out.print("Optiune: ");
            int opt = -1;
            if (scanner.hasNextInt()) opt = scanner.nextInt();
            if (scanner.hasNextLine()) scanner.nextLine();

            if (opt == 1) {
                descarcareAdeverinta(scanner);
            } else if (opt == 0) {
                back = true;
            } else {
                System.out.println("Optiune invalida.");
            }
        }
    }

    public void descarcareAdeverinta(Scanner scanner) {
        List<Adeverinta> toate = Adeverinta.get_toate();
        List<Adeverinta> finalized = new ArrayList<>();
        for (Adeverinta a : toate) {
            if (a.getStudentEmitator() != null && 
                a.getStudentEmitator().getId().equals(this.getId()) &&
                a.getStareCerere() == StareCerere.finalizata) {
                finalized.add(a);
            }
        }

        if (finalized.isEmpty()) {
            System.out.println("Nu aveti nicio adeverinta finalizata pentru descarcare.");
            return;
        }

        System.out.println("--- Adeverinte disponibile pentru descarcare ---");
        for (int i = 0; i < finalized.size(); i++) {
            Adeverinta a = finalized.get(i);
            System.out.println(i + " - [" + a.getDataTrimitere() + "] " + a.getCategorieCerere());
        }

        System.out.print("Alegeti adeverinta (sau -1 pentru inapoi): ");
        int choice = -1;
        if (scanner.hasNextInt()) choice = scanner.nextInt();
        if (scanner.hasNextLine()) scanner.nextLine();

        if (choice >= 0 && choice < finalized.size()) {
            Adeverinta selected = finalized.get(choice);
            System.out.print("Introduceti numele fisierului de destinatie (ex: adeverinta.txt): ");
            String destName = scanner.nextLine();
            if (!destName.endsWith(".txt")) {
                destName += ".txt";
            }
            
            try {
                Files.copy(Path.of(selected.getPath()), Path.of(destName), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Adeverinta a fost descarcata cu succes sub numele: " + destName);
            } catch (IOException e) {
                System.out.println("Eroare la descarcare: " + e.getMessage());
            }
        }
    }

    public void vizualizareStatusAdeverinte(Scanner scanner) {
        List<Adeverinta> toate = Adeverinta.get_toate();
        List<Adeverinta> aleMele = new ArrayList<>();
        
        for (Adeverinta a : toate) {
            if (a.getStudentEmitator() != null && a.getStudentEmitator().getId().equals(this.getId())) {
                aleMele.add(a);
            }
        }

        if (aleMele.isEmpty()) {
            System.out.println("Nu aveti nicio cerere depusa.");
            return;
        }

        // Sortare invers cronologica (dupa dataTrimitere)
        aleMele.sort((a1, a2) -> a2.getDataTrimitere().compareTo(a1.getDataTrimitere()));

        System.out.println("--- Statusul cererilor dumneavoastra ---");
        for (int i = 0; i < aleMele.size(); i++) {
            Adeverinta a = aleMele.get(i);
            System.out.println(i + " - [" + a.getDataTrimitere() + "] " + a.getCategorieCerere() + " -> STARE: " + a.getStareCerere());
        }

        System.out.print("Alegeti o cerere pentru detalii (sau -1 pentru inapoi): ");
        int choice = -1;
        if (scanner.hasNextInt()) choice = scanner.nextInt();
        if (scanner.hasNextLine()) scanner.nextLine();

        if (choice >= 0 && choice < aleMele.size()) {
            aleMele.get(choice).vizualizareAdeverinta(this);
            System.out.println("Apasati Enter pentru a reveni la meniu...");
            if (scanner.hasNextLine()) scanner.nextLine();
        }
    }
}
