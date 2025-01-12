import menu.MenuController;
import menu.MenuModel;
import menu.MenuView;

public class Main {
    public static void main(String[] args) {
        // Initialize and show menu
        MenuModel menuModel = new MenuModel();
        MenuView menuView = new MenuView();
        new MenuController(menuModel, menuView);
        menuView.setVisible(true);
    }
}