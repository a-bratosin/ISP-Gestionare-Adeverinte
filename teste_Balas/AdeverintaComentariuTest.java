import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;

public class AdeverintaComentariuTest {
    @Test
    public void nullComentariuIsInvalid() {
        assertFalse(Adeverinta.validateComentariu(null));
    }

    @Test
    public void emptyComentariuIsInvalid() {
        assertFalse(Adeverinta.validateComentariu(""));
    }

    @Test
    public void tooLongComentariuIsInvalid() {
        char[] c = new char[100];
        Arrays.fill(c, 'a');
        String s = new String(c);
        assertFalse(Adeverinta.validateComentariu(s));
    }

    @Test
    public void maxAllowedComentariuValid() {
        char[] c = new char[99];
        Arrays.fill(c, 'a');
        String s = new String(c);
        assertTrue(Adeverinta.validateComentariu(s));
    }

    @Test
    public void instanceMethodWorks() {
        Adeverinta a = new Adeverinta(new java.util.Date(), null, StareCerere.incarcataDeStudent, null, "", null);
        assertFalse(a.isComentariuValid());
        a.SetComentariu("Motiv valid");
        assertTrue(Adeverinta.validateComentariu(a.getComentariu()));
        assertTrue(a.isComentariuValid());
    }
}
