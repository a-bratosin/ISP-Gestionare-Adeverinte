import java.util.Date;

public abstract class Adeverinta {
    private Date dataTrimitere;
    private Date dataFinalizare;
    private StareCerere stareCerere;
    private CategoriiAdeverinte categorieCerere;
    private String comentariu;

    public abstract void compile();

    public abstract void render();
}
