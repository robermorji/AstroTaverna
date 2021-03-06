package org.purl.wf4ever.astrotaverna.aladin;

//import static org.junit.Assert.assertTrue;

//import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
//import java.io.InputStreamReader;
//import java.util.Map;

import net.sf.taverna.raven.prelauncher.ClassLocation;
import net.sf.taverna.t2.annotation.AnnotationAssertion;

import org.apache.log4j.Logger;
import org.purl.wf4ever.astrotaverna.utils.NoExitSecurityManager_;
import org.purl.wf4ever.astrotaverna.utils.StreamReaderAsync;

import cds.aladin.Aladin;

//import cds.aladin.Aladin;


/**
 * It runs Aladin scripts and macros. 
 * At the time of this class the version is Aladin 7.5. 
 * @author Julian Garrido
 * @date 10/04/2013
 *
 */
public class AladinInvoker {

	//private String script;
	private String std_out="";
	private String error_out="";
	private int option;
	
	public static final String GUI = "gui";
	public static final String NOGUI = "nogui";
	
	private String ALADINJAR = "/Applications/Aladin.app/Contents/Resources/Java/Aladin.jar";
	//private String Aladinjar = "/Users/julian/Documents/wf4ever/aladin/Aladin.jar";
	//private String ALADINJAR = "/home/julian/Documentos/wf4ever/aladin/Aladin.jar";
	
	private static Logger logger = Logger.getLogger(AladinInvoker.class);
	
	public AladinInvoker() throws IOException{
		initAladinJar();
	}
	
	public AladinInvoker(int opt ) throws IOException{
		option = opt;
		initAladinJar();
	}
		
	public void runScript(String script, String gui) throws InterruptedException, IOException{
		ProcessBuilder builder;
		
		initAladinJar();
		
		System.out.println("Calling Aladin script: "+ ALADINJAR);
		logger.info("ALADIN. --------- Calling Aladin script: "+ ALADINJAR);
		
		if(AladinInvoker.GUI.compareTo(gui)!=0){
		    //System.out.println("java -jar " + ALADINJAR + " -nogui script="+script);
			//builder = new ProcessBuilder("java", "-jar", "/Users/julian/Documents/wf4ever/aladin/Aladin.jar", "-nogui", "script="+script);
			builder = new ProcessBuilder("java", "-jar", ALADINJAR, "-nogui", "script="+script);
			System.out.println("ALADIN. Calling Aladin script: "+ "java -jar "+ ALADINJAR + " -nogui script="+script);
			logger.info("ALADIN. Calling Aladin script: "+ "java -jar "+ ALADINJAR + " -nogui script="+script);
		}else{
			///Users/julian/Documents/wf4ever/aladin/
			//builder = new ProcessBuilder("java", "-jar", "/Users/julian/Documents/wf4ever/aladin/Aladin.jar", "script="+script);
			builder = new ProcessBuilder("java", "-jar", ALADINJAR, "script="+script);
			System.out.println("ALADIN. Calling Aladin script: "+ "java -jar " + ALADINJAR + " script="+script);
			logger.info("ALADIN. Calling Aladin script: "+ "java -jar " + ALADINJAR + " script="+script);
		}
		
		
		
		//Map<String, String> environ = builder.environment();

	    Process process;
	    SecurityManager securityBackup = System.getSecurityManager();
		System.setSecurityManager(new NoExitSecurityManager_());
		
		try{
		    
			process = builder.start();
		    InputStream is = process.getInputStream();		    
		    StreamReaderAsync outputReader = new StreamReaderAsync(is, "OUTPUT");
		    
		    InputStream eis = process.getErrorStream();
		    StreamReaderAsync errorReader = new StreamReaderAsync(eis, "ERROR");
		    
		    //start the threads
		    outputReader.start();
		    errorReader.start();
	    
	    
		    //System.out.println("Estoy antes del waitfor");
		    int exitValue = process.waitFor();
		    //System.out.println("Estoy despues del waitfor");
		    
		    //is.close();
		    //eis.close();
		    	        
		    this.error_out = errorReader.getResult();
		    this.std_out = outputReader.getResult();
		   
		    //System.out.println("exit value for the process: " + process.exitValue());
		    process.destroy();
		    
		}catch(SecurityException ex){
			System.out.println("Se ha ejecutado exit() en AladinInvoker");
			logger.error("Se ha ejecutado exit() en AladinInvoker");
		}
		
		System.setSecurityManager(securityBackup);
	    
		
		    
	    //System.out.println("ERROR: " + this.error_out);
	    //System.out.println("STD: " + this.std_out);
		
		
	}
	
	public String getStd_out() {
		return std_out;
	}

	public String getError_out() {
		return error_out;
	}

	public void runScriptURL(String url, String gui) throws InterruptedException, IOException{
		ProcessBuilder builder;
		
		logger.info("ALADIN. --------- Calling Aladin script: "+ ALADINJAR);
		initAladinJar();
		
		if(AladinInvoker.GUI.compareTo(gui)!=0){
		
			//ProcessBuilder builder = new ProcessBuilder("java", "-jar", "/Users/julian/Documents/wf4ever/aladin/Aladin.jar", "-nogui", "-scriptfile="+url); 
			//builder = new ProcessBuilder("java", "-jar", "/Users/julian/Documents/wf4ever/aladin/Aladin.jar", "-nogui", "-scriptfile="+url);
			logger.info("ALADIN. --------- Calling Aladin script: java -jar" + ALADINJAR + " -nogui -scriptfile="+url);
			System.out.println("ALADIN. --------- Calling Aladin script: java -jar" + ALADINJAR + " -nogui -scriptfile="+url);
			builder = new ProcessBuilder("java", "-jar", ALADINJAR, "-nogui", "-scriptfile="+url);
		}else{
			///Users/julian/Documents/wf4ever/aladin/
			//builder = new ProcessBuilder("java", "-jar", "/Users/julian/Documents/wf4ever/aladin/Aladin.jar", "-scriptfile="+url);
			logger.info("ALADIN. --------- Calling Aladin script: java -jar" + ALADINJAR + " -scriptfile="+url);
			System.out.println("ALADIN. --------- Calling Aladin script: java -jar" + ALADINJAR + " -scriptfile="+url);
			builder = new ProcessBuilder("java", "-jar", ALADINJAR, "-scriptfile="+url);
		}
		
		////Map<String, String> environ = builder.environment();

	    Process process;

		process = builder.start();
	
	    InputStream is = process.getInputStream();		    
	    StreamReaderAsync outputReader = new StreamReaderAsync(is, "OUTPUT");
	    
	    InputStream eis = process.getErrorStream();
	    StreamReaderAsync errorReader = new StreamReaderAsync(eis, "ERROR");
	    
	    //start the threads
	    outputReader.start();
	    errorReader.start();
	    
	    int exitValue = process.waitFor();
	    
	    //is.close();
	    //eis.close();
	    
	    this.error_out = errorReader.getResult();
	    this.std_out = outputReader.getResult();
	    
		    		
	}
	
	/**
	 * Find the folder where Aladin.jar is and initialize ALADINJAR with the full path. 
	 * This is suppouse to be the version 7.5. If a new version is deployed, the way the path is built 
	 * has to be changed.
	 * @throws IOException 
	 */
	public void initAladinJar() throws IOException{
		File file = ClassLocation.getClassLocationFile(AnnotationAssertion.class);
		
		file = ClassLocation.getClassLocationFile(Aladin.class);
		if(file.exists()){
			ALADINJAR = file.getAbsolutePath();
			System.out.println();
			logger.info("Aladin to be used at: "+ ALADINJAR);
			System.out.println("Aladin to be used at: "+ ALADINJAR);
		}else{
			file = ClassLocation.getClassLocationFile(Aladin.class);
			if(file.exists()){
				ALADINJAR = file.getAbsolutePath();
				System.out.println();
				logger.info("Aladin to be used at: "+ ALADINJAR);
				System.out.println("Aladin to be used at: "+ ALADINJAR);
			}else{
				System.out.println("ALADIN: trying to init ALADINJAR. the result was a non existing path");
				logger.info("ALADIN: trying to init ALADINJAR. the result was a non existing jar file");
			}
		}
		
		file = ClassLocation.getClassLocationFile(AnnotationAssertion.class);
		
		
		
		String path = file.getAbsolutePath();
		//int position = path.indexOf("repository");
		//position = path.indexOf("net"+File.separator+"sf"+File.separator+"taverna"+File.separator+"t2"+File.separator+"core");
		//ALADINJAR = path.substring(0, position);
		//ALADINJAR += "cds"+File.separator+"aladin"+File.separator+"Aladin"+File.separator+"7.5"+File.separator+"Aladin-7.5.jar";
	}
	
	
	public void runMacro(String scriptURL, String parametersURL, String gui) throws InterruptedException, IOException{
		
		String macroScript = "macro "+ scriptURL + " " + parametersURL; 
		runScript(macroScript, gui);
		
	}
	
	/**
	 * This method is to test different ways to know where is the jar that contains Aladin.class
	 * @throws IOException 
	 */
	public void testWhereIsThejar() throws IOException{
		MyClassLocation myLocation = new MyClassLocation();
		File file;
		URL resource;
		String classResourceName = "/cds/aladin/Aladin.class";
		
		
		//the error that taverna provide with the err-outpu:
		//Unable to access jarfile /Applications/Taverna 2.4.0.app/Contents/Resources/Java/repository/cds/aladin/Aladin/7.5/Aladin-7.5.jar
		
		file = ClassLocation.getClassLocationFile(AnnotationAssertion.class);
		System.out.println("Result for AnnotationAssertion with ClassLocation: "+file + ", exists? " + file.isFile());
		logger.info("Result for AnnotationAssertion with ClassLocation: "+file + ", exists? " + file.isFile());
		
		file = ClassLocation.getClassLocationFile(Aladin.class);
		System.out.println("Result form ClassLocation: "+file + ", exists? " + file.isFile());
		logger.info("Result form ClassLocation: "+file + ", exists? " + file.isFile());
		
		file = myLocation.getClassLocationFile(Aladin.class);
		System.out.println("Result form MyClassLocationocation: "+file + ", exists? " + file.isFile());
		logger.info("Result form MyClassLocationocation: "+file + ", exists? " + file.isFile());
		
		
		Class theClass = Aladin.class;
		try{
			classResourceName = theClass.getName().replace('.', '/')	+ ".class";
			resource = theClass.getResource("/" + classResourceName);
			System.out.println("resource: "+ resource);
			logger.info("resource: "+ resource);
		}catch(NullPointerException ex){
			System.out.println("A null pointer exception was capture in AladinInvoker");
		}
		try{
			URL codeSource = theClass.getProtectionDomain().getCodeSource().getLocation();
			System.out.println("code source: " + codeSource);
			logger.info("code source: " + codeSource);
		}catch(NullPointerException ex){
			System.out.println("A null pointer exception was capture in AladinInvoker");
		}
		try{
			//it returns the path to the class that is running
			System.out.println("Class loader 1: " + ClassLoader.getSystemClassLoader().getResource(".").getPath());
			logger.info("Class loader 1: " + ClassLoader.getSystemClassLoader().getResource(".").getPath());
		}catch(NullPointerException ex){
			System.out.println("A null pointer exception was capture in AladinInvoker");
		}
		
		try{
			System.out.println("Class loader 2: " + ClassLoader.getSystemClassLoader().getResource(classResourceName).getPath());
			logger.info("Class loader 2: " + ClassLoader.getSystemClassLoader().getResource(classResourceName).getPath());
		}catch(NullPointerException ex){
			System.out.println("A null pointer exception was capture in AladinInvoker");
		}
		try{
			resource = Aladin.class.getClassLoader().getResource("/"+classResourceName);
			System.out.println("Class loader 3: "+ resource);
			logger.info("Class loader 3: "+ resource);
		}catch(NullPointerException ex){
			System.out.println("A null pointer exception was capture in AladinInvoker");
		}
	}

	protected void run() throws IOException{
		try {		
			if(option == 1){
				String example2 = "get aladin(J,FITS) m1 ;\n save /Users/julian/Documents/wf4ever/aladin/exampleTests/m1.jpg; quit";
				System.out.println("Starting option 1");
				runScript(example2, "gui");				
				System.out.println("Ending option 1");
			}else if(option ==2){
				String scriptpath = "/Users/julian/workspaces/aladinTest_ws/myAladin/myTestSRC/iaa/amiga/aladin/resources/examplescript.ajs";
				String scriptURL = "file:///Users/julian/workspaces/aladinTest_ws/myAladin/myTestSRC/iaa/amiga/aladin/resources/examplescript.ajs";
				System.out.println("Starting option 2");
				runScriptURL(scriptpath, "nogui");
				System.out.println("Ending option 2");
			}else if(option == 3){
				String scriptMacro ="macro /Users/julian/workspaces/aladinTest_ws/myAladin/myTestSRC/iaa/amiga/aladin/resources/Aladin_workflow_script.ajs /Users/julian/workspaces/aladinTest_ws/myAladin/myTestSRC/iaa/amiga/aladin/resources/Aladin_workflow_params.txt";
				System.out.println("Starting option 3");
				runScript(scriptMacro, "nogui");
				System.out.println("Ending option 3");
			}else if(option == 4){
				System.out.println("Starting option 4");
				runMacro("/Users/julian/src/astrotaverna/Image-activity/src/test/resources/Aladin_workflow_script.ajs", "/Users/julian/src/astrotaverna/Image-activity/src/test/resources/Aladin_workflow_params.txt", "nogui");
				System.out.println("Ending option 4");
			}else if(option == 5){
				String example2 = "get aladin(J,FITS) m1 ;\n save /home/julian/Documentos/wf4ever/aladin/m1.jpg; quit";
				System.out.println("Starting option 5");
				runScript(example2, "gui");				
				System.out.println("Ending option 5");
			}else if(option == 6){
				System.out.println("Starting option 6");
				runMacro("file:///home/julian/Documentos/wf4ever/aladin/Aladin_workflow_script.ajs", "file:///home/julian/Documentos/wf4ever/aladin/Aladin_workflow_params.txt", "nogui");
				System.out.println("Ending option 6");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		AladinInvoker invoker4 = new AladinInvoker(4);
		AladinInvoker invoker3 = new AladinInvoker(3);
		AladinInvoker invoker2 = new AladinInvoker(2);
		AladinInvoker invoker1 = new AladinInvoker(1);
		AladinInvoker invoker5 = new AladinInvoker(5);
		AladinInvoker invoker6 = new AladinInvoker(6);
		
		//invoker1.run();
		//invoker2.run();
		//invoker3.run();
		invoker4.run();
		//invoker5.run();
		//invoker6.run();
		System.out.println("The end");
	}

}
