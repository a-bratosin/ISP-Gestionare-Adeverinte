public class Utilizator {

    private String id;
    private String nume;
    private String prenume;
    private String telefon;
    private String email;
    private String parola;

    //TODO: pune ceva valori mai ok
    public Utilizator() {
        this.nume = "heheheha";
        this.prenume = "heheheha";
        this.telefon = "heheheha";
        this.email = "heheheha";
        this.parola = "heheheha";
    }

    public Utilizator(
        String id,
        String nume,
        String prenume,
        String telefon,
        String email,
        String parola
    ) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.telefon = telefon;
        this.email = email;
        this.parola = parola;
    }

    public Utilizator(String[] entry) {
        this(
            (entry != null && entry.length > 0) ? entry[0] : "",
            (entry != null && entry.length > 1) ? entry[1] : "",
            (entry != null && entry.length > 2) ? entry[2] : "",
            (entry != null && entry.length > 3) ? entry[3] : "",
            (entry != null && entry.length > 4) ? entry[4] : "",
            (entry != null && entry.length > 5) ? entry[5] : ""
        );
    }

    String getNume() {
        return nume;
    }

    String getId() {
        return id;
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

    void setId(String id) {
        this.id = id;
    }

    public void modificareDate() {
        // Implementation for modifying user data
    }

    public void resetareParola() {
        // Implementation for resetting password
    }
}
