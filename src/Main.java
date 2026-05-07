import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Utilizator user = Autentificare.login(scanner);
        if (user == null) {
            System.out.println("Autentificare esuata (email/parola gresite sau user inexistent).");
            return;
        }

        System.out.println("Autentificat ca: " + user.getNume() + " " + user.getPrenume());

        if (user instanceof Student) {
            Student student = (Student) user;
            student.incarcareAdeverinta(scanner);
        } else {
            System.out.println("Doar studentii pot incarca adeverinte.");
        }
    }
}
