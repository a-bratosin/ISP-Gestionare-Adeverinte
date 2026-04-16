import java.awt.Image;

public abstract class Decan extends Utilizator {
    private Image semnatura;
    private String facultate;
    private String idMandat;

    public abstract void semneazaAdeverinta();

    public abstract void anuleazaAdeverinta();
}
