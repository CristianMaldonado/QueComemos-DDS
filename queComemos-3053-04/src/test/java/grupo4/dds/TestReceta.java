package grupo4.dds;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import grupo4.dds.excepciones.NoSePuedeModificarLaReceta;
import grupo4.dds.receta.EncabezadoDeReceta;
import grupo4.dds.receta.Receta;
import grupo4.dds.receta.RecetaPublica;
import grupo4.dds.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestReceta {
	
	private Receta receta;
		
	@Rule public ExpectedException expectedExcetption = ExpectedException.none();
	
	@Before
	public void setUp() {

	}
	
	/* Test: @esValida/0 */

	@Test
	public void testNoEsValidaUnaRecetaSinIngredientes() {
		receta = new Receta();
		
		receta.setTotalCalorias(4500);
		assertFalse(receta.esValida());
	}
	
	@Test
	public void testEsValidaUnaRecetaConIngredientesY4500Calorias() {
		receta = new Receta();
		
		receta.agregarIngrediente("carne", 0f);
		receta.setTotalCalorias(4500);
		assertTrue(receta.esValida());
	}
	
	/* Test: @modificarReceta/6 */
	
	@Test
	public void testPuedeModificarseUnaRecetaConElUsuarioQueLaCreo() throws NoSePuedeModificarLaReceta {
		Usuario usuario = new Usuario();		
		receta = new Receta(usuario, new EncabezadoDeReceta(), "Preparación antes de modificar");
		
		EncabezadoDeReceta encabezado = new EncabezadoDeReceta();
		encabezado.setTotalCalorias(4500);
		
		HashMap<String, Float> ingredientes = new HashMap<String, Float>();
		ingredientes.put("frutas", 0f);
				
		receta.modificarReceta(usuario, encabezado, ingredientes, null, "Preparación después de modificar", null);
		assertEquals(receta.getPreparacion(),"Preparación después de modificar");
	}
	
	@Test
	public void testNoPuedeModificarseUnaRecetaConUnUsuarioQueNoLaCreo() throws NoSePuedeModificarLaReceta {
		expectedExcetption.expect(NoSePuedeModificarLaReceta.class);
		
		Usuario usuario = new Usuario();		
		receta = new Receta(usuario, new EncabezadoDeReceta(), "Preparación antes de modificar");
		
		EncabezadoDeReceta encabezado = new EncabezadoDeReceta();
		encabezado.setTotalCalorias(4500);
		
		HashMap<String, Float> ingredientes = new HashMap<String, Float>();
		ingredientes.put("frutas", 0f);
				
		receta.modificarReceta(new Usuario(), encabezado, ingredientes, null, "Preparación después de modificar", null);
		assertEquals(receta.getPreparacion(), "Preparación después de modificar");
		}
	
	@Test
	public void testAlModificarUnaRecetaPublicaSeGeneraUnaNuevaRecetaConLasModificaciones() throws NoSePuedeModificarLaReceta {
		Usuario usuario = new Usuario();
		RecetaPublica recetaPublica = new RecetaPublica(null, "Preparación antes de modificar");
		
		EncabezadoDeReceta encabezado = new EncabezadoDeReceta();
		encabezado.setTotalCalorias(4500);
		
		HashMap<String, Float> ingredientes = new HashMap<String, Float>();
		ingredientes.put("frutas", 0f);

		usuario.modificarReceta(recetaPublica, encabezado, ingredientes, null, "Preparación después de modificar", null);

		assertEquals(recetaPublica.getPreparacion(), "Preparación antes de modificar");
		assertEquals(usuario.recetaMasReciente().getPreparacion(), "Preparación después de modificar");
	}
	
	/* Test: @getPreparacion */
		
	@Test
	public void testLaPreparacionDeUnaRecetaSinSubrecetasEsLaSuya() {
		receta = new Receta(null, null, "Preparación propia");
		assertEquals(receta.getPreparacion(), "Preparación propia");
	}
	
	@Test
	public void testLaPreparacionDeUnaRecetaEsLaSuyaYlaDeSusSubrecetas() {
		receta = new Receta(null, null, "Preparación propia\n");
		receta.agregarSubreceta(new Receta(null, null, "Preparación subreceta 1\n"));
		receta.agregarSubreceta(new Receta(null, null, null));
		receta.agregarSubreceta(new Receta(null, null, "Preparación subreceta 2\n"));
		
		assertEquals(receta.getPreparacion(), "Preparación propia\nPreparación subreceta 1\nPreparación subreceta 2\n");
	}
	
	/* Test: @getNombreIngredientes */
	
	@Test
	public void testLosIngredientesDeUnaRecetaSinSubrecetasSonLosSuyos() {
		receta = new Receta(null, null, null);
		receta.agregarIngrediente("carne", 0f);
		receta.agregarIngrediente("pollo", 0f);
		
		List<String> expected = new ArrayList<String>();
		expected.add("carne");
		expected.add("pollo");
		
		assertEquals(receta.getNombreIngredientes(), expected);
	}
	
	@Test
	public void testLosIngredientesDeUnaRecetaSonLosSuyosYLosDeSusSubrecetas() {
		receta = new Receta(null, null, null);
		receta.agregarIngrediente("carne", 0f);
		receta.agregarIngrediente("pollo", 0f);
		
		List<String> expected = new ArrayList<String>();
		expected.add("carne");
		expected.add("pollo");
		expected.add("chivito");
		expected.add("chori");
		
		Receta sub1 = new Receta(null, null, null);
		sub1.agregarIngrediente("chivito", 0f);
		Receta sub2 = new Receta(null, null, null);
		sub2.agregarIngrediente("chori", 0f);
		
		receta.agregarSubreceta(sub1);
		receta.agregarSubreceta(sub2);
		
		assertEquals(receta.getNombreIngredientes(), expected);
	}
	
	/* Test: @getNombreCondimentos */
	
	@Test
	public void testLosCondimentosDeUnaRecetaSinSubrecetasSonLosSuyos() {
		receta = new Receta(null, null, null);
		receta.agregarCondimento("caldo", 0f);
		receta.agregarCondimento("sal", 0f);
		
		List<String> expected = new ArrayList<String>();
		expected.add("caldo");
		expected.add("sal");
		
		assertEquals(receta.getNombreCondimentos(), expected);
	}
	
	@Test
	public void testLosCondimentosDeUnaRecetaSonLosSuyosYLosDeSusSubrecetas() {
		receta = new Receta(null, null, null);
		receta.agregarCondimento("caldo", 0f);
		receta.agregarCondimento("sal", 0f);
		
		
		List<String> expected = new ArrayList<String>();
		expected.add("caldo");
		expected.add("sal");
		expected.add("pimienta");
		expected.add("azucar");
		
		Receta sub1 = new Receta(null, null, null);
		sub1.agregarCondimento("pimienta", 0f);
		Receta sub2 = new Receta(null, null, null);
		sub2.agregarCondimento("azucar", 0f);
		
		receta.agregarSubreceta(sub1);
		receta.agregarSubreceta(sub2);
		
		assertEquals(receta.getNombreCondimentos(), expected);
	
	}
	
}