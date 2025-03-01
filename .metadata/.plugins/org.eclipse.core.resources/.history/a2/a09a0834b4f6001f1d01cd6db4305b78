 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileReader; 
 import java.io.IOException; 
 
 public class ContentReader {

	 private File inputFile;
	 public ContentReader(File file) {
		 this.inputFile = file;  
	 }
	
	 public File getInputFile() {
		 return inputFile;
	 }
	
	 public void setInputFile(File inputFile) {
		 this.inputFile = inputFile;
	 }
	
	 public String read() throws IOException {
		 BufferedReader br = new BufferedReader(new FileReader(this.getInputFile()));
		 StringBuilder sb = new StringBuilder();
		 
		 try {
			 String line = br.readLine();
			
			 while (line != null) {
				 sb.append(line);
				 sb.append(System.lineSeparator());
				 line = br.readLine();
			 }
		 } 
		 finally {
			 br.close();
		 }
		 
		 sb.trimToSize();
		 String everything = sb.toString();
		 return everything;
	 }
	 
	 public static void main (String args[]) {
		 ContentReader reader = new ContentReader(new File("Person.java"));
		 try {
			 System.out.println(reader.read());
		 } 
		 catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
	 }
 }