package bookapp;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BookStoreManager {
	Scanner sc = new Scanner(System.in);
	
	// Método para obtener la conexión a la base de datos
	public static Connection getConnection() throws SQLException {
	        return DriverManager.getConnection("jdbc:mysql://localhost:3306/pubs", "root", "12345"); // Ajusta esto según tu configuración
	  }
		
		
	public static void addField(String tableName, Scanner sc) {
	    try (Connection connection = getConnection()) {
	        // Consulta para obtener las columnas de la tabla
	        DatabaseMetaData metaData = connection.getMetaData();
	        ResultSet columns = metaData.getColumns(null, null, tableName, null);

	        // Mostrar las columnas de la tabla
	        System.out.println("Columnas de la tabla " + tableName + ":");
	        StringBuilder columnNames = new StringBuilder();
	        while (columns.next()) {
	            columnNames.append(columns.getString("COLUMN_NAME")).append(", ");
	        }
	        // Eliminar la última coma y espacio
	        columnNames.setLength(columnNames.length() - 2);
	        System.out.println(columnNames);

	        String response;
	        
	        do {
	            // Obtener los valores para cada columna
	            StringBuilder insertColumns = new StringBuilder();
	            StringBuilder insertValues = new StringBuilder();
	            columns = metaData.getColumns(null, null, tableName, null); // reiniciar el result set

	            while (columns.next()) {
	                String columnName = columns.getString("COLUMN_NAME");
	                System.out.print("\n-------------------------------------------\n");
	                System.out.print("Ingrese el valor para la columna " + columnName + ": ");
	                String value = sc.nextLine();

	                // Se agrega la columna y el valor en los respectivos StringBuilder
	                insertColumns.append(columnName).append(", ");
	                insertValues.append("'").append(value).append("', ");
	            }

	            // Eliminar la última coma y espacio de las listas de columnas y valores
	            if (insertColumns.length() > 0) {
	                insertColumns.setLength(insertColumns.length() - 2);
	            }

	            if (insertValues.length() > 0) {
	                insertValues.setLength(insertValues.length() - 2);
	            }

	            // Crear la consulta SQL de inserción
	            String sql = "INSERT INTO " + tableName + " (" + insertColumns + ") VALUES (" + insertValues + ")";
	            System.out.println("Consulta SQL generada: " + sql);

	            // Ejecutar la consulta de inserción
	            try (Statement stmt = connection.createStatement()) {
	                int rowsAffected = stmt.executeUpdate(sql);
	                if (rowsAffected > 0) {
	                    System.out.println("Fila insertada correctamente en la tabla " + tableName);
	                }
	            }

	            // Preguntar al usuario si desea continuar con la inserción de más filas
	            System.out.print("\n¿Desea continuar insertando otra fila? (s/n): ");
	            response = sc.nextLine();

	        } while (response.equalsIgnoreCase("s"));  // Continuar si la respuesta es 's'

	    } catch (SQLException e) {
	        System.err.println("Error al acceder a la base de datos: " + e.getMessage());
	    }
	}


	 
	 
	   
	    
	public static void modifyField(Scanner sc) {
	    try (Connection connection = getConnection()) {
	        // Solicitar el nombre de la tabla y la columna a modificar
	        System.out.print("Ingrese el nombre de la tabla: ");
	        String tableName = sc.nextLine();

	        // Obtener los nombres de las columnas
	        DatabaseMetaData metaData = connection.getMetaData();
	        ResultSet columns = metaData.getColumns(null, null, tableName, null);

	        // Mostrar las columnas disponibles
	        System.out.println("Columnas en la tabla " + tableName + ":");
	        while (columns.next()) {
	            System.out.println(columns.getString("COLUMN_NAME"));
	        }

	        // Seleccionar la columna a modificar
	        System.out.print("\n-------------------------------------------");
	        System.out.print("\nIngrese el nombre de la columna a modificar: ");
	        String columnNameToModify = sc.nextLine();

	        // Obtener los valores actuales de la columna y su ID (o una columna única)
	        String selectQuery = "SELECT ID, " + columnNameToModify + " FROM " + tableName;
	        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
	             ResultSet columnValues = selectStatement.executeQuery()) {

	            // Mostrar los registros
	            System.out.println("\nRegistros de la columna " + columnNameToModify + ":");
	            List<String> valuesList = new ArrayList<>();
	            List<Integer> idsList = new ArrayList<>();
	            int rowNumber = 1;
	            while (columnValues.next()) {
	                int id = columnValues.getInt("ID"); // Suponiendo que la columna ID es la clave primaria
	                String value = columnValues.getString(columnNameToModify);
	                valuesList.add(value);
	                idsList.add(id);
	                System.out.println(rowNumber++ + ". " + value + " (ID: " + id + ")");
	            }

	            // Solicitar el registro a modificar
	            System.out.print("\n-------------------------------------------");
	            System.out.print("\nSeleccione el número del registro a modificar: ");
	            int recordNumber = sc.nextInt();
	            sc.nextLine(); // Limpiar buffer

	            if (recordNumber > 0 && recordNumber <= valuesList.size()) {
	                String oldValue = valuesList.get(recordNumber - 1);
	                int recordId = idsList.get(recordNumber - 1); // Obtener el ID de la fila seleccionada

	                // Solicitar el nuevo valor
	                System.out.print("\nIngrese el nuevo valor para " + columnNameToModify + ": ");
	                String newValue = sc.nextLine();

	                // Actualizar el registro usando el ID como referencia única
	                String updateSQL = "UPDATE " + tableName + " SET " + columnNameToModify + " = ? WHERE ID = ?";
	                try (PreparedStatement updateStatement = connection.prepareStatement(updateSQL)) {
	                    updateStatement.setString(1, newValue);
	                    updateStatement.setInt(2, recordId); // Usamos el ID único para asegurar que se modifique solo esa fila
	                    int rowsAffected = updateStatement.executeUpdate();
	                    System.out.println(rowsAffected > 0 ? "Registro actualizado exitosamente." : "No se pudo actualizar el registro.");
	                } catch (SQLException e) {
	                    System.out.println("Error al actualizar el registro: " + e.getMessage());
	                }
	            } else {
	                System.out.println("Número de registro no válido.");
	            }
	        }

	        // Preguntar al usuario si desea continuar
	        System.out.print("\n¿Desea continuar modificando otro registro? (s/n): ");
	        String response = sc.nextLine();
	        if (response.equalsIgnoreCase("s")) {
	            modifyField(sc);  // Llamada recursiva para continuar
	        } else {
	            System.out.println("Proceso de modificación terminado.");
	        }

	    } catch (SQLException e) {
	        System.out.println("Error al acceder a la base de datos: " + e.getMessage());
	    }
	}

	
	
	public static void deleteRow(String tableName, Scanner sc) {
	    try (Connection connection = getConnection()) {
	        boolean continueDeletion = true; // Bandera para controlar si el usuario desea continuar

	        while (continueDeletion) {
	            // Obtener los nombres de las columnas de la tabla
	            DatabaseMetaData metaData = connection.getMetaData();
	            ResultSet columns = metaData.getColumns(null, null, tableName, null);

	            // Mostrar las columnas de la tabla
	            System.out.println("Columnas de la tabla " + tableName + ":");
	            StringBuilder columnNames = new StringBuilder();
	            while (columns.next()) {
	                columnNames.append(columns.getString("COLUMN_NAME")).append(", ");
	            }

	            // Eliminar la última coma y espacio
	            if (columnNames.length() > 0) {
	                columnNames.setLength(columnNames.length() - 2);
	            }
	            System.out.println(columnNames);

	            // Solicitar al usuario seleccionar una columna
	            System.out.print("\n-------------------------------------------\n");
	            System.out.print("Ingrese el nombre de la columna por la cual desea eliminar la fila: ");
	            String columnToDelete = sc.nextLine();

	            // Verificar el tipo de la columna para asegurarse de que sea String o int
	            boolean isValidColumn = false;
	            columns = metaData.getColumns(null, null, tableName, columnToDelete); // Volver a obtener los datos de la columna seleccionada

	            while (columns.next()) {
	                String columnType = columns.getString("TYPE_NAME");
	                // Permitir tanto columnas de tipo String como numéricas (INT)
	                if (columnType.equalsIgnoreCase("VARCHAR") || columnType.equalsIgnoreCase("CHAR") || columnType.equalsIgnoreCase("INT")) {
	                    isValidColumn = true;
	                }
	            }

	            if (!isValidColumn) {
	                System.out.println("La columna seleccionada no es válida para eliminación. Solo se permiten columnas de tipo String o int.");
	                continue; // Volver a pedir la columna si no es válida
	            }

	            // Mostrar los valores de la columna seleccionada
	            String selectQuery = "SELECT " + columnToDelete + " FROM " + tableName;
	            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
	                 ResultSet resultSet = selectStatement.executeQuery()) {

	                System.out.println("\nValores de la columna " + columnToDelete + ":");
	                int rowNumber = 1;
	                while (resultSet.next()) {
	                    String value = resultSet.getString(1);
	                    System.out.println(rowNumber++ + ". " + value);
	                }
	            }

	            // Solicitar el valor para la columna seleccionada
	            System.out.print("\n-------------------------------------------\n");
	            System.out.print("Ingrese el valor de la columna " + columnToDelete + " para eliminar la fila: ");
	            String valueToDelete = sc.nextLine();

	            // Crear la consulta SQL para eliminar la fila usando la columna seleccionada
	            String sql = "DELETE FROM " + tableName + " WHERE " + columnToDelete + " = ?";

	            // Ejecutar la consulta DELETE
	            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	                pstmt.setString(1, valueToDelete);
	                int rowsAffected = pstmt.executeUpdate();
	                if (rowsAffected > 0) {
	                    System.out.println("Fila eliminada correctamente de la tabla " + tableName);
	                } else {
	                    System.out.println("No se encontró ninguna fila con el valor proporcionado para eliminar.");
	                }
	            }

	            // Preguntar al usuario si desea continuar con la eliminación o salir
	            System.out.print("\n¿Desea eliminar otra fila? (s/n): ");
	            String response = sc.nextLine().trim().toLowerCase();
	            if (!response.equals("s")) {
	                continueDeletion = false; // Terminar el ciclo si la respuesta no es 's'
	                System.out.println("Operación de eliminación finalizada.");
	                // Aquí puedes regresar al menú principal o realizar alguna otra acción
	                return; // Esto termina el método y regresa al flujo de selección de tabla
	            }
	        }

	    } catch (SQLException e) {
	        System.err.println("Error al acceder a la base de datos: " + e.getMessage());
	    }
	}



}

		
	
	
	

