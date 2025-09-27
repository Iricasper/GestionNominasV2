package Laboral;

/**
 * Clase de utilidad para calcular el sueldo final de los empleaods.
 */
public class Nomina {
    private static final int[] SUELDO_BASE = {50000, 70000, 90000, 110000, 130000,
            150000, 170000, 190000, 210000, 230000};

    /**
     *
     * @param emp Empleado a calcular
     * @return Devuelve el sueldo final
     */
    public static int sueldo(Empleado emp) {
        return SUELDO_BASE[emp.getCategoria()-1]+5000*emp.anyos;
    }
}
