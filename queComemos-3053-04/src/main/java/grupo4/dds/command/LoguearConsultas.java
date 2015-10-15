package grupo4.dds.command;

import grupo4.dds.receta.Receta;
import grupo4.dds.usuario.Usuario;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoguearConsultas implements Command{

	private Logger logger = Logger.getLogger(LoguearConsultas.class);
	private List<Receta> consultas;

	public LoguearConsultas(List<Receta> consultas){
		this.consultas = consultas;
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void ejecutar(Usuario usuario) {
		PropertyConfigurator.configure("log4j.properties");
		if (consultas.size() > 100){
			if (logger.isInfoEnabled())
				logger.info("Consultas Con Mas De 100 Resultados");	

		}
	}
	
}