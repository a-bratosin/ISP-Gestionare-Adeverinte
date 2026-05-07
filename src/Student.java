import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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

    public  Adeverinta descarcareAdeverinta(){
        return null;
    }
}
