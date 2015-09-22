package grupo4.dds.usuario;

import grupo4.dds.receta.Ingrediente;
import grupo4.dds.receta.Receta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Grupos")
public class GrupoUsuarios {
	
	@Id
	@GeneratedValue
	@Column(name = "id_grupo")
	private long id;
	
	private String nombre;
	@OneToMany
	@JoinTable(name = "Usuarios_Grupos")
	private Set<Usuario> usuarios = new HashSet<>();
	@OneToMany
	@JoinTable(name = "Grupos_Ingredientes")
	private List<Ingrediente> preferenciasAlimenticias = new ArrayList<>();
	
	
	/* Constructores */
	
	public GrupoUsuarios(String nombre) {
		this.nombre = nombre;
	}
	
	/* Servicios */
	
	public boolean puedeSugerirse(Receta receta) {
		return receta.contieneAlguna(preferenciasAlimenticias) && esAdecuadaParaMiembros(receta);
	}

	public boolean puedeVer(Receta receta) {
		return usuarios.stream().anyMatch(u -> receta.puedeSerVistaPor(u));
	}

	public boolean esMiembro(Usuario usuario) {
		return usuarios.contains(usuario);
	}
	
	/* Servicios privados */
	
	private boolean esAdecuadaParaMiembros(Receta receta) {
		return usuarios.stream().allMatch(u -> u.esAdecuada(receta));
	}
	
	/* Accesors and Mutators */
	
	public void agregarUsuario(Usuario usuario) {
		usuarios.add(usuario);
		if (!usuario.perteneceA(this))
			usuario.agregarGrupo(this);
	}
	
	public void agregarPreferenciaAlimenticia(Ingrediente comida) {
		preferenciasAlimenticias.add(comida);
	}
	
	public String getNombre() {
		return nombre;
	}
	
}
