package common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Animal implements Serializable {
	
	private int id;
	private String nombre;
	private String tipo;
	private String edad_string;
	private int edad_int;
	private String estado;
	private String genero;
	private String desc_fisica;
	private String desc_personalidad;
	private String desc_adicional;
	private boolean is_esterilizado;
	private boolean is_vacunas;
	private String image;
	private String equipo;
	private String region;
	private String comuna;
	private String url;
	
	public Animal (
			int id, 
			String nombre, 
			String tipo, 
			String edad_string, 
			int edad_int,
			String estado,
			String genero,
			String desc_fisica,
			String desc_personalidad,
			String desc_adicional,
			int esterelizado,
			int vacunas,
			String image,
			String equipo,
			String region,
			String comuna,
			String url
			) {
		this.id = id;
		this.nombre = nombre;
		this.tipo = tipo;
		this.edad_string = edad_string;
		this.edad_int = edad_int;
		this.estado = estado;
		this.genero = genero;
		this.desc_fisica = desc_fisica;
		this.desc_personalidad = desc_personalidad;
		this.desc_adicional = desc_adicional;
		this.is_esterilizado = (esterelizado == 1);
		this.is_vacunas = (vacunas == 1);
		this.image = image;
		this.equipo = equipo;
		this.region = region;
		this.comuna = comuna;
		this.url = url;
	}
	
	// ====================
	// ID
	// ====================
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    // ====================

	// ====================
	// Nombre
	// ====================
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    // ====================

	// ====================
	// Tipo
	// ====================
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    // ====================

	// ====================
	// Edad String
	// ====================
    public String getEdad_string() {
        return edad_string;
    }

    public void setEdad_string(String edad_string) {
        this.edad_string = edad_string;
    }
    // ====================

	// ====================
	// Edad int
	// ====================
    public int getEdad_int() {
        return edad_int;
    }

    public void setEdad_int(int edad_int) {
        this.edad_int = edad_int;
    }
    // ====================

	// ====================
	// Estado
	// ====================
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    // ====================

	// ====================
	// Genero
	// ====================
    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
    // ====================

	// ====================
	// Descripcion fisica
	// ====================
    public String getDesc_fisica() {
        return desc_fisica;
    }

    public void setDesc_fisica(String desc_fisica) {
        this.desc_fisica = desc_fisica;
    }
    // ====================

	// ====================
	// Descripcion personalidad
	// ====================
    public String getDesc_personalidad() {
        return desc_personalidad;
    }

    public void setDesc_personalidad(String desc_personalidad) {
        this.desc_personalidad = desc_personalidad;
    }
    // ====================

	// ====================
	// Descripcion adicional
	// ====================
    public String getDesc_adicional() {
        return desc_adicional;
    }

    public void setDesc_adicional(String desc_adicional) {
        this.desc_adicional = desc_adicional;
    }
    // ====================

	// ====================
	// esterelizado
	// ====================
    public boolean isIs_esterilizado() {
        return is_esterilizado;
    }

    public void setIs_esterilizado(boolean is_esterilizado) {
    	this.is_esterilizado = is_esterilizado;
    }
    // ====================

	// ====================
	// Vacunas
	// ====================
    public boolean isIs_vacunas() {
        return is_vacunas;
    }

    public void setIs_vacunas(boolean is_vacunas) {
    	this.is_vacunas = is_vacunas;
    }
    // ====================

	// ====================
	// Imagen
	// ====================
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    // ====================

	// ====================
	// Equipo
	// ====================
    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }
    // ====================

	// ====================
	// Region
	// ====================
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
    // ====================

	// ====================
	// Comuna
	// ====================
    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }
    // ====================

	// ====================
	// Url
	// ====================
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    // ====================
}