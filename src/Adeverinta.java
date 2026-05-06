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
        CsvManager myCsvManager = new CsvManager();
        System.out.println("Categorii disponibile:");
        CategoriiAdeverinte[] valori = CategoriiAdeverinte.values();
        for (int i = 0; i < valori.length; i++) {
            System.out.println(i + " - " + valori[i]);
        }

        System.out.print("Selecteaza categoria (0-" + (valori.length - 1) + "): ");
        Scanner scanner = new Scanner(System.in);
        int alegere = -1;
        while (true) {
            if (scanner.hasNextInt()) {
                alegere = scanner.nextInt();
                if (alegere >= 0 && alegere < valori.length) {
                    break;
                } else {
                    System.out.print("Valoare invalida. Reintrodu (0-" + (valori.length - 1) + "): ");
                }
            } else {
                scanner.next(); // consume invalid token
                System.out.print("Va rugam introduceti un numar: ");
            }
        }

        scanner.close();
        this.categorieCerere = valori[alegere];
    }
}
