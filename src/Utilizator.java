public class Utilizator {

    private String nume;
    private String prenume;
    private String telefon;
    private String email;
    private String parola;

    public Utilizator(
        String nume,
        String prenume,
        String telefon,
        String email,
        String parola
    ) {
        this.nume = nume;
        this.prenume = prenume;
        this.telefon = telefon;
        this.email = email;
        this.parola = parola;
    }

    String getNume() {
        return nume;
    }

    String getPrenume() {
        return prenume;
    }
    String getTelefon() {
        return telefon;
    }
    String getEmail() {
        return email;
    }
    String getParola() {
        return parola;
    }

    void setNume(String nume) {
        this.nume = nume;
    }
    void setPrenume(String prenume) {
        this.prenume = prenume;
    }
    void setTelefon(String telefon) {
        this.telefon = telefon;
    }
    void setEmail(String email) {
        this.email = email;
    }
    void setParola(String parola) {
        this.parola = parola;
    }
    
    public void modificareDate() {
        // Implementation for modifying user data
    }

    public void resetareParola() {
        // Implementation for resetting password
    }
}
