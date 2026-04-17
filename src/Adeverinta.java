import java.util.Date;

public class Adeverinta {

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

    Date getDataTrimitere() {
        return dataTrimitere;
    }

    Date getDataFinalizare() {
        return dataFinalizare;
    }

    StareCerere getStareCerere() {
        return stareCerere;
    }

    CategoriiAdeverinte getCategorieCerere() {
        return categorieCerere;
    }

    String getComentariu() {
        return comentariu;
    }

    void SetDataTrimitere(Date dataTrimitere) {
        this.dataTrimitere = dataTrimitere;
    }

    void SetDataFinalizare(Date dataFinalizare) {
        this.dataFinalizare = dataFinalizare;
    }

    void SetStareCerere(StareCerere stareCerere) {
        this.stareCerere = stareCerere;
    }

    void SetCategorieCerere(CategoriiAdeverinte categorieCerere) {
        this.categorieCerere = categorieCerere;
    }

    void SetComentariu(String comentariu) {
        this.comentariu = comentariu;
    }

    public void compile(){};

    public void render(){};
}
