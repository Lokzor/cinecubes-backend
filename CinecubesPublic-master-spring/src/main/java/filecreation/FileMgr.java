package filecreation;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import storymgr.Act;
import storymgr.FinalResult;
import storymgr.Slide;
import storymgr.Story;
import storymgr.Tabular;

public abstract class FileMgr {
    /**
	 * @uml.property  name="finalResult"
	 * @uml.associationEnd  
	 */
    protected FinalResult finalResult;
	private Map<String,Object> apiData;
	
	public FileMgr(){
		apiData = new HashMap<String,Object>(); 
	}
	
	public FinalResult getFinalResult() {
		return finalResult;
	}

	public void setFinalResult(FinalResult finalresult) {
		finalResult = finalresult;
	}
   
	public void createFile(Story story){
		int slide_so_far_created = 0;
		
			
		for (Act actItem : story.getActs()) {
			if (actItem.getId() == 0) {
				
				Map<String,Object> introSlideData = new HashMap<String,Object>();
				
			
				Slide slide = (Slide) actItem.getEpisode(0);
				slide.subTimeCreationPutInPPTX(System.nanoTime());
					
				createIntroSlide(slide,slide_so_far_created);
				slide.subTimeCreationPutInPPTX(System.nanoTime());
				slide_so_far_created += actItem.getSizeOfEpisodes();
				
//				System.out.println("This is the intro slide with id ");
//				System.out.println("This is the title of the Episode: " + slide.getTitle());
//				System.out.println("This is the subtitle of the Episode: " + slide.getSubTitle());
//				System.out.println("This is the notes of the Episode: " + slide.getNotes());
				
				introSlideData.put("Title",slide.getTitle());
				introSlideData.put("Subtitle",slide.getSubTitle());
				introSlideData.put("Notes",slide.getNotes());
				
				addStringToMap("IntroSlide",introSlideData);
			 
												
			} else if (actItem.getId() == -1) {
				Slide slide = (Slide) actItem.getEpisode(0);
				slide.setTimeCreationPutInPPTX(System.nanoTime());
				createSummarySlide(slide, slide_so_far_created + 2);
				slide.subTimeCreationPutInPPTX(System.nanoTime());
				slide_so_far_created += actItem.getSizeOfEpisodes();
				
				
				Map<String,Object> summarySlideData = new HashMap<String,Object>();
				
//				System.out.println("This is the summary slide with id ");
//				System.out.println("This is the title of the Episode: " + slide.getTitle());
//				System.out.println("This is the subtitle of the Episode: " + slide.getSubTitle());
//				System.out.println("This is the notes of the Episode: " + slide.getNotes());
				
				summarySlideData.put("Title",slide.getTitle());
				summarySlideData.put("Subtitle",slide.getSubTitle());
				summarySlideData.put("Notes",slide.getNotes());
				
				addStringToMap("SummarySlide",summarySlideData);
				
			} else if (actItem.getSizeOfEpisodes() > 1
					|| actItem.getId() == 20) {
				for (int j = 0; j < actItem.getSizeOfEpisodes(); j++) {
					
					Slide slide = (Slide) actItem.getEpisode(j);

					slide.setTimeCreationPutInPPTX(System.nanoTime());
					
					
					Map<String,Object> slideData = new HashMap<String,Object>();

					
					if (slide.getTitle().contains("Act")) {
						createNewSlide(null, null, slide.getFilenameAudio(),
							 slide.getTitle(), j + slide_so_far_created + 2,
							 null, null, slide.getSubTitle(), null,
								(actItem.getId() == 3 ? 0 : 1));
						
//						System.out.println("This is slide with id " + j + slide_so_far_created + 2);
//						System.out.println("This is the title of the slide: " + slide.getTitle());
//						System.out.println("This is the subtitle of the slide: " + slide.getSubTitle());
//						System.out.println("This is the notes of the slide: " + slide.getNotes());
						
						slideData.put("Title",slide.getTitle());
						slideData.put("Subtitle",slide.getSubTitle());
						slideData.put("Notes",slide.getNotes());
						
						
					}
					else if (slide.getNotes().length() == 0) {
						Tabular tmp_tbl = ((Tabular) slide.getVisual());
						createNewSlide(slide.getVisual().getPivotTable(),
								tmp_tbl.getColorTable(), null, slide.getTitle(), j
										+ slide_so_far_created + 2, null, null,
								slide.getSubTitle(),
								(Tabular) slide.getVisual(),
								(actItem.getId() == 3 ? 0 : 1));
						
//						System.out.println("This is slide with id " + j + slide_so_far_created + 2);
//						System.out.println("This is the title of the slide: " + slide.getTitle());
//						System.out.println("This is the subtitle of the slide: " + slide.getSubTitle());
//						System.out.println("This is without notes of the slide: " + slide.getNotes());
//						System.out.println("This is the table of the slide: " + Arrays.deepToString(slide.getVisual().getPivotTable()));
						
						slideData.put("Title",slide.getTitle());
						slideData.put("Subtitle",slide.getSubTitle());
						slideData.put("Notes",slide.getNotes());
						slideData.put("Table",slide.getVisual().getPivotTable());
						
						
					} else {
						Tabular tmp_tbl = ((Tabular) slide.getVisual());
						createNewSlide(slide.getVisual().getPivotTable(),
								tmp_tbl.getColorTable(), slide.getFilenameAudio(),
								slide.getTitle(), j+ slide_so_far_created + 2,
								slide.getTitleColumn(), slide.getTitleRow(),
								slide.getSubTitle(),
								(Tabular) slide.getVisual(),
								(actItem.getId() == 3 ? 0 : 1));
						addNotesOnSlide(slide.getNotes());
						
//						System.out.println("This is slide with id " + j + slide_so_far_created + 2);
//						System.out.println("This is the title of the slide: " + slide.getTitle());
//						System.out.println("This is the subtitle of the slide: " + slide.getSubTitle());
//						System.out.println("This is the notes of the slide: " + slide.getNotes());
//						System.out.println("This is the table of the slide: " + Arrays.deepToString(slide.getVisual().getPivotTable()));
						
						slideData.put("Title",slide.getTitle());
						slideData.put("Subtitle",slide.getSubTitle());
						slideData.put("Notes",slide.getNotes());
						
						
						String [][] tempTable = slide.getVisual().getPivotTable();
						ArrayList<Map> table = new ArrayList<Map>(); 
						
						for(int i=2; i<tempTable.length; i++) { 
							
							Map<String,String> entry = new HashMap<String,String>(); 
							
							for(int j1=0;j1<tempTable[0].length;j1++) {
								
								entry.put(tempTable[0][j1],tempTable[i][j1]);
								
							} 
							
							table.add(entry);
						} 						
						
						slideData.put("Table", table);
					}
					
					addStringToMap("Slide" + (j + slide_so_far_created + 2) ,slideData);
					
					slide.subTimeCreationPutInPPTX(System.nanoTime());
				}
				slide_so_far_created += actItem.getSizeOfEpisodes();
			}
		}
		

		FileOutputStream fout;
		try {
			fout = new FileOutputStream(this.finalResult.getFilename());
			writeOutput(fout);
			fout.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	 }
		
		
		public Map<String,Object> getApiData() {
			return apiData;
		}
		
		
		private void addStringToMap(String key, Map<String,Object> data) {		
			apiData.put(key,data);
		}
		

		abstract protected void createNewSlide(String[][] table, Color[][] colorTable,
				String AudioFilename, String Title, int slideid,
				String titleColumn, String titleRow, String subtitle,
				Tabular tabular, int hide_slide);
		
		abstract protected void addNotesOnSlide(String notes); 
		
		abstract protected void createIntroSlide(Slide episode, int slide_so_far_created);
		
		abstract protected void createSummarySlide(Slide episode, int slideId);
		
		abstract protected void writeOutput(FileOutputStream fout) throws IOException;
		

}
