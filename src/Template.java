import java.util.Date;
import org.w3c.dom.Document;

public abstract class Template {
    private String adresaFisier;
    private String adresaJson;
    private Date dataModificare;

    public abstract void actualizareFisiere(String nouaAdresaFisier, String nouaAdresaJson);

    public abstract Document genereazaPreview(Json dateTest);
}
