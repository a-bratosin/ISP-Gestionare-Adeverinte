import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Scanner;

public class Adeverinta {

    private Date dataTrimitere;
    private Date dataFinalizare;
    private StareCerere stareCerere;
    private CategoriiAdeverinte categorieCerere;
    private String comentariu;

    public Adeverinta(
        Date dataTrimitere,
        Date dataFinalizare,
        StareCerere stareCerere,
        CategoriiAdeverinte categorieCerere,
        String comentariu
    ) {
        this.dataTrimitere = dataTrimitere;
        this.dataFinalizare = dataFinalizare;
        this.stareCerere = stareCerere;
        this.categorieCerere = categorieCerere;
        this.comentariu = comentariu;
    }

    Date getDataTrimitere() {
        return dataTrimitere;
    }

    Date getDataFinalizare() {
        return dataFinalizare;
    }

    StareCerere getStareCerere() {
        return stareCerere;
    }

    CategoriiAdeverinte getCategorieCerere() {
        return categorieCerere;
    }

    String getComentariu() {
        return comentariu;
    }

    void SetDataTrimitere(Date dataTrimitere) {
        this.dataTrimitere = dataTrimitere;
    }

    void SetDataFinalizare(Date dataFinalizare) {
        this.dataFinalizare = dataFinalizare;
    }

    void SetStareCerere(StareCerere stareCerere) {
        this.stareCerere = stareCerere;
    }

    void SetCategorieCerere(CategoriiAdeverinte categorieCerere) {
        this.categorieCerere = categorieCerere;
    }

    void SetComentariu(String comentariu) {
        this.comentariu = comentariu;
    }

    public void compile() {}

    public void render() {}

    public void alegere_categorie() {
        alegere_categorie(new Scanner(System.in));
    }

    public void alegere_categorie(Scanner scanner) {
        CsvManager myCsvManager = new CsvManager();
        System.out.println("Categorii disponibile:");
        CategoriiAdeverinte[] valori = CategoriiAdeverinte.values();
        for (int i = 0; i < valori.length; i++) {
            System.out.println(i + " - " + valori[i]);
        }

        System.out.print(
            "Selecteaza categoria (0-" + (valori.length - 1) + "): "
        );
        int alegere = -1;
        while (true) {
            if (scanner.hasNextInt()) {
                alegere = scanner.nextInt();
                if (alegere >= 0 && alegere < valori.length) {
                    break;
                } else {
                    System.out.print(
                        "Valoare invalida. Reintrodu (0-" +
                            (valori.length - 1) +
                            "): "
                    );
                }
            } else {
                scanner.next(); // consume invalid token
                System.out.print("Va rugam introduceti un numar: ");
            }
        }

        this.categorieCerere = valori[alegere];

        String[] dateDeBaza = myCsvManager.findRow(
            "db/template.csv",
            "Tip",
            this.categorieCerere.name()
        );

        // dateDeBaza[1] e path la un fisier .txt
        // muta tot continutul de la .txt intr-un tmp.txt
        if (dateDeBaza == null || dateDeBaza.length < 2) {
            System.out.println(
                "Nu am gasit un template pentru categoria selectata: " +
                    this.categorieCerere.name()
            );
            return;
        }

        Path templatePath = Path.of(dateDeBaza[1].trim());
        Path tmpPath = Path.of("tmp.txt");

        try {
            Files.copy(
                templatePath,
                tmpPath,
                StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            System.out.println(
                "Eroare la copierea template-ului din '" +
                    templatePath +
                    "' in '" +
                    tmpPath +
                    "'."
            );
            e.printStackTrace();
        }
    }
}
