import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Adeverinta {

    private String path;
    private Date dataTrimitere;
    private Date dataFinalizare;
    private StareCerere stareCerere;
    private CategoriiAdeverinte categorieCerere;
    private String comentariu;
    private Student studentEmitator;
    private SecretarDeAn secretarValidator;
    private Decan decanSemnatar;

    // Cazul 1: Cererea a fost doar trimisă de student
    public Adeverinta(
        Date dataTrimitere,
        Date dataFinalizare,
        StareCerere stareCerere,
        CategoriiAdeverinte categorieCerere,
        String comentariu,
        Student studentEmitator
    ) {
        this(
            dataTrimitere,
            dataFinalizare,
            stareCerere,
            categorieCerere,
            comentariu,
            studentEmitator,
            null,
            null
        );
    }

    // Cazul 2: Cererea a fost validată de secretar
    public Adeverinta(
        Date dataTrimitere,
        Date dataFinalizare,
        StareCerere stareCerere,
        CategoriiAdeverinte categorieCerere,
        String comentariu,
        Student studentEmitator,
        SecretarDeAn secretarValidator
    ) {
        this(
            dataTrimitere,
            dataFinalizare,
            stareCerere,
            categorieCerere,
            comentariu,
            studentEmitator,
            secretarValidator,
            null
        );
    }

    // Cazul 3: Cererea a fost semnată de decan
    public Adeverinta(
        Date dataTrimitere,
        Date dataFinalizare,
        StareCerere stareCerere,
        CategoriiAdeverinte categorieCerere,
        String comentariu,
        Student studentEmitator,
        SecretarDeAn secretarValidator,
        Decan decanSemnatar
    ) {
        this.path = "tmp.txt";
        this.dataTrimitere = dataTrimitere;
        this.dataFinalizare = dataFinalizare;
        this.stareCerere = stareCerere;
        this.categorieCerere = categorieCerere;
        this.comentariu = comentariu;
        this.studentEmitator = studentEmitator;
        this.secretarValidator = secretarValidator;
        this.decanSemnatar = decanSemnatar;
    }

    public Adeverinta(
        String[] entry,
        Student studentEmitator,
        SecretarDeAn secretarValidator,
        Decan decanSemnatar
    ) {
        this(
            (entry != null && entry.length > 1 && entry[1] != null && !entry[1].isEmpty())
                ? Date.from(Instant.parse(entry[1]))
                : null,
            (entry != null && entry.length > 2 && entry[2] != null && !entry[2].isEmpty())
                ? Date.from(Instant.parse(entry[2]))
                : null,
            (entry != null && entry.length > 3 && entry[3] != null && !entry[3].isEmpty())
                ? StareCerere.valueOf(entry[3])
                : null,
            (entry != null && entry.length > 4 && entry[4] != null && !entry[4].isEmpty())
                ? CategoriiAdeverinte.valueOf(entry[4])
                : null,
            (entry != null && entry.length > 5 && entry[5] != null) ? entry[5] : "",
            studentEmitator,
            secretarValidator,
            decanSemnatar
        );
        if (entry != null && entry.length > 6 && entry[6] != null) {
            this.path = entry[6];
        }
    }

    public static Adeverinta[] get_nevalidate() {
        List<Adeverinta> toate = get_toate();
        List<Adeverinta> nevalidate = new ArrayList<>();
        for (Adeverinta a : toate) {
            if (a.getStareCerere() == StareCerere.incarcataDeStudent) {
                nevalidate.add(a);
            }
        }
        return nevalidate.toArray(new Adeverinta[0]);
    }

    public static List<Adeverinta> get_toate() {
        List<Adeverinta> lista = new ArrayList<>();
        Path csvPath = Path.of("db", "adeverinte.csv");
        if (!Files.exists(csvPath)) {
            return lista;
        }

        try {
            List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
            if (lines.isEmpty()) return lista;
            
            // Skip header
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) continue;
                
                String[] entry = CsvManager.parseCsvLine(line, ','); 
                Student student = (Student) Autentificare.getUserById(entry[0]);
                lista.add(new Adeverinta(entry, student, null, null));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void updateInCsv() {
        Path csvPath = Path.of("db", "adeverinte.csv");
        try {
            List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
            List<String> newLines = new ArrayList<>();
            if (lines.isEmpty()) return;

            newLines.add(lines.get(0)); // header
            
            String isoDataTrimitere = DateTimeFormatter.ISO_INSTANT.format(this.dataTrimitere.toInstant());
            String currentUserId = studentEmitator.getId();

            boolean found = false;
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] entry = CsvManager.parseCsvLine(line, ',');
                if (entry[0].equals(currentUserId) && entry[1].equals(isoDataTrimitere)) {
                    newLines.add(toCsvRow());
                    found = true;
                } else {
                    newLines.add(line);
                }
            }
            
            if (!found) {
                newLines.add(toCsvRow());
            }

            Files.write(csvPath, newLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toCsvRow() {
        return CsvManager.csvEscape(studentEmitator.getId()) + "," +
               CsvManager.csvEscape(DateTimeFormatter.ISO_INSTANT.format(dataTrimitere.toInstant())) + "," +
               CsvManager.csvEscape(dataFinalizare == null ? "" : DateTimeFormatter.ISO_INSTANT.format(dataFinalizare.toInstant())) + "," +
               CsvManager.csvEscape(stareCerere == null ? "" : stareCerere.name()) + "," +
               CsvManager.csvEscape(categorieCerere == null ? "" : categorieCerere.name()) + "," +
               CsvManager.csvEscape(comentariu) + "," +
               CsvManager.csvEscape(path);
    }

    public void vizualizareAdeverinta() {
        System.out.println("------- VIZUALIZARE ADEVERINTA -------");
        System.out.println("Student: " + studentEmitator.getNume() + " " + studentEmitator.getPrenume());
        System.out.println("Data Trimitere: " + dataTrimitere);
        System.out.println("Categorie: " + categorieCerere);
        System.out.println("Stare: " + stareCerere);
        System.out.println("Continut:");
        try {
            String content = Files.readString(Path.of(this.path), StandardCharsets.UTF_8);
            System.out.println(content);
        } catch (IOException e) {
            System.out.println("[Eroare la citirea continutului]");
        }
        System.out.println("--------------------------------------");
    }

    public static Adeverinta[] get_nesemnate() {
        List<Adeverinta> toate = get_toate();
        List<Adeverinta> nesemnate = new ArrayList<>();
        for (Adeverinta a : toate) {
            if (a.getStareCerere() == StareCerere.trimisaLaDecan) {
                nesemnate.add(a);
            }
        }
        return nesemnate.toArray(new Adeverinta[0]);
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

    String getPath() {
        return path;
    }

    public Student getStudentEmitator() {
        return studentEmitator;
    }

    public SecretarDeAn getSecretarValidator() {
        return secretarValidator;
    }

    public Decan getDecanSemnatar() {
        return decanSemnatar;
    }

    public void SetSecretarValidator(SecretarDeAn secretarValidator) {
        this.secretarValidator = secretarValidator;
    }

    void setPath(String path) {
        this.path = path;
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

    public boolean complete() {
        return complete(new Scanner(System.in));
    }

    public boolean complete(Scanner scanner) {
        Path tmpPath = Path.of(this.path);
        if (!Files.exists(tmpPath)) {
            System.out.println(
                "Nu exista '" +
                    this.path +
                    "'. Ruleaza intai alegere_categorie()."
            );
            return false;
        }

        String continut;
        try {
            continut = Files.readString(tmpPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println(
                "Eroare la citirea fisierului '" + this.path + "'."
            );
            e.printStackTrace();
            return false;
        }

        Pattern myPattern = Pattern.compile("::[A-Za-z0-9_]+::");
        Map<String, String> valori = new HashMap<>();

        Matcher matcher = myPattern.matcher(continut);
        while (matcher.find()) {
            String token = matcher.group(); // ::label::
            String label = token.substring(2, token.length() - 2);

            String value = valori.get(label);
            if (value == null) {
                System.out.print(label + " ce valoare sa aiba? ");
                if (!scanner.hasNextLine()) {
                    System.out.println("Nu s-a primit valoare. Oprire.");
                    return false;
                }
                value = scanner.nextLine();
                valori.put(label, value);
            }

            continut = continut.replace(token, value);
            matcher = myPattern.matcher(continut);
        }

        try {
            Files.writeString(tmpPath, continut, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Eroare la scrierea in '" + this.path + "'.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

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

        // dateDeBaza[1] e path la un fisier template
        // muta tot continutul template-ului in fisierul temporar (this.path)
        if (dateDeBaza == null || dateDeBaza.length < 2) {
            System.out.println(
                "Nu am gasit un template pentru categoria selectata: " +
                    this.categorieCerere.name()
            );
            return;
        }

        Path templatePath = Path.of(dateDeBaza[1].trim());
        Path tmpPath = Path.of(this.path);

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
