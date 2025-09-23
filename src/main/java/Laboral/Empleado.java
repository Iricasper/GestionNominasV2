package Laboral;

/**
 * Amplía las características de la clase {@link Persona}, incluyendo la antigüedad en la empresa y la categoría que ocupa.
 */
public class Empleado extends Persona {
    // Propiedades
    private int categoria;
    public int anyos;

    // Constructores
    /**
     *
     * @param nombre Se obtiene de la clase {@link Persona}
     * @param dni Se obtiene de la clase {@link Persona}
     * @param sexo Un solo carácter
     * @param categoria Número entre 1 y 10 incluidos.
     * @param anyos Número positivo.
     * @throws DatosNoCorrectosException Error al introducir años negativos o categoría fuera de los límites.
     */
    public Empleado(String nombre, String dni, char sexo, int categoria, int anyos) throws DatosNoCorrectosException {
        super(nombre, dni, sexo);
        if (categoria >=1 && categoria <= 10 && anyos >= 0) {
            this.categoria = categoria;
            this.anyos = anyos;
        } else {
            throw new DatosNoCorrectosException("Datos no correctos.");
        }
    }

    /**
     * Se utiliza en caso de trabajadores nuevos.
     * @param nombre
     * @param dni
     * @param sexo
     */
    public Empleado(String nombre, String dni, char sexo) {
        super(nombre, dni, sexo);
        this.categoria = 1;
        this.anyos = 0;
    }

    // Métodos

    /**
     * Permite cambiar la categoría por otra distinta, siempre que se encuentre en los valores esperados.
     * @param categoriaNueva
     */
    public void setCategoria(int categoriaNueva) {
        if (categoriaNueva >=1 && categoriaNueva <= 10) {
            categoria = categoriaNueva;
        }
    }

    public int getCategoria() {
        return categoria;
    }

    /**
     * Aumenta en uno los años de experiencia.
     */
    public void incrAnyo() {
        anyos++;
    }

    /**
     * Devuelve los datos de la función {@link super.imprimePersona} y añade los nuevos atributos.
     */
    public void imprime() {
        super.imprimePersona();
        System.out.println("Antigüedad: " + anyos);
        System.out.println("Categoría: " + getCategoria());
    }
}
