package Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

import MVC.GameModelEventsListener;
import MVC.GameUIEventsListener;
import UI.VisualApplication;

public class ServerReceiver implements Runnable{
	private static final int WANTED_PORT = 29888;
	private static Vector<GameUIEventsListener> allListeners;
	
	public ServerReceiver() {
		allListeners = new Vector<GameUIEventsListener>();


	}
	
	public void registerListener(GameModelEventsListener listener) {
		allListeners.add(listener);
	}
	
	private static void addMissileLauncher(String id) {
		for (GameUIEventsListener g : allListeners) {
			g.addMissileLauncherFromUI(id);
		}
	}
	
	private static void launchMissile(String missileLauncherId, String missileID,String destination, int damage) {
		for (GameUIEventsListener g : allListeners) {
			g.launchMissileFromUI(missileLauncherId, missileID, destination, damage);
		}
	}
	
	private static void destructMissile(String missileIdToDestruct, String missileDestructorId) {
		for (GameUIEventsListener g : allListeners) {
			g.destructMissileFromUI(missileIdToDestruct, missileDestructorId);
		}
	}
	
	
	public void startServer() throws Exception {
		System.out.println("Running server...");
		ServerSocket listener = new ServerSocket(getAvailablePort(WANTED_PORT));
		try {
			while (true) {
				new Handler(listener.accept()).start();
			}
		} finally {
			listener.close();
		}
	}
	
	private static int getAvailablePort(int port) throws IOException {
		int min = 20000, max = 30000; 
		int range = (max - min) + 1;
	    int Finalport = port;
	    while (!isPortAvailable(Finalport)) {
	    	Finalport =  (int)(Math.random() * range) + min;
	    }
	    return Finalport;
	}

	private static boolean isPortAvailable(final int port) throws IOException {
	    ServerSocket ss = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        return true;
	    } catch (final IOException e) {
	    } finally {
	        if (ss != null) {
	            ss.close();
	        }
	    }

	    return false;
	}
	
	private static class Handler extends Thread {
		private Socket socket;
		private InputStream in;
		private OutputStream out;

		private void sendPublicKey() throws IOException {
			StringBuilder messageHeader = new StringBuilder();
			messageHeader.append("PUBLIC KEY\n");
			File publicKeyFile = new File("public.der");
			messageHeader.append(publicKeyFile.length() + "\n\n");
			out.write(messageHeader.toString().getBytes("ASCII"));
			out.write(Files.readAllBytes(publicKeyFile.toPath()));
			out.flush();
		}

		private void sendErrorMessage(String msg) {
			try {
				msg = "ERROR\n" + msg + "\n\n";
				out.write(msg.getBytes("ASCII"));
			} catch (IOException e) {
				System.out.println("Failed to send an error message to client.");
				System.exit(1);
			}
		}

		private byte[] readAndDecryptAesKey(byte[] privateKeyFile) throws GeneralSecurityException, IOException {
			// read the encrypted AES key from the socket
			byte[] encryptedAesKey = new byte[ProtocolUtilities.KEY_SIZE_AES * 2];
			in.read(encryptedAesKey);
			// put the private RSA key in the appropriate data structure
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyFile);
			PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec);
			// Decipher the AES key using the private RSA key
			Cipher pkCipher = Cipher.getInstance("RSA");
			pkCipher.init(Cipher.DECRYPT_MODE, privateKey);
			CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(encryptedAesKey), pkCipher);
			byte[] aesKey = new byte[ProtocolUtilities.KEY_SIZE_AES / 8];
			cipherInputStream.read(aesKey);
			cipherInputStream.close();
			return aesKey;
		}
		
		private String scanLineFromCipherStream(CipherInputStream cstream) throws IOException {
			StringBuilder line = new StringBuilder();
			char c;
			while ((c = (char) cstream.read()) != '\n') {
				line.append(c);
			}
			return line.toString();
		}
		
		private File receiveFile(byte[] aesKey) throws GeneralSecurityException, IOException {
			Cipher aesCipher = Cipher.getInstance("AES");
			SecretKeySpec aeskeySpec = new SecretKeySpec(aesKey, "AES");
			aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);
			CipherInputStream cipherInputStream = new CipherInputStream(in, aesCipher);
			String fileName = scanLineFromCipherStream(cipherInputStream);
			String fileSize = scanLineFromCipherStream(cipherInputStream);
			File receivedFile = new File(fileName.toString());
			FileOutputStream foStream = new FileOutputStream(receivedFile);
			ProtocolUtilities.sendBytes(cipherInputStream, foStream, Long.parseLong(fileSize));
			foStream.flush();
			foStream.close();
			return receivedFile;
		}

		public Handler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			String command;
			System.out.println("server thread id: " + Thread.currentThread().getId());

			try {
				in = new BufferedInputStream(socket.getInputStream());
				out = new BufferedOutputStream(socket.getOutputStream());
				ArrayList<String> headerParts = ProtocolUtilities.consumeAndBreakHeader(in);
				command = headerParts.get(0);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Connection to client dropped.");
				return;
			} catch (NullPointerException e) {
				System.err.println("Unable to read command from client");
				return;
			}
			switch (command) {
			case "GET PUBLIC KEY":
				try {
					sendPublicKey();
					System.out.println("Sent public key!");
				} catch (IOException e) {
					System.err.println("Connection to client dropped. Failed to send public key.");
				}
				break;
			case "FILE TRANSFER":
				byte[] privateRsaKey;
				try {
					privateRsaKey = Files.readAllBytes(new File("private.der").toPath());
				} catch (IOException e) {
					sendErrorMessage("SERVER ERROR");
					System.err.println("Server failed to open private key file.");
					return;
				}
				try {
					byte[] aesKey = readAndDecryptAesKey(privateRsaKey);
					File file = receiveFile(aesKey);
					System.out.println("Received File");
					System.out.println("Name: " + file.getName());
					System.out.println("Size:" + file.length());
					ProtocolUtilities.handleFile(file);
					out.write("SUCCESS\nsuccessful transmission\n\n".getBytes("ASCII"));
					out.flush();
					readFile(file);
					deleteFile(file);
					socket.close();
				} catch (GeneralSecurityException e) {
					sendErrorMessage("Failed to decrypt AES key and/or file content.");
					System.err.println("Server failed to decrypt AES key and/or file content.");
					return;
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("Connection to client dropped.");
					return;
				}
				break;
			default:
				sendErrorMessage("INVALID COMMAND");
				System.out.println("Invalid command detected: " + command);
			}
		}

		private void deleteFile(File file) {
			file.deleteOnExit();
		}
		
		private void readFile(File file) {
		    try {
		    	for (String line : Files.readAllLines(Paths.get(file.getAbsolutePath()))) {
	    		String[] splitStr = line.split("\\s+");
	    			if(splitStr[0] == "addMissileLauncher") {
	    				addMissileLauncher(splitStr[1]);
	    			} else if(splitStr[0] == "launchMissile") {
	    				launchMissile(splitStr[1], splitStr[2], splitStr[3], Integer.parseInt(splitStr[4]));
	    			} else if(splitStr[0] == "destructMissile") {
	    				destructMissile(splitStr[1], splitStr[2]);
	    			}
	    		}
		    } catch (FileNotFoundException e) {
		      e.printStackTrace();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		}
	}

	@Override
	public void run() {
		try {
			this.startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}