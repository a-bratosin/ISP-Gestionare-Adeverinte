public abstract class Utilizator {

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

    public abstract void modificareDate();

    public abstract void resetareParola();
}
