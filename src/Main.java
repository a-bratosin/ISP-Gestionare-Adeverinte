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
        } else if (user instanceof SecretarDeAn) {
            SecretarDeAn secretar = (SecretarDeAn) user;
            secretar.valideazaCerere(scanner);
        } else {
            System.out.println("Rolul utilizatorului nu are actiuni implementate in Main.");
        }
    }
}
