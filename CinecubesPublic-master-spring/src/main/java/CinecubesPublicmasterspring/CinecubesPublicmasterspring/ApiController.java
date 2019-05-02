package CinecubesPublicmasterspring.CinecubesPublicmasterspring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mainengine.MainEngine;

@RestController
public class ApiController {

		private static final Logger logger = LoggerFactory.getLogger(ApiController.class);        
	    
	    @CrossOrigin
	    @GetMapping(path = "/jsonrequest")
	    public Map<String,Object> getJSONFromResultSet() throws RemoteException {
	    	
	    	Map<String,Object>  dataReply = new HashMap<String,Object>();
	    	
	    	
	    	MainEngine service = new MainEngine();
	    	service.initializeConnection("pkdd99", "root",
					"root", "pkdd99", "loan");
			File f2 = new File("InputFiles/cubeQueriesloan2.ini");
			service.answerCubeQueriesFromFile(f2);
			
			dataReply = service.getData();
			
			return dataReply;
	    }
	   	    
	    
	    @CrossOrigin
	    @GetMapping(path = "/jsonrequestparam", produces = MediaType.APPLICATION_JSON_VALUE)
	    public Map<String,Object> free(@RequestParam String ID) throws RemoteException {
	    	
	    	Map<String,Object>  dataReply = new HashMap<String,Object>();
	    	
	    	
	    	MainEngine service = new MainEngine();
	    	service.initializeConnection("pkdd99", "root",
					"root", "pkdd99", "loan");
	        File tmpFile;
			try {
				tmpFile = File.createTempFile("test", ".tmp");
				FileWriter writer = new FileWriter(tmpFile);
		        writer.write(ID);
		        writer.close();
		        
				service.answerCubeQueriesFromFile(tmpFile);/**/
				
				dataReply = service.getData();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return dataReply;
	    }
}
