import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Decan extends Utilizator {
    private Image semnatura;
    private String facultate;
    private String idMandat;

    public Decan(
        String id,
        String nume,
        String prenume,
        String telefon,
        String email,
        String parola,
        Image semnatura,
        String facultate,
        String idMandat
    ) {
        super(id, nume, prenume, telefon, email, parola);
        this.semnatura = semnatura;
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
            null, // semnatura temporary
            (decanEntry != null && decanEntry.length > 2) ? decanEntry[2] : "",
            (decanEntry != null && decanEntry.length > 3) ? decanEntry[3] : ""
        );

        if (decanEntry != null && decanEntry.length > 1 && !decanEntry[1].isEmpty()) {
            try {
                this.semnatura = ImageIO.read(new File(decanEntry[1]));
            } catch (IOException e) {
                System.err.println("Eroare la incarcarea semnaturii decanului: " + decanEntry[1]);
            }
        }
    }

    void setSemnatura(Image semnatura) {
        this.semnatura = semnatura;
    }
    void setFacultate(String facultate) {
        this.facultate = facultate;
    }
    void setIdMandat(String idMandat) {
        this.idMandat = idMandat;
    }

    Image getSemnatura() {
        return semnatura;
    }
    String getFacultate() {
        return facultate;
    }
    String getIdMandat() {
        return idMandat;
    }

    public void semneazaAdeverinta() {
        // Implementation for signing certificate
    }

    public void anuleazaAdeverinta() {
        // Implementation for canceling certificate
    }
}
