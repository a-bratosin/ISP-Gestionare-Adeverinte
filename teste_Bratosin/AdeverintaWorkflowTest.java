import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdeverintaWorkflowTest {

    private CsvManager csv = new CsvManager();

    @Test
    public void confirmWorkflow() throws Exception {
        String comment = "bratosin-confirm-" + System.currentTimeMillis();

        Student student = (Student) Autentificare.getUserById("0");
        assertNotNull(student);

        // Create adeverinta via Student flow (choose category 0)
        // Provide comment first, then token values (date)
        String studentInput = "0\n" + comment + "\n2026-05-10\n";
        student.incarcareAdeverinta(new Scanner(studentInput));

        // Verify row created in CSV
        String[] row = csv.findRow("db/adeverinte.csv", "comentariu", comment);
        assertNotNull("CSV row for created adeverinta should exist", row);
        assertEquals("incarcataDeStudent", row[3]);
        assertEquals("adeverintaStudent", row[4]);

        // Secretary validates it (send to dean)
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

        // Check it moved to dean
        String[] after = csv.findRow("db/adeverinte.csv", "comentariu", comment);
        assertNotNull(after);
        assertEquals("trimisaLaDecan", after[3]);

        // Decan should see it in nesemnate
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

        // Choose action 1 = sign (provide any password)
        decan.gestioneazaAdeverinte(new Scanner(idxDec + "\n" + "1\n" + "pwd\n"));

        String[] finalRow = csv.findRow("db/adeverinte.csv", "comentariu", comment);
        assertNotNull(finalRow);
        assertEquals("finalizata", finalRow[3]);
    }

    @Test
    public void refuseWorkflow() throws Exception {
        String comment = "bratosin-refuse-" + System.currentTimeMillis();

        Student student = (Student) Autentificare.getUserById("0");
        assertNotNull(student);

        String studentInput = "0\n" + comment + "\n2026-05-10\n";
        student.incarcareAdeverinta(new Scanner(studentInput));

        String[] row = csv.findRow("db/adeverinte.csv", "comentariu", comment);
        assertNotNull(row);

        // Secretary validates (send to dean)
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

        // Now decan will refuse (action 2)
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
        // Choose action 2 = send back to secretary (provide an empty line for optional reason)
        decan.gestioneazaAdeverinte(new Scanner(idxDec + "\n" + "2\n" + "\n"));

        String[] after = csv.findRow("db/adeverinte.csv", "comentariu", comment);
        assertNotNull(after);
        assertEquals("incarcataDeStudent", after[3]);
    }
}
