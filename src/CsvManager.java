import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvManager {

    /**
     * Caută primul rând dintr-un fișier CSV unde valoarea din coloana dată
     * (identificată după numele din header) este egală cu {@code value}.
     *
     * @param csvFile     cale (relativă sau absolută) către fișierul CSV
     * @param columnName  numele coloanei din header (prima linie)
     * @param value       valoarea căutată (comparare exactă)
     * @return rândul găsit sub formă de {@code String[]} sau {@code null} dacă nu există
     */
    public String[] findRow(String csvFile, String columnName, String value) {
        if (csvFile == null || columnName == null || value == null) {
            return null;
        }

        Path path = Path.of(csvFile);
        try (
            BufferedReader reader = Files.newBufferedReader(
                path,
                StandardCharsets.UTF_8
            )
        ) {
            String headerLine = readNextNonEmptyLine(reader);
            if (headerLine == null) {
                return null;
            }

            char delimiter = detectDelimiter(headerLine);
            String[] headers = parseCsvLine(headerLine, delimiter);
            int columnIndex = indexOfHeader(headers, columnName);
            if (columnIndex < 0) {
                return null;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] row = parseCsvLine(line, delimiter);
                if (
                    columnIndex < row.length && value.equals(row[columnIndex])
                ) {
                    return row;
                }
            }
            return null;
        } catch (NoSuchFileException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Caută toate rândurile dintr-un fișier CSV unde valoarea din coloana dată
     * (identificată după numele din header) este egală cu {@code value}.
     *
     * @param csvFile     cale (relativă sau absolută) către fișierul CSV
     * @param columnName  numele coloanei din header (prima linie)
     * @param value       valoarea căutată (comparare exactă)
     * @return o listă cu toate rândurile găsite sub formă de {@code String[]}
     */
    public List<String[]> findAllRows(
        String csvFile,
        String columnName,
        String value
    ) {
        List<String[]> result = new ArrayList<>();
        if (csvFile == null || columnName == null || value == null) {
            return result;
        }

        Path path = Path.of(csvFile);
        try (
            BufferedReader reader = Files.newBufferedReader(
                path,
                StandardCharsets.UTF_8
            )
        ) {
            String headerLine = readNextNonEmptyLine(reader);
            if (headerLine == null) {
                return result;
            }

            char delimiter = detectDelimiter(headerLine);
            String[] headers = parseCsvLine(headerLine, delimiter);
            int columnIndex = indexOfHeader(headers, columnName);
            if (columnIndex < 0) {
                return result;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] row = parseCsvLine(line, delimiter);
                if (
                    columnIndex < row.length && value.equals(row[columnIndex])
                ) {
                    result.add(row);
                }
            }
        } catch (NoSuchFileException e) {
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String readNextNonEmptyLine(BufferedReader reader)
        throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.isBlank()) {
                return line;
            }
        }
        return null;
    }

    private static int indexOfHeader(String[] headers, String columnName) {
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            if (
                i == 0 &&
                header != null &&
                !header.isEmpty() &&
                header.charAt(0) == '\uFEFF'
            ) {
                header = header.substring(1);
            }
            if (columnName.equals(header != null ? header.trim() : null)) {
                return i;
            }
        }
        return -1;
    }

    private static char detectDelimiter(String line) {
        int commas = countDelimiter(line, ',');
        int semicolons = countDelimiter(line, ';');
        int tabs = countDelimiter(line, '\t');
        if (semicolons > commas && semicolons >= tabs) {
            return ';';
        }
        if (tabs > commas && tabs > semicolons) {
            return '\t';
        }
        return ',';
    }

    private static int countDelimiter(String line, char delimiter) {
        boolean inQuotes = false;
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (
                    inQuotes &&
                    i + 1 < line.length() &&
                    line.charAt(i + 1) == '"'
                ) {
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (!inQuotes && c == delimiter) {
                count++;
            }
        }
        return count;
    }

    private static String[] parseCsvLine(String line, char delimiter) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (
                    inQuotes &&
                    i + 1 < line.length() &&
                    line.charAt(i + 1) == '"'
                ) {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (!inQuotes && c == delimiter) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}
