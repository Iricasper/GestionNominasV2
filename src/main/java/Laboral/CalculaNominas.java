package Laboral;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Funcionalidad principal de la aplicación, donde probaremos los diferentes módulos.

public class CalculaNominas {
    static void main(String[] args) throws IOException, SQLException {

        String empleadosTxt = "res/empleados.txt";
        String sueldosBat = "res/sueldos.bat";
        String empleadosNuevosTxt = "res/empleadosNuevos.txt";

        Map<String, Empleado> empleadosMap = new HashMap<>();

        generarTxtBase(empleadosTxt);

        lecturaTxt(empleadosTxt, empleadosMap);

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/gestion_nominas", "root", "123456");

            for (Empleado each : empleadosMap.values()) {
                escribe(each);
            }

            empleadosMap.get("32000031R").incrAnyo();
            empleadosMap.get("32000032G").setCategoria(9);

            for (Empleado each : empleadosMap.values()) {
                escribe(each);
            }

            guardarTxt(empleadosTxt, empleadosMap);

            guardarBat(sueldosBat, empleadosMap);

            try (PreparedStatement statement = connection.prepareStatement("""
                      UPDATE empleados
                          SET anyos = ? WHERE dni = ?
                    """)) {
                statement.setInt(1, empleadosMap.get("32000031R").anyos);
                statement.setString(2, "32000031R");
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement("""
                      UPDATE empleados SET categoria = ? WHERE dni = ?
                    """)) {
                statement.setInt(1, empleadosMap.get("32000032G").getCategoria());
                statement.setString(2, "32000032G");
                statement.executeUpdate();
            }

            actualizarSueldosCompleto(connection, empleadosMap);

        } catch (DatosNoCorrectosException e) {
            System.out.println(e.getMessage());
        }
        altaEmpleado(empleadosNuevosTxt, empleadosMap);

        guardarTxt(empleadosTxt, empleadosMap);
        guardarBat(sueldosBat, empleadosMap);

        for (Empleado each : empleadosMap.values()) {
            escribe(each);
        }
        menu(empleadosMap);
        if (connection != null) {
            connection.close();
        }
    }

    private static void actualizarSueldosCompleto(Connection connection, Map<String, Empleado> empleadosMap) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                  UPDATE nominas SET sueldo = ? WHERE dni = ?
                """)) {
            for (Empleado each : empleadosMap.values()) {
                statement.setInt(1, Nomina.sueldo(each));
                statement.setString(2, each.dni);
                statement.executeUpdate();
            }
        }
    }

    private static void guardarTxt(String empleadosTxt, Map<String, Empleado> empleadosMap) {
        try (FileWriter fw = new FileWriter(empleadosTxt)) {

            for (Empleado each : empleadosMap.values()) {
                fw.write(registraEmpleado(each) + "\n");
            }

        } catch (IOException ex) {
            System.err.println("Se produjo un error al abrir o escribir en el fichero " + empleadosTxt);
        }
    }

    private static void guardarBat(String sueldosBat, Map<String, Empleado> empleadosMap) {
        try (FileWriter fw = new FileWriter(sueldosBat)) {
            for (Empleado each : empleadosMap.values()) {
                fw.write(each.dni + "\n");
                fw.write(Nomina.sueldo(each) + "\n");
            }

        } catch (IOException e) {
            System.err.println("Se produjo un error al abrir o escribir en el fichero " + sueldosBat);
        }
    }

    private static void generarTxtBase(String textoTxt) {
        try (FileWriter fw = new FileWriter(textoTxt)) {
            fw.write("""
                    32000032G
                    James Cosling
                    M
                    7
                    4
                    32000031R
                    Ada Lovelace
                    F
                    0
                    1
                    """);
        } catch (IOException ex) {
            System.err.println("Se produjo un error al abrir o escribir en el fichero " + textoTxt);
        }
    }

    private static void lecturaTxt(String textoTxt, Map<String, Empleado> empleadosMap) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(textoTxt))) {
            String linea = br.readLine();
            while (linea != null) {
                String dni = linea;
                String nombre = br.readLine();
                char sexo = br.readLine().charAt(0);
                int anyos = Integer.parseInt(br.readLine());
                int categoria = Integer.parseInt(br.readLine());
                empleadosMap.put(dni, (new Empleado(nombre, dni, sexo, categoria, anyos)));
                linea = br.readLine();
            }
        } catch (NumberFormatException | DatosNoCorrectosException | FileNotFoundException exc) {
            System.err.println(exc.getMessage());
        }
    }

    /**
     * Muestra en pantalla todos los datos recogidos.
     *
     * @param emp Empleado cuyos datos mostraremos
     */
    private static void escribe(Empleado emp) {
        emp.imprime();
        System.out.println("Nómina: " + Nomina.sueldo(emp) + " €");
        System.out.println();
    }

    private static String registraEmpleado(Empleado emp) {
        return emp.dni + "\n" + emp.nombre + "\n" + emp.sexo + "\n" + emp.anyos + "\n" + emp.getCategoria();
    }

    private static void altaEmpleado(Map<String, Empleado> empleadosMap) throws DatosNoCorrectosException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce el DNI:");
        String dni = sc.nextLine();
        System.out.println("Introduce el nombre:");
        String nombre = sc.nextLine();
        System.out.println("Introduce el sexo (F o M):");
        char sexo = sc.nextLine().charAt(0);
        System.out.println("Introduce los años de experiencia (0 si no tiene):");
        int anyos = sc.nextInt();
        System.out.println("Introduce la categoría (1 si es nuevo):");
        int categoria = sc.nextInt();
        try {
            empleadosMap.put(dni, (new Empleado(dni, nombre, sexo, categoria, anyos)));
        } catch (DatosNoCorrectosException e) {
            throw new DatosNoCorrectosException("Datos no válidos");
        }
        try {
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/gestion_nominas", "root", "123456");
            try (PreparedStatement statement = connection.prepareStatement("""
                      INSERT INTO empleados VALUES (?, ?, ?, ?, ?);
                    """)) {
                statement.setString(1, dni);
                statement.setString(2, nombre);
                statement.setString(3, String.valueOf(sexo));
                statement.setInt(4, anyos);
                statement.setInt(5, categoria);
                statement.executeUpdate();

                try (PreparedStatement statement2 = connection.prepareStatement("""
                          INSERT INTO nominas VALUES (?, ?)
                        """)) {
                    statement2.setString(1, dni);
                    statement2.setInt(2, Nomina.sueldo(empleadosMap.get(dni)));
                    statement2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void altaEmpleado(String empleadosNuevosTxt, Map<String, Empleado> empleadosMap) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/gestion_nominas", "root", "123456");
            lecturaTxt(empleadosNuevosTxt, empleadosMap);
            try (PreparedStatement statement = connection.prepareStatement("""
                      INSERT INTO empleados VALUES (?, ?, ?, ?, ?)
                      ON DUPLICATE KEY UPDATE nombre=?, sexo=?, anyos=?, categoria=?;
                    """)) {
                for (Empleado each : empleadosMap.values()) {
                    statement.setString(1, each.dni);
                    statement.setString(2, each.nombre);
                    statement.setString(3, String.valueOf(each.sexo));
                    statement.setInt(4, each.anyos);
                    statement.setInt(5, each.getCategoria());
                    statement.setString(6, each.nombre);
                    statement.setString(7, String.valueOf(each.sexo));
                    statement.setInt(8, each.anyos);
                    statement.setInt(9, each.getCategoria());
                    statement.addBatch();
                }
                statement.executeBatch();

                try (PreparedStatement statement2 = connection.prepareStatement("""
                          INSERT INTO nominas VALUES (?, ?)
                          ON DUPLICATE KEY UPDATE sueldo=?
                        """)) {
                    for (Empleado each : empleadosMap.values()) {
                        statement2.setString(1, each.dni);
                        statement2.setInt(2, Nomina.sueldo(empleadosMap.get(each.dni)));
                        statement2.setInt(3, Nomina.sueldo(empleadosMap.get(each.dni)));
                        statement2.addBatch();
                    }
                    statement2.executeBatch();
                }
            }

        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void menu(Map<String, Empleado> empleados) throws SQLException {
        Scanner sca = new Scanner(System.in);
        int opcion = -1;
        Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/gestion_nominas", "root", "123456");

        while (opcion != 0) {
            System.out.print("""
                    MENU EMPLEADOS
                    ---------------------------------
                    1-Mostrar todos los datos
                    2-Buscar salario por DNI
                    3-Modificar datos empleados
                    4-Actualizar nómina empleado por DNI
                    5-Actualizar todas las nóminas
                    6-Copia de seguridad en ficheros
                    0-Salir
                    --------------------------------
                    Elige la opción:
                    """);
            opcion = sca.nextInt();

            switch (opcion) {
                case 1:
                    mostrarEmpleadosBD();
                    break;
                case 2:
                    mostrarEmpleadoPorDNI();
                    break;
                case 3:
                    menuModificacionBD(empleados);
                    break;
                case 4:
                    Scanner scan = new Scanner(System.in);
                    System.out.println("Introduce el DNI del usuario a consultar");
                    String dni = scan.nextLine();
                    actualizarSueldo(empleados, connection, dni);
                    break;
                case 5:
                    actualizarSueldosCompleto(connection, empleados);
                    break;
                case 6:
                    System.out.println("Realizando copia en empleados.txt...");
                    guardarTxt("res/empleados.txt", empleados);
                    System.out.println("Realizando copia en sueldos.bat...");
                    guardarBat("res/sueldos.bat", empleados);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida");
                    break;
            }
        }
        System.out.println("Adiós muy buenas");
    }

    private static void menuModificacionBD(Map<String, Empleado> empleados) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/gestion_nominas", "root", "123456");
        Scanner scan = new Scanner(System.in);
        System.out.println("Introduce el DNI cuyos datos quieres modificar: ");
        String dni = scan.nextLine();
        int opcion2;
        do {
            System.out.printf("""
                    MENU MODIFICACIÓN DE DATOS DEL USUARIO CON DNI %s
                    ---------------------------------
                    1-Modificar nombre
                    2-Modificar sexo
                    3-Modificar años de experiencia
                    4-Modificar categoría
                    5-Seleccionar otro DNI
                    0-Salir
                    ---------------------------------
                    Elegir la opción:
                    """, dni);
            opcion2 = scan.nextInt();
            Scanner sc = new Scanner(System.in);
            PreparedStatement stmt;

            switch (opcion2) {
                case 1:
                    System.out.println("Nuevo nombre: ");
                    String nombre = sc.nextLine();
                    stmt = conn.prepareStatement("UPDATE empleados SET nombre = ? WHERE dni = ?");
                    stmt.setString(1, nombre);
                    stmt.setString(2, dni);
                    stmt.executeUpdate();
                    empleados.get(dni).nombre = nombre;
                    break;
                case 2:
                    System.out.println("Nuevo sexo: ");
                    String sexo = sc.nextLine();
                    stmt = conn.prepareStatement("UPDATE empleados SET sexo = ? WHERE dni = ?");
                    stmt.setString(1, sexo);
                    stmt.setString(2, dni);
                    stmt.executeUpdate();
                    empleados.get(dni).sexo = sexo.charAt(0);
                    break;
                case 3:
                    System.out.println("Nueva antigüedad: ");
                    int anyos = scan.nextInt();
                    stmt = conn.prepareStatement("UPDATE empleados SET anyos = ? WHERE dni = ?");
                    stmt.setInt(1, anyos);
                    stmt.setString(2, dni);
                    stmt.executeUpdate();
                    empleados.get(dni).anyos = anyos;
                    actualizarSueldo(empleados, conn, dni);
                    break;
                case 4:
                    System.out.println("Nueva categoría: ");
                    int categoria = scan.nextInt();
                    stmt = conn.prepareStatement("UPDATE empleados SET categoria = ? WHERE dni = ?");
                    stmt.setInt(1, categoria);
                    stmt.setString(2, dni);
                    stmt.executeUpdate();
                    empleados.get(dni).setCategoria(categoria);
                    actualizarSueldo(empleados, conn, dni);
                    break;
                case 5:
                    System.out.println("Introduce el DNI cuyos datos quieres modificar: ");
                    dni = sc.nextLine();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion2 != 0);
    }

    private static void actualizarSueldo(Map<String, Empleado> empleados, Connection conn, String dni) throws SQLException {
        PreparedStatement statement2 = conn.prepareStatement("""
                  INSERT INTO nominas VALUES (?, ?)
                """);
        statement2.setString(1, dni);
        statement2.setInt(2, Nomina.sueldo(empleados.get(dni)));
        statement2.executeUpdate();
    }

    private static void mostrarEmpleadoPorDNI() throws SQLException {
        System.out.println("Selecciona el DNI a consultar: ");
        Scanner sc = new Scanner(System.in);
        String dni = sc.nextLine();
        Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/gestion_nominas", "root", "123456");
        try {
            PreparedStatement statement = conn.prepareStatement("""
                    SELECT * FROM empleados WHERE dni = ?
                    """);
            statement.setString(1, dni);
            ResultSet rs = statement.executeQuery();
            rs.next(); // Empieza contando por 0, que es un objeto vacío, por eso lo saltamos
            System.out.println("DNI: " + rs.getString(1));
            System.out.println("Nombre: " + rs.getString(2));
            System.out.println("Sexo: " + rs.getString(3));
            System.out.println("Años: " + rs.getInt(4));
            System.out.println("Categoría: " + rs.getInt(5) + "\n");
        } catch (SQLException e) {
            System.err.print("Error de SQL: " + e.getMessage());
        }
    }

    private static void mostrarEmpleadosBD() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/gestion_nominas", "root", "123456");
        try {
            PreparedStatement statement = conn.prepareStatement("""
                    SELECT * FROM empleados
                    """);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                System.out.println("DNI: " + rs.getString(1));
                System.out.println("Nombre: " + rs.getString(2));
                System.out.println("Sexo: " + rs.getString(3));
                System.out.println("Años: " + rs.getInt(4));
                System.out.println("Categoría: " + rs.getInt(5) + "\n");
            }
        } catch (SQLException e) {
            System.err.print("Error de SQL: " + e.getMessage());
        }
    }
}
