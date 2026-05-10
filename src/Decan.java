import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Decan extends Utilizator {

    private String facultate;
    private String idMandat;

    public Decan(
        String id,
        String nume,
        String prenume,
        String telefon,
        String email,
        String parola,
        String facultate,
        String idMandat
    ) {
        super(id, nume, prenume, telefon, email, parola);
        this.facultate = facultate;
        this.idMandat = idMandat;
    }

    public Decan(String[] baseEntry, String[] decanEntry) {
        this(
            (baseEntry != null && baseEntry.length > 0) ? baseEntry[0] : "",
            (baseEntry != null && baseEntry.length > 1) ? baseEntry[1] : "",
            (baseEntry != null && baseEntry.length > 2) ? baseEntry[2] : "",
            (baseEntry != null && baseEntry.length > 3) ? baseEntry[3] : "",
            (baseEntry != null && baseEntry.length > 4) ? baseEntry[4] : "",
            (baseEntry != null && baseEntry.length > 5) ? baseEntry[5] : "",
            (decanEntry != null && decanEntry.length > 2) ? decanEntry[2] : "",
            (decanEntry != null && decanEntry.length > 3) ? decanEntry[3] : ""
        );
    }

    void setFacultate(String facultate) {
        this.facultate = facultate;
    }

    void setIdMandat(String idMandat) {
        this.idMandat = idMandat;
    }

    String getFacultate() {
        return facultate;
    }

    String getIdMandat() {
        return idMandat;
    }

    public void gestioneazaAdeverinte(Scanner scanner) {
        Adeverinta[] nesemnate = Adeverinta.get_nesemnate();
        if (nesemnate.length == 0) {
            System.out.println("Nu exista adeverinte de gestionat.");
            return;
        }

        System.out.println("--- Adeverinte trimise la decan ---");
        for (int i = 0; i < nesemnate.length; i++) {
            System.out.println(
                i +
                    " - Student: " +
                    nesemnate[i].getStudentEmitator().getNume() +
                    " " +
                    nesemnate[i].getStudentEmitator().getPrenume() +
                    " (" +
                    nesemnate[i].getCategorieCerere() +
                    ")"
            );
        }

        System.out.print("Alegeti adeverinta (sau -1 pt renuntare): ");
        int choice = -1;
        if (scanner.hasNextInt()) choice = scanner.nextInt();
        if (scanner.hasNextLine()) scanner.nextLine();

        if (choice < 0 || choice >= nesemnate.length) return;

        Adeverinta sel = nesemnate[choice];
        sel.vizualizareAdeverinta(this);

        System.out.println("Actiuni disponibile:");
        System.out.println("1 - Semneaza (Accepta)");
        System.out.println("2 - Trimite inapoi la secretar (Respinge)");
        System.out.print("Alegere: ");

        int actiune = 0;
        if (scanner.hasNextInt()) actiune = scanner.nextInt();
        if (scanner.hasNextLine()) scanner.nextLine();

        if (actiune == 1) {
            System.out.print("Introduceti parola pentru semnare: ");
            String pwd = scanner.nextLine();

            try {
                String content = Files.readString(
                    Path.of(sel.getPath()),
                    StandardCharsets.UTF_8
                );
                String signature = generateSignature(content, pwd);

                String signedContent =
                    content +
                    "\n\n[SEMNATURA DIGITALA DECAN: " +
                    signature +
                    "]\n[DECAN: " +
                    this.getNume() +
                    " " +
                    this.getPrenume() +
                    "]\n";
                Files.writeString(
                    Path.of(sel.getPath()),
                    signedContent,
                    StandardCharsets.UTF_8
                );

                sel.SetStareCerere(StareCerere.finalizata);
                sel.updateInCsv();
                System.out.println("Adeverinta a fost semnata cu succes!");
            } catch (Exception e) {
                System.err.println(
                    "Eroare la semnarea adeverintei: " + e.getMessage()
                );
            }
        } else if (actiune == 2) {
            System.out.print("Introduceti motivul respingerii (optional, apasati Enter pt a lasa gol): ");
            String motiv = scanner.nextLine().trim();
            sel.setMotivRespingere(motiv);
            
            sel.SetStareCerere(StareCerere.incarcataDeStudent); // Trimitere inapoi la secretar
            sel.updateInCsv();
            System.out.println(
                "Adeverinta a fost retrimisa catre secretar pentru reverificare."
            );
        } else {
            System.out.println("Actiune anulata.");
        }
    }

    private String generateSignature(String data, String key)
        throws NoSuchAlgorithmException, InvalidKeyException {
        // Simple HMAC approach with password as key
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(
            key.getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
        );
        sha256_HMAC.init(secret_key);

        return Base64.getEncoder().encodeToString(
            sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8))
        );
    }

    // Removed separate methods to keep it clean as they are now merged
}
