package Laboral;

/**
 * Clase básica que contiene los atributos de una persona y cambios de estado básicos.
 */
public class Persona {
    // Propiedades
    public String nombre;
    public String dni;
    public char sexo;

    // Constructores
    /**
     *
     * @param nombre
     * @param dni
     * @param sexo
     */
    public Persona(String nombre, String dni, char sexo) {
        this.nombre = nombre;
        this.dni = dni;
        this.sexo = sexo;
    }

    /**
     *
     * @param nombre
     * @param sexo
     */
    public Persona(String nombre, char sexo) {
        this.nombre = nombre;
        this.sexo = sexo;
    }

    //Métodos
    /**
     * Modifica la propiedad dni de la persona.
     * @param dniNuevo Patrón de 8 números consecutivos y una letra en mayúscula.
     */
    public void setDni(String dniNuevo) {
        dni = dniNuevo;
    }
    /**
     * Muestra en pantalla las propiedades "nombre" y "dni" del usuario.
     */
    public void imprimePersona() {
        System.out.println("Nombre: " + nombre);
        System.out.println("DNI: " + dni);
    }
}
