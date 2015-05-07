package grupo4.dds.receta;

import grupo4.dds.usuario.Usuario;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Receta {

	protected Usuario creador;

	/* Encabezado de la receta */
	protected EncabezadoDeReceta encabezado = new EncabezadoDeReceta();

	/* Detalle de la receta */
	protected HashMap<String, Float> ingredientes = new HashMap<String, Float>();
	protected HashMap<String, Float> condimentos = new HashMap<String, Float>();
	protected Collection<Receta> subrecetas;
	protected String preparacion;

	/* Constructores */
	
	public Receta(){}

	public Receta(Usuario creador) {
		this.creador = creador;
	}// Creado para testear por ahora

	protected Receta(Usuario creador, EncabezadoDeReceta encabezado, String preparacion) {
		this.creador = creador;
		this.encabezado = encabezado;
		this.preparacion = preparacion;
	}

	static public Receta crearNueva(Usuario creador,
			EncabezadoDeReceta encabezado, String preparacion) {

		Receta nuevaReceta = new Receta(creador, encabezado, preparacion);
		creador.agregarReceta(nuevaReceta);

		return nuevaReceta;
	}

	/* Servicios */

	public boolean esValida() {
		int totalCalorias = getTotalCalorias();
		return !ingredientes.isEmpty() && 10 <= totalCalorias
				&& totalCalorias <= 5000;
	}

	public boolean tieneIngrediente(String unIngrediente) {
		return this.ingredientes.containsKey(unIngrediente);
	}

	public boolean tieneCondimento(String condimento) {
		return this.condimentos.containsKey(condimento);
	}

	public Float cantidadCondimento(String unCondimento) {
		return this.condimentos.get(unCondimento);
	}

	public boolean puedeSerVistaPor(Usuario usuario) {
		return creador.equals(usuario);
	}

	public boolean puedeSerModificadaPor(Usuario usuario) {
		return puedeSerVistaPor(usuario);
	}

	public void modificarReceta(Usuario usuario, EncabezadoDeReceta encabezado,
			HashMap<String, Float> ingredientes,
			HashMap<String, Float> condimentos, String preparacion,
			Collection<Receta> subRecetas) throws NoTienePermisoParaModificar {

		if (!puedeSerModificadaPor(usuario))
			throw new NoTienePermisoParaModificar();

		this.encabezado = encabezado;
		this.ingredientes = ingredientes;
		this.condimentos = condimentos;
		this.subrecetas = subRecetas;
		this.preparacion = preparacion;
	}

	public Collection<String> getNombreIngredientes() {
		return getConSubrecetas((Receta receta) -> {
			return receta.ingredientes.keySet();
		}, ingredientes.keySet());
	}

	public String getPreparacion() {

		String preparacionDeSubrecetas = subrecetas.stream()
				.map(Receta::getPreparacion).collect(Collectors.joining("\n"));

		return String.join("\n", preparacion, preparacionDeSubrecetas);
	}

	public Collection<String> getNombreCondimentos() {
		return getConSubrecetas((Receta receta) -> {
			return receta.condimentos.keySet();
		}, condimentos.keySet());
	}

	/* Servicios Internos */
	//TODO mejorar para llegar a algo más cercano a fold/reduct
	private Collection<String> getConSubrecetas(
			Function<Receta, Collection<String>> f, Collection<String> seed) {

		class CollectionMerger implements BinaryOperator<Collection<String>> {

			@Override
			public Collection<String> apply(Collection<String> t,
					Collection<String> u) {
				Collection<String> a = new HashSet<String>();

				a.addAll(t);
				a.addAll(u);

				return a;
			}

		}

		CollectionMerger merger = new CollectionMerger();

		if (subrecetas == null)
			return seed;

		Collection<String> coleccionesDeSubrecetas = subrecetas.stream().map(f)
				.collect(Collectors.reducing(merger)).get();

		return merger.apply(seed, coleccionesDeSubrecetas);

	}

	/* Accessors and Mutators */

	public int getTotalCalorias() {
		return encabezado.getTotalCalorias();
	}

	public void setTotalCalorias(int totalCalorias) {
		encabezado.setTotalCalorias(totalCalorias);
	}

	public void agregarIngrediente(String key, Float value) {
		ingredientes.put(key, value);
	}

	public void agregarCondimento(String key, Float value) {
		condimentos.put(key, value);
	}
	
	public void agregarSubreceta(Receta subreceta) {
		subrecetas.add(subreceta);
	}

	public void agregarIngredientes(HashMap<String, Float> ingredientes) {
		ingredientes.putAll(ingredientes);
	}

	public void agregarCondimentos(HashMap<String, Float> condimentos) {
		condimentos.putAll(condimentos);
	}
	
	public void agregarSubrecetas(Collection<Receta> subrecetas) {
		subrecetas.addAll(subrecetas);
	}
	
	public String getNombreDelPlato() {
		return encabezado.getNombreDelPlato();
	}

	public Temporada getTemporada() {
		return encabezado.getTemporada();
	}

	public String getDificultad() {
		return encabezado.getDificultad();
	}

}
