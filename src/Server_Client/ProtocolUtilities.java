package Server_Client;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class ProtocolUtilities {

	public static final int KEY_SIZE_AES = 128;
	
	//Forwards the bytes from one stream to another
	public static void sendBytes(InputStream source, OutputStream destination) throws IOException {
		byte[] buffer = new byte[1024];
		while(true) {
			int readAmount = source.read(buffer);
			if (readAmount == -1) break;
			destination.write(buffer,0,readAmount);
		}
	}

	//Read header from file
	public static ArrayList<String> consumeAndBreakHeader(InputStream in) throws IOException {
		ArrayList<Character> pipeline = new ArrayList<>();
		StringBuilder header = new StringBuilder();
		int c;
		while ((c = in.read()) != -1) {
			pipeline.add((char) c);
			header.append((char) c);
			if (pipeline.size() != 2) // pipeline not full
				continue;
			if (pipeline.get(0) == '\n' && pipeline.get(1) == '\n') {
				header.deleteCharAt(header.length()-1);
				break;
			}
			pipeline.remove(0); // keep track of only the recent 2 bytes
		}
		if (header.length() == 0) return null;
		ArrayList<String> headerParts = new ArrayList<String>();
		Scanner scanner = new Scanner(header.toString());
		scanner.useDelimiter("\n");
		while (scanner.hasNext()) {
			headerParts.add(scanner.next());
		}
		scanner.close();
		return headerParts;
	}
}
