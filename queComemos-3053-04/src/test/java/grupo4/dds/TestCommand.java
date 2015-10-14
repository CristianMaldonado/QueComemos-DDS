package grupo4.dds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import grupo4.dds.monitores.asincronicos.EnvioPorMail;
import grupo4.dds.monitores.asincronicos.LoggeoConsultas;
import grupo4.dds.monitores.asincronicos.MarcarFavoritas;
import grupo4.dds.monitores.asincronicos.mail.EMailer;
import grupo4.dds.monitores.asincronicos.mail.Mail;
import grupo4.dds.monitores.asincronicos.mail.MailSender;
import grupo4.dds.receta.Receta;
import grupo4.dds.receta.busqueda.filtros.Filtro;
import grupo4.dds.receta.busqueda.filtros.FiltroExcesoCalorias;
import grupo4.dds.receta.busqueda.filtros.FiltroNoLeGusta;
import grupo4.dds.repositorios.RepositorioDeTareas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class TestCommand extends BaseTest {
		
	private RepositorioDeTareas repoTareas = RepositorioDeTareas.instance();
	
	private List<Receta> resultadoConsulta = new ArrayList<>();
	private List<Receta> resultadoCon101Recetas = new ArrayList<>();
	private List<Filtro> parametros =  new ArrayList<>();
	
	private MockLogger mockLogger;
	
	private class MockLogger extends Logger {
		boolean seRegistroElLog = false;
		
		protected MockLogger(String name) {	super(name); }
		public boolean isInfoEnabled() { return true; }
		public void info(Object logMessage) { seRegistroElLog = true; }
	}

	private class MockMailSender implements MailSender {
		Mail mail;
		
		public void enviarMail(Mail mail) { this.mail = mail; }
		public Mail ultimoMail() { return mail; }
	}
	
	@Before
	public void setUp() {
		
		for(int i = 0; i<110;i++) {
			resultadoCon101Recetas.add(milanesa);
		}
		
		mockLogger = new MockLogger(null);		
		resultadoConsulta = Arrays.asList(pollo, pure, salmon, lomito, coliflor);
		parametros = Arrays.asList(new FiltroNoLeGusta(), new FiltroExcesoCalorias());
	}

	@Test
	public void testMarcarComoFavoritaATodasLasRecetas() {
		
		federicoHipper.setMarcaFavorita(true);
		new MarcarFavoritas().notificarConsulta(federicoHipper, resultadoConsulta, null);
		repoTareas.ejecutarTodas();
		
		assertTrue(federicoHipper.getHistorial().containsAll(resultadoConsulta));
	}
	
	@Test
	public void testNoSeMarcanComoFavoritasSiElUsuarioNoTieneLaOpcionActivada() {

		new MarcarFavoritas().notificarConsulta(federicoHipper, resultadoConsulta, null);
		
		repoTareas.ejecutarTodas();
		
		assertTrue(federicoHipper.getHistorial().isEmpty());
	}
	
	@Test
	public void testNoHayEfectoEnMarcarUnaRecetaQueYaEstaComoFavorita() {
		
		federicoHipper.marcarFavorita(pollo);
		
		assertEqualsList(Arrays.asList(pollo), federicoHipper.getHistorial());
		
		new MarcarFavoritas().notificarConsulta(federicoHipper, resultadoConsulta, null);
		federicoHipper.setMarcaFavorita(true);
		repoTareas.ejecutarTodas();
		
		assertEqualsList(resultadoConsulta, federicoHipper.getHistorial());
	}
	
	@Test
	public void testAgregarEnvioDeMailEnMailPendientesEnRepositorioDeReceta() {
		
		EnvioPorMail envioPorMail = new EnvioPorMail();
		envioPorMail.suscribir(federicoHipper);
		envioPorMail.notificarConsulta(federicoHipper, resultadoConsulta, parametros);
		
		MailSender mockMailSender = new MockMailSender();
		EMailer.setMailSender(mockMailSender);
		
		assertNull(mockMailSender.ultimoMail());
		
		repoTareas.ejecutarTodas();
		
		Mail expected = new Mail(federicoHipper, resultadoConsulta, parametros);
		assertEquals(expected.crearMensaje(), mockMailSender.ultimoMail().crearMensaje());
	}	

	@Test
	public void testNoSeEnvianMailsAUsuarioNoSuscriptos() {
		
		EnvioPorMail envioPorMail = new EnvioPorMail();
		envioPorMail.notificarConsulta(fecheSena, resultadoConsulta, parametros);
		
		MailSender mockMailSender = new MockMailSender();
		EMailer.setMailSender(mockMailSender);
		
		repoTareas.ejecutarTodas();
		
		assertNull(mockMailSender.ultimoMail());
	}	

/*
	@Test
	public void testEnviarMailEnMailSender(){
		Mail otroMail = new Mail();
		mailSender.enviarMail(otroMail);
		verify(mailSender, times(1)).enviarMail(any(Mail.class));
	}
	
	@Test 
	public void testEnviarAMailSender(){
		Mail otroMail = Mockito.mock(Mail.class);
		otroMail.enviarMail(mailSender);
		validateMockitoUsage();
	}
*/
	
	@Test
	public void testNoLoggeaConsultasConMenosDe100Resultados(){
				
		LoggeoConsultas loggeoConsultas = new LoggeoConsultas();
		loggeoConsultas.setLogger(mockLogger);
		loggeoConsultas.notificarConsulta(federicoHipper, resultadoCon101Recetas.subList(0, 98), null);		
		
		assertFalse(mockLogger.seRegistroElLog);
		
		repoTareas.ejecutarTodas();
		
		assertFalse(mockLogger.seRegistroElLog);
	}
	
	@Test
	public void testLoggeaConsultasConMasDe100Resultados(){

		LoggeoConsultas loggeoConsultas = new LoggeoConsultas();
		loggeoConsultas.setLogger(mockLogger);
		loggeoConsultas.notificarConsulta(federicoHipper, resultadoCon101Recetas, null);		
		
		assertFalse(mockLogger.seRegistroElLog);
		
		repoTareas.ejecutarTodas();
		
		assertTrue(mockLogger.seRegistroElLog);
	}
    
}
