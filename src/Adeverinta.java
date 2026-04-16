import java.util.Date;

public abstract class Adeverinta {

    private Date dataTrimitere;
    private Date dataFinalizare;
    private StareCerere stareCerere;
    private CategoriiAdeverinte categorieCerere;
    private String comentariu;

    public Adeverinta(
        Date dataTrimitere,
        Date dataFinalizare,
        StareCerere stareCerere,
        CategoriiAdeverinte categorieCerere,
        String comentariu
    ) {
        this.dataTrimitere = dataTrimitere;
        this.dataFinalizare = dataFinalizare;
        this.stareCerere = stareCerere;
        this.categorieCerere = categorieCerere;
        this.comentariu = comentariu;
    }

    public abstract void compile();

    public abstract void render();
}
