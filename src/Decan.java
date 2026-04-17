import java.awt.Image;

public class Decan extends Utilizator {
    private Image semnatura;
    private String facultate;
    private String idMandat;

        public Decan(
            String nume,
            String prenume,
            String telefon,
            String email,
            String parola,
            Image semnatura,
            String facultate,
            String idMandat
        ) {
            super(nume, prenume, telefon, email, parola);
            this.semnatura = semnatura;
            this.facultate = facultate;
            this.idMandat = idMandat;
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
