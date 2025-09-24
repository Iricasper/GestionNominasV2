package Laboral;

import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

// Funcionalidad principal de la aplicación, donde probaremos los diferentes módulos.
public class CalculaNominas {
    public static void main(String[] args) throws IOException, SQLException {

        String textoTxt = "res/empleados.txt";
        String textoBat = "res/sueldos.bat";

        Map<String, Empleado> empleadosMap = new HashMap<>();

        generarTxtBase(textoTxt);

        lecturaTxt(textoTxt, empleadosMap);

        try {
            Connection connection = DriverManager.getConnection(
                "jdbc:mariadb://localhost:3306/gestion_nominas",
                "root", "123456"
            );

            for (Empleado each : empleadosMap.values()) {
                escribe(each);
            }

            empleadosMap.get("32000031R").incrAnyo();
            empleadosMap.get("32000032G").setCategoria(9);

            for (Empleado each : empleadosMap.values()) {
                escribe(each);
            }

            try (FileWriter fw = new FileWriter(textoTxt)) {

                for (Empleado each : empleadosMap.values()) {
                    fw.write(registraEmpleado(each) + "\n");
                }

            } catch (IOException ex) {
                System.err.println("Se produjo un error al abrir o escribir en el fichero " + textoTxt);
            }

            try (FileWriter fw = new FileWriter(textoBat)) {
                for (Empleado each : empleadosMap.values()) {
                    fw.write(each.dni + "\n");
                    fw.write(Nomina.sueldo(each) + "\n");
                }

            } catch (IOException e) {
                System.err.println("Se produjo un error al abrir o escribir en el fichero " + textoBat);
            }

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

            try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE nominas SET sueldo = ? WHERE dni = ?
              """)) {
                for (Empleado each: empleadosMap.values()) {
                    statement.setInt(1, Nomina.sueldo(each));
                    statement.setString(2, each.dni);
                    statement.executeUpdate();
                }
            }

        } catch (DatosNoCorrectosException e) {
            System.out.println(e.getMessage());
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
     * @param emp Empleado cuyos datos mostraremos
     */
    private static void escribe(Empleado emp) {
        emp.imprime();
        System.out.println("Nómina: " + Nomina.sueldo(emp) + " €");
        System.out.println();
    }

    private static String registraEmpleado(Empleado emp) {
        return emp.dni +"\n"
                + emp.nombre + "\n"
                + emp.sexo + "\n"
                + emp.anyos + "\n"
                + emp.getCategoria();
    }

}