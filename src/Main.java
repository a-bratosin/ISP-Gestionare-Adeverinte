import java.util.Date;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class Main {

    private static String pseudoHash(String input) {
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

    private static String csvEscape(String value) {
        if (value == null) {
            return "";
        }
        boolean needsQuotes =
            value.contains(",") ||
            value.contains("\"") ||
            value.contains("\n") ||
            value.contains("\r");
        if (!needsQuotes) {
            return value;
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

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

        // consumam newline ramas dupa nextInt() din alegere_categorie
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        if (adeverinta.complete(scanner)) {
            String isoDataTrimitere = DateTimeFormatter.ISO_INSTANT.format(
                adeverinta.getDataTrimitere().toInstant()
            );
            String hashInput =
                user.getNume() + "|" + user.getPrenume() + "|" + isoDataTrimitere;
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
                    csvEscape(user.getId()) +
                    "," +
                    csvEscape(isoDataTrimitere) +
                    "," +
                    csvEscape(
                        adeverinta.getDataFinalizare() == null
                            ? ""
                            : DateTimeFormatter.ISO_INSTANT.format(
                                adeverinta.getDataFinalizare().toInstant()
                            )
                    ) +
                    "," +
                    csvEscape(
                        adeverinta.getStareCerere() == null
                            ? ""
                            : adeverinta.getStareCerere().name()
                    ) +
                    "," +
                    csvEscape(
                        adeverinta.getCategorieCerere() == null
                            ? ""
                            : adeverinta.getCategorieCerere().name()
                    ) +
                    "," +
                    csvEscape(adeverinta.getComentariu()) +
                    "," +
                    csvEscape(adeverinta.getPath()) +
                    "\n";

                Files.writeString(
                    csvPath,
                    row,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
                );
            } catch (IOException e) {
                System.out.println(
                    "Eroare la scrierea in '" + csvPath + "'."
                );
                e.printStackTrace();
                return;
            }

            System.out.println(
                "Adeverinta salvata: " + destFile + " (hash: " + hash + ")"
            );
        }
    }
}
