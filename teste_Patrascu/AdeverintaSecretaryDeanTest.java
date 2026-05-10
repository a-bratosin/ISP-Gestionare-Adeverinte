import org.junit.Test;
import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdeverintaSecretaryDeanTest {

    private CsvManager csv = new CsvManager();

    @Test
    public void deanApprovesAndSigns() throws Exception {
        String comment = "patrascu-approve-" + System.currentTimeMillis();

        Student student = (Student) Autentificare.getUserById("0");
        assertNotNull(student);

        // Provide comment first, then token values (date)
        String studentInput = "0\n" + comment + "\n2026-05-10\n";
        student.incarcareAdeverinta(new Scanner(studentInput));

        String[] row = csv.findRow("db/adeverinte.csv", "comentariu", comment);
        assertNotNull("CSV row for created adeverinta should exist", row);
        assertEquals("incarcataDeStudent", row[3]);

        // Secretary validates and sends to dean
        String[] secRow = csv.findRow("db/secretari.csv", "id", "1");
        assertNotNull(secRow);
        int secAn = Integer.parseInt(secRow[1]);

        Adeverinta[] nevalidate = Adeverinta.get_nevalidate();
        List<Adeverinta> deValidat = new ArrayList<>();
        for (Adeverinta a : nevalidate) {
            if (a.getStudentEmitator() != null && a.getStudentEmitator().getAn() == secAn) {
                deValidat.add(a);
            }
        }

        int idx = -1;
        for (int i = 0; i < deValidat.size(); i++) {
            if (comment.equals(deValidat.get(i).getComentariu())) {
                idx = i;
                break;
            }
        }
        assertTrue(idx >= 0);

        SecretarDeAn sec = (SecretarDeAn) Autentificare.getUserById("1");
        assertNotNull(sec);
        sec.valideazaCerere(new Scanner(idx + "\n" + "y\n"));

        String[] after = csv.findRow("db/adeverinte.csv", "comentariu", comment);
        assertNotNull(after);
        assertEquals("trimisaLaDecan", after[3]);

        // Dean signs
        Adeverinta[] nesemnate = Adeverinta.get_nesemnate();
        int idxDec = -1;
        for (int i = 0; i < nesemnate.length; i++) {
            if (comment.equals(nesemnate[i].getComentariu())) {
                idxDec = i;
                break;
            }
        }
        assertTrue(idxDec >= 0);

        Decan decan = (Decan) Autentificare.getUserById("3");
        assertNotNull(decan);

        String signaturePassword = "pwd123";
        decan.gestioneazaAdeverinte(new Scanner(idxDec + "\n" + "1\n" + signaturePassword + "\n"));

        String[] finalRow = csv.findRow("db/adeverinte.csv", "comentariu", comment);
        assertNotNull(finalRow);
        assertEquals("finalizata", finalRow[3]);

        String path = finalRow[6];
        String content = Files.readString(Path.of(path));
        assertTrue(content.contains("[SEMNATURA DIGITALA DECAN"));
    }

    @Test
    public void deanRefusesAndReturnsToStudent() throws Exception {
        String comment = "patrascu-refuse-" + System.currentTimeMillis();

        Student student = (Student) Autentificare.getUserById("0");
        assertNotNull(student);

        String studentInput = "0\n" + comment + "\n2026-05-10\n";
        student.incarcareAdeverinta(new Scanner(studentInput));

        String[] row = csv.findRow("db/adeverinte.csv", "comentariu", comment);
        assertNotNull(row);

        String[] secRow = csv.findRow("db/secretari.csv", "id", "1");
        assertNotNull(secRow);
        int secAn = Integer.parseInt(secRow[1]);

        Adeverinta[] nevalidate = Adeverinta.get_nevalidate();
        List<Adeverinta> deValidat = new ArrayList<>();
        for (Adeverinta a : nevalidate) {
            if (a.getStudentEmitator() != null && a.getStudentEmitator().getAn() == secAn) {
                deValidat.add(a);
            }
        }

        int idx = -1;
        for (int i = 0; i < deValidat.size(); i++) {
            if (comment.equals(deValidat.get(i).getComentariu())) {
                idx = i;
                break;
            }
        }
        assertTrue(idx >= 0);

        SecretarDeAn sec = (SecretarDeAn) Autentificare.getUserById("1");
        assertNotNull(sec);
        sec.valideazaCerere(new Scanner(idx + "\n" + "y\n"));

        Adeverinta[] nesemnate = Adeverinta.get_nesemnate();
        int idxDec = -1;
        for (int i = 0; i < nesemnate.length; i++) {
            if (comment.equals(nesemnate[i].getComentariu())) {
                idxDec = i;
                break;
            }
        }
        assertTrue(idxDec >= 0);

        Decan decan = (Decan) Autentificare.getUserById("3");
        assertNotNull(decan);

        String reason = "Lipsa stampila biblioteca";
        decan.gestioneazaAdeverinte(new Scanner(idxDec + "\n" + "2\n" + reason + "\n"));

        String[] after = csv.findRow("db/adeverinte.csv", "comentariu", comment);
        assertNotNull(after);
        assertEquals("incarcataDeStudent", after[3]);
        assertEquals(reason, after[7]);
    }

}
