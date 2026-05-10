import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SecretarDeAn extends Utilizator {

    private int an;
    private String programDeLucru;
    private String programPublic;

    public SecretarDeAn(
        String id,
        String nume,
        String prenume,
        String telefon,
        String email,
        String parola,
        int an,
        String programDeLucru,
        String programPublic
    ) {
        super(id, nume, prenume, telefon, email, parola);
        this.an = an;
        this.programDeLucru = programDeLucru;
        this.programPublic = programPublic;
    }

    public SecretarDeAn(String[] baseEntry, String[] secretarEntry) {
        this(
            (baseEntry != null && baseEntry.length > 0) ? baseEntry[0] : "",
            (baseEntry != null && baseEntry.length > 1) ? baseEntry[1] : "",
            (baseEntry != null && baseEntry.length > 2) ? baseEntry[2] : "",
            (baseEntry != null && baseEntry.length > 3) ? baseEntry[3] : "",
            (baseEntry != null && baseEntry.length > 4) ? baseEntry[4] : "",
            (baseEntry != null && baseEntry.length > 5) ? baseEntry[5] : "",
            (secretarEntry != null &&
                secretarEntry.length > 1 &&
                !secretarEntry[1].isEmpty())
                ? Integer.parseInt(secretarEntry[1])
                : 0,
            (secretarEntry != null && secretarEntry.length > 2)
                ? secretarEntry[2]
                : "",
            (secretarEntry != null && secretarEntry.length > 3)
                ? secretarEntry[3]
                : ""
        );
    }

    public void valideazaCerere() {
        valideazaCerere(new Scanner(System.in));
    }

    public void valideazaCerere(Scanner scanner) {
        Adeverinta[] nevalidate = Adeverinta.get_nevalidate();
        List<Adeverinta> deValidat = new ArrayList<>();

        System.out.println(
            "Cererile de adeverinta pentru anul " + this.an + ":"
        );
        for (Adeverinta a : nevalidate) {
            if (
                a.getStudentEmitator() != null &&
                a.getStudentEmitator().getAn() == this.an
            ) {
                deValidat.add(a);
            }
        }

        if (deValidat.isEmpty()) {
            System.out.println("Nu exista cereri noi.");
            return;
        }

        for (int i = 0; i < deValidat.size(); i++) {
            Adeverinta a = deValidat.get(i);
            System.out.println(
                i +
                    " - " +
                    a.getStudentEmitator().getNume() +
                    " " +
                    a.getStudentEmitator().getPrenume()
            );
        }

        System.out.print("Selectati adeverinta (sau -1 pentru iesire): ");
        if (!scanner.hasNextInt()) {
            scanner.next();
            return;
        }
        int choice = scanner.nextInt();
        if (scanner.hasNextLine()) scanner.nextLine(); // consume newline

        if (choice >= 0 && choice < deValidat.size()) {
            Adeverinta selectata = deValidat.get(choice);
<<<<<<< HEAD
            selectata.vizualizareAdeverinta();

            System.out.print("Validati aceasta adeverinta? (y/N): ");
=======
            selectata.vizualizareAdeverinta(this);
            
            System.out.print("Validati aceasta adeverinta? (y/n): ");
>>>>>>> da1d3ea540b403c1254653efa3900d5185a98773
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("y")) {
                selectata.SetStareCerere(StareCerere.trimisaLaDecan);
                selectata.SetSecretarValidator(this);
                selectata.updateInCsv();
                System.out.println("Adeverinta a fost validata.");
            } else {
                System.out.println("Adeverinta nu a fost validata.");
            }
        }
    }

    public void respingereCerere() {
        return; //TODO
    }
}
