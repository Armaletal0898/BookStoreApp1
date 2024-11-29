package bookapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;


public class BookStoreQueries {
	 // Método para conectar a la base de datos
    private Connection connect() {
        String url = "jdbc:mysql://localhost:3306/pubs";  // Ajusta la URL de tu base de datos
        String user = "root";  // Tu usuario de base de datos
        String password = "12345";  // Tu contraseña de base de datos

        try {
            // Establecer conexión con la base de datos
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa a la base de datos.");
            return conn;
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos.");
            e.printStackTrace();
            return null;
        }
    }
    

    // Obtener los nombres de las tablas en la base de datos
    public ArrayList<String> getTableNames() {
        ArrayList<String> tables = new ArrayList<>();
        String query = "SHOW TABLES";  // Query para obtener todas las tablas
        try (Connection conn = this.connect();  // Llamamos a nuestro propio método connect
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                tables.add(rs.getString(1));  // Agregar el nombre de cada tabla a la lista
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    // Obtener las columnas de una tabla específica
    public ArrayList<String> getTableColumns(String tableName) {
        ArrayList<String> columns = new ArrayList<>();
        String query = "DESCRIBE " + tableName;  // Query para obtener las columnas de la tabla
        try (Connection conn = this.connect(); 
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                columns.add(rs.getString(1));  // Agregar el nombre de cada columna a la lista
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columns;
    }

    // Obtener los datos de una columna específica de una tabla
    public ArrayList<String> getColumnData(String tableName, String columnName) {
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT " + columnName + " FROM " + tableName;  // Query para obtener los datos de la columna
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                data.add(rs.getString(1));  // Agregar los datos de la columna a la lista
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Mostrar los detalles de una opción seleccionada de una tabla
    public void showTableDetails(String tableName, String columnName, String value) {
        String query = "SELECT * FROM " + tableName + " WHERE " + columnName + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, value);  // Establecer el valor a consultar
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Mostrar los detalles de todos los campos de la fila seleccionada
                int columnCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnNameDB = rs.getMetaData().getColumnName(i);
                    String columnValue = rs.getString(i);
                    System.out.println(columnNameDB + ": " + columnValue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

 // Método para consultar y obtener datos específicos de la tabla
    public static void consultSpecifies() {
        // Crear instancia de BookStoreQueries para acceder a sus métodos
        BookStoreQueries dbManager = new BookStoreQueries();

        // Obtener todas las tablas de la base de datos
        ArrayList<String> tables = dbManager.getTableNames();

        // Mostrar las tablas disponibles
        System.out.println("Tablas disponibles en la base de datos:");
        for (int i = 0; i < tables.size(); i++) {
            System.out.println((i + 1) + ". " + tables.get(i));
        }

        // Crear un objeto Scanner para leer la entrada del usuario
        Scanner sc = new Scanner(System.in);
        String response = null;

        // Iniciar un ciclo para permitir múltiples consultas
        do {
            // Selección de la tabla
            System.out.print("\nOPCION PARA BUSCAR DATOS ESPECIFICOS EN LAS TABLAS ");
            System.out.print("\n---------------------------------------------------");
            System.out.print("\nSeleccione una tabla para consultar por nombre: ");
            String tableName = sc.nextLine();

            // Validar que la tabla exista en la base de datos
            if (!tables.contains(tableName)) {
                System.out.println("Tabla no válida. Por favor seleccione una tabla válida.");
                continue;
            }

            // Mostrar las columnas de la tabla seleccionada
            ArrayList<String> columns = dbManager.getTableColumns(tableName); // Obtener las columnas de la tabla seleccionada
            System.out.println("Columnas disponibles en la tabla '" + tableName + "':");
            for (int i = 0; i < columns.size(); i++) {
                System.out.println((i + 1) + ". " + columns.get(i));
            }

            // Selección de la columna
            System.out.print("\n-------------------------------------------");
            System.out.print("\n-------------------------------------------");
            System.out.print("\nSeleccione una columna para consultar por numero: ");
            int columnIndex = sc.nextInt();
            sc.nextLine(); // Consumir nueva línea

            if (columnIndex < 1 || columnIndex > columns.size()) {
                System.out.println("Selección inválida.");
                continue;
            }

            String selectedColumn = columns.get(columnIndex - 1);

            // Mostrar los valores de la columna seleccionada
            System.out.println("Valores de la columna '" + selectedColumn + "':");
            ArrayList<String> columnData = dbManager.getColumnData(tableName, selectedColumn); // Obtener los datos de la columna
            for (int i = 0; i < columnData.size(); i++) {
                System.out.println((i + 1) + ". " + columnData.get(i));
            }

            // Selección de un valor de la columna
            System.out.print("\n-------------------------------------------");
            System.out.print("\n-------------------------------------------");
            System.out.print("\nSeleccione una opción por numero para ver más detalles: ");
            int rowIndex = sc.nextInt();
            sc.nextLine(); // Consumir nueva línea

            if (rowIndex < 1 || rowIndex > columnData.size()) {
                System.out.println("Selección inválida.");
                continue;
            }

            String selectedValue = columnData.get(rowIndex - 1);

            // Mostrar detalles de la opción seleccionada
            System.out.println("\nDetalles de la opción seleccionada (" + selectedValue + "):");
            dbManager.showTableDetails(tableName, selectedColumn, selectedValue); // Mostrar detalles de la opción seleccionada

            // Preguntar si desea continuar
            System.out.print("\n¿Desea realizar otra consulta? (s/n): ");
            response = sc.nextLine();

        } while (response.equalsIgnoreCase("s"));

        // Mensaje de despedida al finalizar
        System.out.println("Gracias por usar el sistema de consultas.");
    }


	
	
	
	 

 // Método para consultar ventas por título
    public static void getSalesPercentage(DatabaseManager dbManager) {
        Connection conn = dbManager.getConnection();
        
        try {
            // Cambiar el tipo de ResultSet a TYPE_SCROLL_INSENSITIVE
            String query = "SELECT price, ytd_sales, title FROM titles";
            PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery();
            
            double totalSales = 0;
            while (rs.next()) {
                // Sumar las ventas totales (price * ytd_sales) para todos los títulos
                double price = rs.getDouble("price");
                int ytdSales = rs.getInt("ytd_sales");
                totalSales += price * ytdSales;
            }
            
            // Volver al inicio del ResultSet
            rs.beforeFirst();
            
            // Calcular el porcentaje de ventas por título
            System.out.println("\n--- PORCENTAJE DE VENTAS POR TITULOS ---");
            System.out.print("\n---------------------------------------------------------------------------------------\n");
            
            // Mostrar el porcentaje de ventas por título
            while (rs.next()) {
                double price = rs.getDouble("price");
                int ytdSales = rs.getInt("ytd_sales");
                
                if (totalSales > 0) {
                    // Calcular porcentaje de ventas
                    double salesValue = price * ytdSales;
                    double salesPercentage = (salesValue / totalSales) * 100;
                    System.out.printf("Título: %s | Porcentaje de Ventas: %.2f%%\n", rs.getString("title"), salesPercentage);
                } else {
                    System.out.println("No se encontraron ventas para calcular.");
                }
            }

            // Al finalizar la ejecución, el método termina y regresa al menú de opciones (o lo que sea que venga después)

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


        
        
        
        
    public static void getSalesByTitle(DatabaseManager dbManager) {
        String query = "SELECT title, price, ytd_sales FROM titles";  // Seleccionar los campos que necesitamos
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        Scanner scanner = new Scanner(System.in);  // Mover el Scanner fuera del ciclo

        try {
            // Obtener la conexión
            conn = dbManager.getConnection();

            // Crear el Statement
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // Ejecutar la consulta
            rs = stmt.executeQuery(query);

            // Hacer que el proceso de selección se repita si el usuario lo desea
            boolean continueSelection = true;

            do {
                // Listar todos los títulos con su índice
                System.out.print("\n-------------------------------------------\n");
                System.out.println("Selecciona un título de la lista introduciendo el número correspondiente:");
                System.out.print("-------------------------------------------\n");

                // Crear una lista de títulos para permitir la selección
                int index = 1;
                while (rs.next()) {
                    String title = rs.getString("title");
                    System.out.println(index + ". " + title);
                    index++;
                }

                // Solicitar la selección del usuario
                System.out.print("\n-------------------------------------------\n");
                System.out.print("Ingresa el número del título que deseas ver: ");
                int selectedIndex = scanner.nextInt();

                // Reestablecer el ResultSet para recorrerlo nuevamente
                rs.beforeFirst();

                // Variable para acumular las ventas totales de todos los títulos
                double grandTotalSales = 0.0;

                // Calcular las ventas totales acumuladas
                while (rs.next()) {
                    double price = rs.getDouble("price");
                    int ytdSales = rs.getInt("ytd_sales");

                    // Ignorar registros con datos incompletos
                    if (!rs.wasNull() && price != 0.0 && ytdSales != 0) {
                        grandTotalSales += price * ytdSales; // Acumular el total
                    }
                }

                // Reestablecer el ResultSet nuevamente para encontrar el título seleccionado
                rs.beforeFirst();
                int currentIndex = 1;
                while (rs.next()) {
                    // Verificar si el índice actual corresponde al título seleccionado
                    if (currentIndex == selectedIndex) {
                        String title = rs.getString("title");
                        double price = rs.getDouble("price");
                        int ytdSales = rs.getInt("ytd_sales");

                        // Calcular el total de ventas por título
                        double totalSales = price * ytdSales;

                        // Imprimir la información del título seleccionado
                        System.out.println("\n----------------------------------------");
                        System.out.println("Título seleccionado: " + title);
                        System.out.println("Precio: $" + price);
                        System.out.println("Ventas acumuladas: " + ytdSales);
                        System.out.println("Total de ventas por título: $" + totalSales);
                        System.out.println("\n----------------------------------------");

                        // Mostrar también las ventas totales acumuladas de todos los títulos
                        System.out.printf("Ventas totales acumuladas de todos los títulos: $%.2f%n", grandTotalSales);
                        System.out.println("----------------------------------------");
                        break;  // Terminamos el ciclo una vez que se ha mostrado el título seleccionado
                    }
                    currentIndex++;
                }

                // Preguntar si el usuario desea continuar
                System.out.print("\n¿Deseas continuar? (s/n): ");
                String respuesta = scanner.next();
                if (respuesta.equalsIgnoreCase("n")) {
                    continueSelection = false;  // Salir del ciclo
                } else {
                    // Reestablecer el ResultSet para el próximo ciclo
                    rs.beforeFirst();
                }

            } while (continueSelection);  // Continuar mientras el usuario lo desee

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                // Eliminar el cierre de la conexión aquí
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}


   
        


    
	 
	 
	 
	
