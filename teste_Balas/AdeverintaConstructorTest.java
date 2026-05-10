import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;
import java.util.Scanner;

public class AdeverintaConstructorTest {

    @Test
    public void testStringArrayConstructorParsesFields() throws Exception {
        String[] entry = new String[7];
        entry[0] = "user1";
        entry[1] = "2021-01-01T00:00:00Z";
        entry[2] = "";
        entry[3] = "incarcataDeStudent";
        entry[4] = "adeverintaStudent";
        entry[5] = "coment";
        entry[6] = "/path/to/file";

        Adeverinta a = new Adeverinta(entry, null, null, null);
        assertNotNull(a.getDataTrimitere());
        assertEquals(Instant.parse(entry[1]), a.getDataTrimitere().toInstant());
        assertEquals(StareCerere.incarcataDeStudent, a.getStareCerere());
        assertEquals(CategoriiAdeverinte.adeverintaStudent, a.getCategorieCerere());
        assertEquals("coment", a.getComentariu());
        assertEquals("/path/to/file", a.getPath());
    }

    @Test
    public void testCompleteReadsTokensAndComment() throws Exception {
        Path tmp = Files.createTempFile("adev", ".txt");
        Files.writeString(tmp, "Salut ::nume::\nData ::data::\n");
        Adeverinta a = new Adeverinta(new Date(), null, StareCerere.incarcataDeStudent, null, "", null);
        a.setPath(tmp.toString());

        String input = "Ion\n2026-05-10\nMotivatia mea\n";
        Scanner scanner = new Scanner(input);
        boolean ok = a.complete(scanner);
        assertTrue(ok);
        assertEquals("Motivatia mea", a.getComentariu());
        String content = Files.readString(tmp);
        assertTrue(content.contains("Ion"));
        assertTrue(content.contains("2026-05-10"));
    }

}
