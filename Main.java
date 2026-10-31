import java.util.Scanner;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String mode = args.length > 0 ? args[0] : "";
        if (mode.isEmpty()) {
            System.out.print("1 = Console, 2 = GUI : ");
            mode = sc.nextLine().trim().equals("2") ? "gui" : "console";
        }
        if (mode.equalsIgnoreCase("gui")) SwingUtilities.invokeLater(() -> new RestaurantGUI().setVisible(true));
        else new ConsoleApp(sc).run();
    }
}
