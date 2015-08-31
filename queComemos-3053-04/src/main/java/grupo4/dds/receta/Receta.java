package grupo4.dds.receta;

import grupo4.dds.excepciones.NoSePuedeModificarLaReceta;
import grupo4.dds.persistor.MongoPersistor;
import grupo4.dds.usuario.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import queComemos.entrega3.dominio.Dificultad;

@Entity
public class Receta {
	
	@Id
	private ObjectId id;
	
    @Reference
	protected Usuario creador;

	/* Encabezado de la receta */
    @Embedded
	protected EncabezadoDeReceta encabezado = new EncabezadoDeReceta();

	/* Detalle de la receta */
    @Embedded
	protected List<Ingrediente> ingredientes = new ArrayList<Ingrediente>();
	protected List<Ingrediente> condimentos = new ArrayList<Ingrediente>();
	protected List<Receta> subrecetas = new ArrayList<Receta>();
	protected String preparacion;

	/* Constructores */
	
	public Receta(){	}
	
	public static Receta crearNueva() {
		return crearNueva(null, null, null);
	}
	
	public static Receta crearNueva(Usuario creador, EncabezadoDeReceta encabezado, String preparacion) {
		return crearNueva(creador, encabezado, null, null, null, preparacion);
	}

	public static Receta crearNueva(Usuario creador, EncabezadoDeReceta encabezado, List<Ingrediente> ingredientes,
			List<Ingrediente> condimentos, List<Receta> subrecetas, String preparacion) {

		Receta self = new Receta(creador, encabezado, ingredientes, condimentos, subrecetas, preparacion);
		
		if(creador != null) {
			//TODO: hacer validas recetas en los test para agregar esta funcion
			//creador.agregarReceta(self);
			MongoPersistor.get().dataStore().save(creador);			
		}
		
		RepositorioDeRecetas.get().agregarReceta(self);	
		MongoPersistor.get().dataStore().save(self);
		
		return self;
	}

	protected Receta(Usuario creador, EncabezadoDeReceta encabezado, List<Ingrediente> ingredientes,
			List<Ingrediente> condimentos, List<Receta> subrecetas, String preparacion) {

		this.creador = creador;
		this.encabezado = encabezado != null ? encabezado
				: new EncabezadoDeReceta();
		this.preparacion = preparacion;
		this.ingredientes = ingredientes != null ? ingredientes
				: new ArrayList<>();
		this.condimentos = condimentos != null ? condimentos
				: new ArrayList<>();
		this.subrecetas = subrecetas != null ? subrecetas
				: new ArrayList<Receta>();
	}

	/* Servicios */

	public boolean esValida() {
		int totalCalorias = getTotalCalorias();
		return !ingredientes.isEmpty() && 10 <= totalCalorias && totalCalorias <= 5000;
	}

	public boolean tieneIngrediente(String nombreIngrediente) {
		return tiene(getIngredientes(), nombreIngrediente);
	}

	public boolean tieneCondimento(String nombreCondimento) {
		return tiene(getCondimentos(), nombreCondimento);
	}

	public float cantidadCondimento(String nombreCondimento) {
		int index = getCondimentos().indexOf(new Ingrediente(nombreCondimento));
		return getCondimentos().get(index).getCantidad();
	}

	public boolean puedeSerVistaPor(Usuario usuario) {
		if(creador == null)
			return false;
		return creador.equals(usuario);
	}

	public boolean puedeSerAgregadaPor(Usuario usuario) {
		return puedeSerVistaPor(usuario);
	}

	public void modificarReceta(Usuario usuario, EncabezadoDeReceta encabezado, List<Ingrediente> ingredientes,
			List<Ingrediente> condimentos, String preparacion, List<Receta> subrecetas) {

		if (!usuario.puedeModificar(this))
			throw new NoSePuedeModificarLaReceta();

		this.encabezado = encabezado;
		this.ingredientes = ingredientes != null ? ingredientes
				: new ArrayList<>();
		this.condimentos = condimentos != null ? condimentos
				: new ArrayList<>();
		this.subrecetas = subrecetas != null ? subrecetas
				: new ArrayList<Receta>();
		this.preparacion = preparacion;

	}

	public String getPreparacion() {
		if (preparacion == null & subrecetas.isEmpty())
			return "";

		String preparacionDeSubrecetas = subrecetas == null ? null : subrecetas
				.stream().map(Receta::getPreparacion)
				.collect(Collectors.joining(""));

		if (preparacionDeSubrecetas == null)
			return preparacion;

		return String.join("", preparacion, preparacionDeSubrecetas);
	}

	public List<Ingrediente> getIngredientes() {
		return getConSubrecetas((Receta receta) -> { return receta.ingredientes;}, ingredientes);
	}

	public List<Ingrediente> getCondimentos() {
		return getConSubrecetas((Receta receta) -> {return receta.condimentos;}, condimentos);
	}

	public boolean contieneAlguna(List<Ingrediente> comidas) {
		return !noContieneNinguna(comidas);
	}

	public boolean noContieneNinguna(List<Ingrediente> comidas) {
		return Collections.disjoint(getIngredientes(), comidas);
	}

	public boolean tieneCarne() {
		return getIngredientes().stream().anyMatch(i -> i.esCarne());
	}

	public boolean esDificil() {
		return Dificultad.DIFICIL.equals(encabezado.dificultad);
	}
	
	@Override
	public String toString() {
		return getNombreDelPlato();
	}

	/* Servicios privados */

	private boolean tiene(List<Ingrediente> lista, String nombre) {
		return lista.contains(new Ingrediente(nombre));
	}

	// TODO mejorar para llegar a algo más cercano a fold/reduct
	private List<Ingrediente> getConSubrecetas(Function<Receta, List<Ingrediente>> f, List<Ingrediente> seed) {

		List<Ingrediente> acum = new ArrayList<Ingrediente>(seed);

		for (Receta elem : subrecetas) 
			acum.addAll(f.apply(elem));

		return acum;
	}

	/* Accessors and Mutators */

	public int getTotalCalorias() {
		return encabezado.getTotalCalorias();
	}

	public void setTotalCalorias(int totalCalorias) {
		encabezado.setTotalCalorias(totalCalorias);
	}

	public void agregarIngrediente(Ingrediente unIngrediente) {
		ingredientes.add(unIngrediente);
	}

	public void agregarCondimento(Ingrediente unCondimento) {
		condimentos.add(unCondimento);
	}

	public void agregarSubreceta(Receta subreceta) {
		subrecetas.add(subreceta);
	}

	public String getNombreDelPlato() {
		return encabezado.getNombreDelPlato();
	}

	public Temporada getTemporada() {
		return encabezado.getTemporada();
	}

	public void setCreador(Usuario creador) {
		this.creador = creador;
	}

	public void setEncabezado(EncabezadoDeReceta encabezado) {
		this.encabezado = encabezado;
	}

	public void setIngredientes(List<Ingrediente> ingredientes) {
		this.ingredientes = ingredientes;
	}

	public void setCondimentos(List<Ingrediente> condimentos) {
		this.condimentos = condimentos;
	}

	public void setSubrecetas(List<Receta> subrecetas) {
		this.subrecetas = subrecetas;
	}

	public void setPreparacion(String preparacion) {
		this.preparacion = preparacion;
	}

	public EncabezadoDeReceta getEncabezado() {
		return encabezado;
	}

	public Usuario getCreador() {
		return creador;
	}

	public List<Receta> getSubrecetas() {
		return subrecetas;
	}
		
}
