public class Student extends Utilizator {

    private int nrMatriceal;
    private String serie;
    private int grupa;

    public Student(
        String id,
        String nume,
        String prenume,
        String telefon,
        String email,
        String parola,
        int nrMatriceal,
        String serie,
        int grupa
    ) {
        super(id, nume, prenume, telefon, email, parola);
        this.nrMatriceal = nrMatriceal;
        this.serie = serie;
        this.grupa = grupa;
    }

    public  void incarcareAdeverinta(){
        return;
    }

    public  Adeverinta descarcareAdeverinta(){
        return null;
    }
}
