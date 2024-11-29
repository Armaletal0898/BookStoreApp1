package bookapp;

import java.sql.Connection; 
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;



public class DatabaseManager {
	Scanner sc = new Scanner(System.in);
	private Connection connection;
	
	//conectando la base de datos
	public Connection connect() {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pubs","root","12345");
			
			System.out.println("Conexion a la base de datos exitosa.");
			
		}catch (SQLException e) {
		System.out.println("Error en la conexion a la base de datos: " + e.getMessage());
		e.printStackTrace();
	}
		return connection;
}
	

public Connection getConnection() {
	if (connection == null) {
		connect(); // Asegura que la conexion este establecida
	}
	return connection;
}


public void disconnect() {
	try {
		if(connection != null && !connection.isClosed()) {
			connection.close();
			
			System.out.println("Conexion cerrada.");
		}
	}catch (SQLException e) {
		System.out.println("Error al cerrar la conexion: " + e.getMessage());
		}
	}




public class TableObject {
    private int id; // Número identificador de la tabla
    private String tableName; // Nombre de la tabla

    // Constructor
    public TableObject(int id, String tableName) {
        this.id = id;
        this.tableName = tableName;
    }

    // Getter para id
    public int getId() {
        return id;
    }

    // Getter para tableName
    public String getTableName() {
        return tableName;
    }

    // Método toString (opcional) para mostrar el objeto en formato legible
    @Override
    public String toString() {
        return id + ". " + tableName;
    }
}




public ArrayList<TableObject> showTables() {
    ArrayList<TableObject> tables = new ArrayList<>();
    String query = "SHOW TABLES";

    try (Statement stmt = connection.createStatement();
         ResultSet resultSet = stmt.executeQuery(query)) {

        System.out.println("\n-------------------------------------------");
        System.out.println("Tablas disponibles en la base de datos:");
        System.out.println("-------------------------------------------");

        int index = 1;
        while (resultSet.next()) {
            String tableName = resultSet.getString(1);
            TableObject table = new TableObject(index, tableName);
            tables.add(table);
            System.out.println(index + ". " + tableName);
            index++;
        }

        if (tables.isEmpty()) {
            System.out.println("No hay tablas disponibles.");
        }

        // Preguntar si desea continuar
		Scanner sc = new Scanner(System.in);
			String response;
			boolean exit = false;

			// Usamos un do-while para ejecutar al menos una vez
			do {
			    System.out.print("\n¿Desea ver los datos de alguna tabla? (s/n): ");
			    response = sc.nextLine();

			    if (response.equalsIgnoreCase("s")) {
			        System.out.print("Ingrese el número de la tabla que desea ver: ");
			        int tableIndex = sc.nextInt();
			        sc.nextLine(); // Consumir la línea pendiente

			        // Asegurarse de que el índice esté dentro de los límites
			        if (tableIndex > 0 && tableIndex <= tables.size()) {
			            String tableName = tables.get(tableIndex - 1).getTableName();
			            showTableData(tableName);  // Llamar al método para mostrar los datos de la tabla
			        } else {
			            System.out.println("Número de tabla no válido.");
			        }
			    } else if (response.equalsIgnoreCase("n")) {
			        exit = true; // Salir del ciclo
			    } else {
			        System.out.println("Respuesta no válida. Por favor ingrese 's' para sí o 'n' para no.");
			    }

			} while (!exit); // Continuar hasta que la respuesta sea 'n'
		

    } catch (SQLException e) {
        System.out.println("Error al mostrar las tablas: " + e.getMessage());
    }

    return tables;
}





public void showTableData(String tableName) {
    String query = "SELECT * FROM " + tableName;

    try (Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
         ResultSet resultSet = stmt.executeQuery(query)) {

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Calcular el ancho máximo de cada columna (nombre de columna y datos)
        int[] columnWidths = new int[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnWidths[i - 1] = metaData.getColumnName(i).length();
        }

        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String value = resultSet.getString(i);
                if (value != null) {
                    columnWidths[i - 1] = Math.max(columnWidths[i - 1], value.length());
                } else {
                    columnWidths[i - 1] = Math.max(columnWidths[i - 1], 4);  // "null"
                }
            }
        }

        // Mostrar encabezados de las columnas
        System.out.println("\nDatos de la tabla: " + tableName);
        for (int i = 1; i <= columnCount; i++) {
            System.out.print(String.format("%-" + columnWidths[i - 1] + "s", metaData.getColumnName(i)) + " | ");
        }
        System.out.println();
        System.out.println("-".repeat(columnWidths.length * 4));

        // Volver a ejecutar la consulta para mostrar los datos
        resultSet.beforeFirst();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String value = resultSet.getString(i);
                // Si el valor es nulo, mostrar "N/A"
                System.out.print(String.format("%-" + columnWidths[i - 1] + "s", value != null ? value : "N/A") + " | ");
            }
            System.out.println();
        }

    } catch (SQLException e) {
        System.out.println("Error al mostrar los datos de la tabla: " + e.getMessage());
    }
}








// Método para obtener las columnas de una tabla
public ArrayList<String> getTableColumns(String tableName) {
    ArrayList<String> columns = new ArrayList<>();
    try {
        String query = "DESCRIBE " + tableName;
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            columns.add(rs.getString("Field"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return columns;
}

// Método para obtener los datos de una columna específica
public ArrayList<String> getColumnData(String tableName, String columnName) {
    ArrayList<String> data = new ArrayList<>();
    try {
        String query = "SELECT " + columnName + " FROM " + tableName;
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            data.add(rs.getString(columnName));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return data;
}

// Método para mostrar los detalles completos de una tienda
public void showTableDetails(String tableName, String column, String value) {
    // Crear consulta SQL dinámica para obtener detalles
    String query = "SELECT * FROM " + tableName + " WHERE " + column + " = ?";
    try (Connection conn = this.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        
        stmt.setString(1, value);  // Establecer el valor del parámetro

        try (ResultSet rs = stmt.executeQuery()) {
            // Mostrar los detalles de la fila
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsMetaData.getColumnName(i);
                    String columnValue = rs.getString(i);
                    System.out.println(columnName + ": " + columnValue);
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public void showTablesAndData() {
    ArrayList<TableObject> tables = showTables(); // Obtener las tablas
    if (tables.isEmpty()) {
        System.out.println("No hay tablas disponibles.");
    } else {
        // Mostrar los datos de la primera tabla (sin preguntar más)
        System.out.print("Proceso de ver tablas terminado");;
    }
}

}



