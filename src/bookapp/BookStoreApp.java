package bookapp;

import java.util.Scanner;

public class BookStoreApp {
    private static final Scanner sc = new Scanner(System.in);
    private static DatabaseManager dbManager;

    public static void main(String[] args) {
        dbManager = new DatabaseManager();
        dbManager.connect();
        new BookStoreManager();
        new BookStoreQueries();

        int option;

        do {
            System.out.println("\n---MENU DE OPCIONES---");
            System.out.println("1. Ver tabla");
            System.out.println("2. Añadir Campo nuevo a la tabla");
            System.out.println("3. Modificar un valor a la tabla");
            System.out.println("4. Eliminar un registro");
            System.out.println("5. Consultas Específicas");
            System.out.println("6. Consultar Porcentaje de ventas por títulos");
            System.out.println("7. Consultar Precio de ventas y Total de ventas");
            System.out.println("8. Salir");

            System.out.print("Seleccione una opción: ");
            option = getIntInput(); // Método para capturar entradas seguras de tipo int

            switch (option) {
                case 1:
                    dbManager.showTablesAndData();
                    break;

                case 2:
                    System.out.print("Ingrese el nombre de la tabla (Por ejemplo, 'authors'): ");
                    String tableName = sc.nextLine();
                    BookStoreManager.addField(tableName, sc);
                    break;

                case 3:
                    BookStoreManager.modifyField(sc);
                    break;

                case 4:
                    System.out.println("ELIMINAR FILA DE UNA TABLA.");
                    System.out.print("Ingrese el nombre de la tabla (por ejemplo, 'authors'): ");
                    String deleteField = sc.nextLine();
                    BookStoreManager.deleteRow(deleteField, sc); // Llamamos al método para eliminar la fila
                    break;

                case 5:
                    BookStoreQueries.consultSpecifies();
                    break;

                case 6:
                    BookStoreQueries.getSalesPercentage(dbManager);
                    break;

                case 7:
                    BookStoreQueries.getSalesByTitle(dbManager);
                    break;

                case 8:
                    System.out.println("Saliendo....");
                    break;

                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
                    break;
            }

        } while (option != 8);

        dbManager.disconnect();
    }

    // Método para capturar entradas de tipo int de manera segura
    public static int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine()); // Intentamos convertir la entrada a un número entero
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Por favor ingrese un número entero: ");
            }
        }
    }
}