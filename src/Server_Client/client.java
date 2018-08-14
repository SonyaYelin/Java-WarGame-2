package Server_Client;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Scanner;



public class client {
	static String hostName;
	static int portNumber = 29888;

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		System.out.print("Enter server ip: ");
		String hostIPAddress = s.next();
		System.out.print("Enter server port: ");
		portNumber = s.nextInt();
		
		int selection;
		while(true){
			printMenu();
			selection = s.nextInt();
			if(selection == 4) break;
			File f = funcSelect(selection,s);
			System.out.println("Sending file to host: "+hostIPAddress + " and port number: " + portNumber + "...");
			byte[] publicRsaKey, secretAesKey;
			try {
				publicRsaKey = getPublicKey();
				secretAesKey = generateAesKey();
				// = ProtocolUtilities.getRandomFile(folderName);
				//ProtocolUtilities.writeDetailsToFile(fileName, endpointIP);
				boolean isSuccessful = sendFile(publicRsaKey, secretAesKey, new File(f.getAbsolutePath()));
				if (isSuccessful){
					System.out.println("File was successfully sent.");
				} else {
					System.out.println("File was not sent.");
				}
			} catch (FileNotFoundException e) {
				System.err.println("File not found.");
			} catch (IOException e) {
				System.err.println("There was an error connecting to the server.");
			} catch (NoSuchAlgorithmException e) {
				System.err.println("Failed to generate AES key.");
			} catch (GeneralSecurityException e) {
				System.err.println("Unknown security error.");
			}
			//String endpointIP = "";
			// try {
			//   endpointIP = ProtocolUtilities.createOrganizationEndpoints();
			// } catch (IOException e) {
			// 	e.printStackTrace();
			// }
			//Original://Runnable endpoint = new MyThreadsHandler(endpointIP , hostIPAddress, port, f.getAbsolutePath());
			//Runnable endpoint = new MyThreadsHandler(endpointIP , hostIPAddress, port, f.getAbsolutePath());
			f.delete();
			
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static void sendEcryptedAesKEY(OutputStream out, byte[] publicKey,byte[] aesKey) 
			throws GeneralSecurityException, IOException {
		Cipher pkCipher = Cipher.getInstance("RSA");
		PublicKey pk = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey));
		pkCipher.init(Cipher.ENCRYPT_MODE, pk);
		ByteArrayOutputStream tempByteStream = new ByteArrayOutputStream();
		CipherOutputStream cipherStream = new CipherOutputStream(tempByteStream, pkCipher);
		cipherStream.write(aesKey);
		cipherStream.close();
		tempByteStream.writeTo(out);
	}
	
	private static boolean sendFile(byte[] publicKey, byte[] aesKey, File file)
			throws FileNotFoundException, IOException, GeneralSecurityException {
		Socket socket = new Socket(hostName, portNumber);
		BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
		BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
		// send header and encrypted AES. AES is encrypted using private RSA key.
		out.write("FILE TRANSFER\n\n".getBytes("ASCII"));
		sendEcryptedAesKEY(out,publicKey,aesKey);
		// Encrypt the name of the file and its size using AES and send it over the socket
		String fileNameAndSize = new String(file.getName()  + "\n" + file.length() + "\n");
		ByteArrayInputStream fileInfoStream = new ByteArrayInputStream(fileNameAndSize.getBytes("ASCII"));
		SecretKeySpec aeskeySpec = new SecretKeySpec(aesKey, "AES");
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec);
		CipherOutputStream cipherOutStream = new CipherOutputStream(out, aesCipher);
		ProtocolUtilities.sendBytes(fileInfoStream,cipherOutStream);
		// send the the actual file itself and append some bytes so cipher would know it's the end of the file
		FileInputStream fileStream = new FileInputStream(file);
		ProtocolUtilities.sendBytes(fileStream,cipherOutStream);
		out.write(aesCipher.doFinal());
		out.write("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n".getBytes("ASCII")); 
		out.flush();
		ArrayList<String> serverResponse = ProtocolUtilities.consumeAndBreakHeader(in);
		socket.close();
		if (!serverResponse.get(0).equals("SUCCESS")) {
			System.err.println("Failed to send file. The Server responded with the following:");
			for (String msg : serverResponse)
				System.err.println(msg);
			return false;
		}
		return true;
	}
	
	private static byte[] getPublicKey() throws IOException {
		System.out.println("Getting public key: hostName:" + hostName + "portNumber:" + portNumber);
		Socket socket = new Socket(hostName, portNumber);
		BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
		BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
		out.write("GET PUBLIC KEY\n\n".getBytes("ASCII"));
		out.flush();
		ArrayList<String> headerParts = ProtocolUtilities.consumeAndBreakHeader(in);
		if (!headerParts.get(0).equals("PUBLIC KEY")) {
			System.err.println("Failed to obtain public key. The Server responded with the following:");
			for (String msg : headerParts)
				System.err.println(msg);
			System.exit(1);
		}
		int keySize = Integer.parseInt(headerParts.get(1));
		byte[] publicKey = new byte[keySize];
		in.read(publicKey);
		socket.close();
		return publicKey;
	}

	private static byte[] generateAesKey() throws NoSuchAlgorithmException {
		byte[] secretAesKey = null;
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(ProtocolUtilities.KEY_SIZE_AES); // AES key length 128 bits (16 bytes)
		secretAesKey = kgen.generateKey().getEncoded();
		return secretAesKey;
	}
	
	public static void runOrganizationSimulator(String endpointIP, String hostIPAddress, int port, String fileName) {
		hostName = hostIPAddress;
		portNumber = port;
		//trainingSetFolderName = folderName;
		System.out.println("Using host name: "+hostName + " and port number: " + portNumber + "...");
		byte[] publicRsaKey, secretAesKey;
//		File[] currentDirFiles = new File(Paths.get(".").toAbsolutePath().toString()).listFiles();
		//File currentDirFiles = new File(folderName);
		//String fileName = currentDirFiles.getAbsolutePath();
		System.out.println("Available files in the current directory:");
//		for (File f : currentDirFiles) {
//			String fileName = f.getName();
//			if (fileName.charAt(0) == '.') // ignore hidden files.
//				continue;
//			System.out.println(fileName);
//		}
		try {
			publicRsaKey = getPublicKey();
			secretAesKey = generateAesKey();
//			 = ProtocolUtilities.getRandomFile(folderName);
			//ProtocolUtilities.writeDetailsToFile(fileName, endpointIP);
			boolean isSuccessful = sendFile(publicRsaKey, secretAesKey, new File(fileName));
			if (isSuccessful){
				System.out.println("File was successfully sent.");
			} else {
				System.out.println("File was not sent.");
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found.");
		} catch (IOException e) {
			System.err.println("There was an error connecting to the server.");
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Failed to generate AES key.");
		} catch (GeneralSecurityException e) {
			System.err.println("Unknown security error.");
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static File funcSelect(int selection, Scanner s) {
	    File f = null;	
	    try {
			f = File.createTempFile("tmp", ".txt", new File("C:/Temp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    String missileLauncherId = "";
	    String missileId = "";
	    String destination = "";
	    int damage = 0;
	    String missileIdToDestruct = "";
	    String missileDestructorId = "";
	    
		switch (selection) {
		case 1:
			System.out.println("enter missile launcher id ");
			missileLauncherId = s.next();
			////write-to-file
			writeToFile(new String[] {"addMissileLauncher", missileLauncherId}, f);
			break;
		case 2:
			System.out.println("enter missile launcher id: ");
			missileLauncherId = s.next();
			System.out.println("enter missile id: ");
			missileId = s.next();
			System.out.println("enter missile destination: ");
			destination = s.next();
			System.out.println("enter missile potential damage: ");
			damage = s.nextInt(); 
			writeToFile(new String[] {"launchMissile", missileLauncherId, missileId, destination, String.valueOf(damage)}, f);
			break;
		case 3:			
			System.out.println("enter missile Id: ");
			missileIdToDestruct = s.next();
			System.out.println("enter missile destructor id: ");
			missileDestructorId = s.next();
			writeToFile(new String[] {"destructMissile", missileIdToDestruct, missileDestructorId}, f);
			break;
		case 4:
			System.exit(1);
		default:
			break;
		}
		return f;
	}

	private static void printMenu() {
		//Clear the Console 
	    //Print menu
		System.out.println("\n\n1 - addMissileLauncher\n2 - launchMissile\n3 - destructMissile\n4 - exit");
	}

	private static void writeToFile(String[] contentArray, File f) {
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			for(String s : contentArray) {
				output.write(s + " ");
			}
			output.newLine();
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
