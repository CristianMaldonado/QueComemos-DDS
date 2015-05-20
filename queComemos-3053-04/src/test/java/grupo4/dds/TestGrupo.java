package grupo4.dds;

import static org.junit.Assert.*;
import grupo4.dds.receta.Receta;
import grupo4.dds.usuario.Grupo;
import grupo4.dds.usuario.Ingrediente;
import grupo4.dds.usuario.Usuario;
import grupo4.dds.usuario.condicion.Condicion;
import grupo4.dds.usuario.condicion.Diabetico;
import grupo4.dds.usuario.condicion.Vegano;

import org.junit.Before;
import org.junit.Test;

public class TestGrupo {
	
	private Grupo grupo;
	private Usuario fecheSena;
	private Usuario elPanadero;
	private Receta receta;
	private Ingrediente carne;
	private Ingrediente fruta;
	private Ingrediente chori;
	private Ingrediente azucar;
	private Condicion vegano;
	private Condicion diabetico;

	@Before
	public void setUp() {
		grupo = new Grupo("grosos");
		fecheSena = new Usuario();
		elPanadero = new Usuario();
		fruta = new Ingrediente("fruta", 0f);
		chori = new Ingrediente("chori", 0f);
		azucar = new Ingrediente("azucar", 90f);
		receta = new Receta();
		receta.setTotalCalorias(4500);
		vegano = new Vegano();
		diabetico = new Diabetico();
		
		fecheSena.agregarCondicion(vegano);
		elPanadero.agregarCondicion(diabetico);
		
		grupo.agregarPalabrasClave(chori);
		
	}

	@Test
	public void testAgregarUsuarioAGrupo() {
		grupo.agregarUsuario(fecheSena);
	     assertTrue(grupo.getUsuarios().contains(fecheSena));
		
	}
	
	@Test
	public void testAgregarPalabraClave(){
		
		grupo.agregarPalabrasClave(carne);
		assertTrue(grupo.getPalabrasClave().contains(carne));
	}
	
	@Test
	public void testNoSePuedeSugerirRecetaAGrupoGrosos(){
	
		receta.agregarIngrediente(carne);
		
		fecheSena.agregarPreferenciaAlimenticia(fruta);
	
		assertFalse(grupo.sugerirReceta(receta));
	}
	
	@Test
	public void testSePuedeSugerirRecetaAGrupoGrosos(){
		
		receta.agregarIngrediente(azucar);
		receta.agregarIngrediente(fruta);
		
		fecheSena.agregarPreferenciaAlimenticia(fruta);
		grupo.agregarPalabrasClave(azucar);
			
		assertTrue(grupo.sugerirReceta(receta));
	}
	
	

}