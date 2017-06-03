/*************************************************************************
* Program:
*    Lab Webserver, Computer Communication and Networking
*    Brother Jones, CS 460
* Author:
*    David Lobaccaro
* Summary:
*    This program acts like a basic web server. You can connect to it
*    and retrieve a file of a specific file type (namely txt, html,
*    jpeg, and gif). If it is not found, it returns the 404 Not Found
*    page but if it is found, it retrieves the file.
***************************************************************************/

import java.io.*;
import java.net.*;
import java.util.*;

public final class WebServer {
   
	public static void main(String argv[]) throws Exception {
		int port = argv.length > 0 ? Integer.parseInt(argv[0]) : 6789;

		//Establish the listen socket
		ServerSocket welcomeSocket = new ServerSocket(port);
      
		//Process Http service request in an infinite loop
		while (true) {
			//Listen for a TCP connection request
			Socket individualSocket = welcomeSocket.accept();

			//Construct an object to process the Http request message
			HttpRequest request = new HttpRequest(individualSocket);

			//Create a new thread to process the request
			Thread thread = new Thread(request);

			//Start the thread
			thread.start();
		}
	}
   
}

final class HttpRequest implements Runnable {
   
	final static String CRLF = "\r\n";
	Socket socket;

	//Constructor
	public HttpRequest(Socket socket) throws Exception {
		this.socket = socket;
	}

   //Implement the run() method of the Runnable interface
	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
		//Construct a 1K buffer to hold bytes on their way to the socket
		byte[] buffer = new byte[1024];
		int bytes = 0;

		//Copy requested file into the socket's output stream
		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}

   private static String contentType(String fileName) {
      if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
         return "text/html";
      } else if (fileName.endsWith(".txt")) {
         return "text/plain";
      } else if (fileName.endsWith(".gif")) {
         return "image/gif";
      } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
         return "image/jpeg";
      }
      return "application/octet-stream";
   }

   private void processRequest() throws Exception {
      //Get a reference to the socket's input and output streams
      InputStream is = socket.getInputStream();
      DataOutputStream os = new DataOutputStream(socket.getOutputStream());

      //Setup input stream filters
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);

      //Display the request line of the Http request message
      String requestLine = br.readLine();
      System.out.println();
      System.out.println(requestLine);

      //Get and display the header lines
      String headerLine = null;
      while ((headerLine = br.readLine()).length() != 0) {       
         System.out.println(headerLine);
      }

      //Extract the filename from the request line
      StringTokenizer tokens = new StringTokenizer(requestLine);
      tokens.nextToken();
      String fileName = tokens.nextToken();

      //Prepend a "." so that file request is within the current directory
      fileName = "." + fileName;

      //Open the requested file
      FileInputStream fis = null;
      boolean fileExists = true;
      try {
         fis = new FileInputStream(fileName);
      } catch(FileNotFoundException e) {
         fileExists = false;
      }

      //Construct the respone message
      String statusLine = null;
      String contentTypeLine = null;
      String entityBody = null;
      if (fileExists) {
         statusLine = "HTTP/1.0 200 OK" + CRLF;
         contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
      } else {
         statusLine = "HTTP/1.0 404 Not Found" + CRLF;
         contentTypeLine = "text/html" + CRLF;
         entityBody = "<HTML><HEAD><TITLE>Not Found</TITLE></HEAD><BODY>Not Found</BODY></HTML>";
      }

      //Send the status line
      os.writeBytes(statusLine);

      //Send the content type line
      os.writeBytes(contentTypeLine);

      //Send a blank line to indicate the end of the header lines
      os.writeBytes(CRLF);

      //Send the entity body
      if (fileExists) {
         sendBytes(fis, os);
         fis.close();
      } else {
         os.writeBytes(entityBody);
      }
         
      //close streams and sockets
      os.close();
      br.close();
      socket.close();
      
   }
   
}
