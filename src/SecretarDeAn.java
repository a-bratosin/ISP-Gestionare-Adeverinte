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
    public void valideazaCerere(){
        return; //TODO
    }

    public void respingereCerere(){
        return; //TODO
    }
}
