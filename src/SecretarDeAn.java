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
    ){
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
            (secretarEntry != null && secretarEntry.length > 1 && !secretarEntry[1].isEmpty())
                ? Integer.parseInt(secretarEntry[1])
                : 0,
            (secretarEntry != null && secretarEntry.length > 2) ? secretarEntry[2] : "",
            (secretarEntry != null && secretarEntry.length > 3) ? secretarEntry[3] : ""
        );
    }
    public void valideazaCerere(){
        return; //TODO
    }

    public void respingereCerere(){
        return; //TODO
    }
}
