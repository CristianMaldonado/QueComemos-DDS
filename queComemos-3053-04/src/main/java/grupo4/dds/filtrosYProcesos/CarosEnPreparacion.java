package grupo4.dds.filtrosYProcesos;

import grupo4.dds.receta.Receta;
import grupo4.dds.receta.RepositorioDeRecetas;
import grupo4.dds.usuario.Ingrediente;
import grupo4.dds.usuario.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CarosEnPreparacion implements Filtro {

	private List<Ingrediente> ingredientesCaros = new ArrayList<>();
	
	public void filtrar(Usuario usuario, RepositorioDeRecetas repoRecetas) {
		
		List<Receta> filtroDeReceta = new ArrayList<>();
		
		filtroDeReceta = repoRecetas.listarRecetasParaUnUsuario(usuario).stream().filter(r -> r.interseccion(r.getIngredientes(),(this.ingredientesCaros)).isEmpty()).collect(Collectors.toList());
		 
		repoRecetas.actualizarConsultaDeRecetas(filtroDeReceta);
	}
	
	public void setIngredienteCaro(Ingrediente unIngrediente) {
		this.ingredientesCaros.add(unIngrediente);
	}

}