package mainengine;
import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cubemanager.CubeManager;
import cubemanager.cubebase.CubeQuery;
import filecreation.FileMgr;
import filecreation.PptxFile;
import filecreation.WordFile;
import parsermgr.ParserManager;
import storymgr.FinResult;
import storymgr.StoryMgr;

@SuppressWarnings("serial")
public class MainEngine extends UnicastRemoteObject implements IMainEngine {

	private CubeManager cubeManager;
	private StoryMgr storMgr;
	private ParserManager prsMng;
	private Options optMgr;
	private String msrname;
	
	private Map<String,Object> apiData;
	
	Logger logger = LoggerFactory.getLogger(MainEngine.class);

	public MainEngine() throws RemoteException {
		super();	
		storMgr = new StoryMgr();
		optMgr = new Options();
		
		apiData = new HashMap<String,Object>();
	}
	
	public void answerCubeQueriesFromFile(File file) throws RemoteException {
		Scanner sc;
		try {
			sc = (new Scanner(file)).useDelimiter("@");
			while (sc.hasNext()) {
				long startTime = System.nanoTime();
				CubeQuery currentCubQuery = cubeManager.
						createCubeQueryFromString(sc.next(), msrname);	
				storMgr.createStory(currentCubQuery, optMgr.getAudio(),
						startTime, cubeManager, msrname);
				createDocuments(currentCubQuery, startTime);	
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}	
	
	   public void createDocuments(CubeQuery cubequery,  long startTime) {
	    	// PPTX Wrap up
		   	FileMgr wrapUp;
		   	storMgr.getStory().setFinalResult(new FinResult());
		   	storMgr.getStory().getFinalResult()
				.setFilename("OutputFiles/" + cubequery.getName() + ".pptx");
		   	wrapUp = new PptxFile();
		   	wrapUp.setFinalResult(storMgr.getStory().getFinalResult());
			long strWraUpTime = System.nanoTime();
			wrapUp.createFile(storMgr.getStory());
			storMgr.setStoryTime(((PptxFile) wrapUp).getunZipZipTime());
			storMgr.setStoryTime("WrapUp Time\t" + (System.nanoTime()
					- strWraUpTime) + "\n");	
			
			apiData = wrapUp.getApiData();
			
			// Word Wrap Up
			if (optMgr.getWord()) {
				storMgr.getStory().setFinalResult(new FinResult());
				storMgr.getStory().getFinalResult()
				.setFilename("OutputFiles/" + cubequery.getName() + ".docx");
				wrapUp = new WordFile();
				wrapUp.setFinalResult(storMgr.getStory().getFinalResult());
				wrapUp.createFile(storMgr.getStory());
			}
			storMgr.setStoryTime("Story Creation Time " + "\t"
					+ (System.nanoTime() - startTime) + "\n");
	    }
	    
	
	public void initializeConnection(String schemaName, String login,
			String passwd, String inputfile, String cubeName)
			throws RemoteException {
		createDefaultFolders();
		initializeCubeMgr(inputfile);
		cubeManager.CreateCubeBase(schemaName, login, passwd);
		constructDimension(inputfile, cubeName);
	}

	private void createDefaultFolders() throws RemoteException {
		File audio = new File("audio");
		if (!audio.exists()) {
			audio.mkdir();
		}
		audio.deleteOnExit();
		File ppt = new File("ppt");
		if (!ppt.exists()) {
			ppt.mkdir();
		}
	}
	
	private void initializeCubeMgr(String lookup) throws RemoteException {
		cubeManager = new CubeManager(lookup);
	}

	private void constructDimension(String inputlookup, String cubeName)
			throws RemoteException {
		try {
			this.parseFile(new File("InputFiles/" + inputlookup + "/"
					+ cubeName + ".ini"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void parseFile(File file) throws FileNotFoundException {
		if (file != null) {
			prsMng = new ParserManager();
			@SuppressWarnings("resource")
			Scanner sc = (new Scanner(file)).useDelimiter(";");
			while (sc.hasNext()) {
				prsMng.parse(sc.next() + ";");
				if (prsMng.mode == 2) {
					this.cubeManager.InsertionDimensionLvl(
							prsMng.name_creation, prsMng.sqltable,
							prsMng.originallvllst, prsMng.customlvllst,
							prsMng.dimensionlst);
				} else if (prsMng.mode == 1) {
					this.cubeManager.InsertionCube(prsMng.name_creation,
							prsMng.sqltable, prsMng.dimensionlst,
							prsMng.originallvllst, prsMng.measurelst,
							prsMng.measurefields);
				}
			}
		}
	}

	public void optionsChoice(boolean audio, boolean word)
			throws RemoteException {
		optMgr.setAudio(audio);
		optMgr.setWord(word);
	}
	
	public Map<String,Object> getData() {
		
		
		return apiData;
		//		logger.info("Answer");
//		
//		/*
//		 * System.out.println(currentCubQuery.toString());
//		 * 
//		 * String [][] temparray = currentCubQuery.getResultArray();
//		 * System.out.println(Arrays.deepToString(temparray));
//		 * 
//		 * 
//		 * 
//		 * JSONObject obj = new JSONObject(); JSONArray array = new JSONArray(); for(int
//		 * i=2;i<temparray.length;i++) { JSONObject entry = new JSONObject(); for(int
//		 * j=0;j<temparray[0].length;j++) {
//		 * 
//		 * entry.put(temparray[0][j],temparray[i][j]);
//		 * 
//		 * } array.put(entry); } obj.put("Result",array);
//		 * System.out.println(obj.toString(2));
//		 * 
//		 * tempmap.put("resultarray",temparray);
//		 * //tempmap.put("resultarray2",temparray);
//		 * //tempmap.put("Stringtest",temp.toString());
//		 */		
	}

}
