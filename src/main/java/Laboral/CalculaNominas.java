package Laboral;

import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Funcionalidad principal de la aplicación, donde probaremos los diferentes módulos.
public class CalculaNominas {
    public static void main(String[] args) throws IOException, SQLException {

        String textoTxt = "empleados.txt";
        String textoBat = "sueldos.bat";

        List<Empleado> empleadosList = new ArrayList<>();

        empleadosList.add(new Empleado("James Cosling", "32000032G", 'M', 4, 7));
        empleadosList.add(new Empleado("Ada Lovelace", "32000031R", 'F'));

        Connection connection = DriverManager.getConnection(
                "jdbc:mariadb://localhost:3306/gestion_nominas",
                "root", "123456"
        );

        try {
            for (Empleado each : empleadosList) {
                escribe(each);
            }

            try (FileWriter fw = new FileWriter(textoTxt)) {

                for (Empleado each : empleadosList) {
                    fw.write(registraEmpleado(each) + "\n");
                }

            } catch (IOException ex) {
                System.err.println("Se produjo un error al abrir o escribir en el fichero " + textoTxt);
            }

            empleadosList.get(1).incrAnyo();
            empleadosList.get(0).setCategoria(9);

            for (Empleado each : empleadosList) {
                escribe(each);
            }

            try (FileWriter fw = new FileWriter(textoTxt)) {

                for (Empleado each : empleadosList) {
                    fw.write(registraEmpleado(each) + "\n");
                }
            } catch (IOException ex) {
                System.err.println("Se produjo un error al abrir o escribir en el fichero " + textoTxt);
            }

            try (FileWriter fw = new FileWriter(textoBat)) {
                for (Empleado each : empleadosList) {
                    fw.write(each.dni + "\n");
                    fw.write(Nomina.sueldo(each) + "\n");
                }

            } catch (IOException e) {
                System.err.println("Se produjo un error al abrir o escribir en el fichero " + textoBat);
            }

            try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE empleados SET anyos = ? WHERE dni = ?
              """)) {
                statement.setInt(1, 1);
                statement.setString(2, empleadosList.get(1).dni);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE empleados SET categoria = ? WHERE dni = ?
              """)) {
                statement.setInt(1, 9);
                statement.setString(2, empleadosList.get(0).dni);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE nominas SET sueldo = ? WHERE dni = ?
              """)) {
                for (Empleado each: empleadosList) {
                    statement.setInt(1, Nomina.sueldo(each));
                    statement.setString(2, each.dni);
                    statement.executeUpdate();
                }
            }

        } catch (DatosNoCorrectosException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Muestra en pantalla todos los datos recogidos.
     * @param emp
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
                + emp.getCategoria() + "\n"
                + Nomina.sueldo(emp) + "\n";
    }

}