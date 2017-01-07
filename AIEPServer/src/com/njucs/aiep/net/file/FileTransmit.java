package com.njucs.aiep.net.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.njucs.aiep.frame.AIInfo;


public class FileTransmit {

	public static enum Mode { SERVER, CLIENT };
	
	private String ip = "127.0.0.1", recvFolder = null;
	private int port = 8898;
	private Mode mode;
	private Thread listenThread;
	private ServerSocket serverSocket = null;
	private ReceiveFileEventListener recvFileEventListener = null;
	
	private boolean stop = false;
	
	public final static SimpleDateFormat FILE_DATA_FORMAT 
		= new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	
	//private int listenId = DataTransmit.ILLEGAL_ID;
	private final static byte[] FINISH_BYTES = {'%', '^', '#', '%','@','*','$','*','^','$','*','%'};
	
	private static Random RANDOM = new Random();
	
	public FileTransmit(int port, String recvFolder, ReceiveFileEventListener recvFileEventListener ) {
		this.port = port;
		this.mode = Mode.SERVER;
		this.recvFileEventListener = recvFileEventListener;
		this.recvFolder = recvFolder;
		if( this.recvFolder == null ) this.recvFolder = "";
		else if( ! this.recvFolder.endsWith("\\") && ! this.recvFolder.endsWith("/")){
			this.recvFolder = this.recvFolder.concat( "/" );
		}
	}
	public FileTransmit(String ip, int port){
		this.ip = ip;
		this.port = port;
		this.mode = Mode.CLIENT;
	}
	public Mode getMode(){ return this.mode; }

	public synchronized void execute() throws IOException{
		serverSocket = new ServerSocket(port);
		if( listenThread != null ) return;
		listenThread = new Thread(new Runnable(){
			public void  run(){
				try {
					while( ! stop ) {
						Socket socket = serverSocket.accept();
						InputStream is = socket.getInputStream();
						ObjectInputStream ois = new ObjectInputStream( is );
						AIInfo aiInfo = (AIInfo)ois.readObject();
						
						aiInfo.setJarFileName( genFileName( aiInfo.getSrcFileName() ) );
						FileOutputStream fos = new FileOutputStream(new File( aiInfo.getJarFileName() ));
						byte[] buffer = new byte[10240];
						int len = 0;
						while( (len=is.read(buffer)) != -1 ){
							if(len >= FINISH_BYTES.length){
								int i = 0;
								for( i = 0; i < FINISH_BYTES.length; i ++ ){
									if( buffer[len-1-i] != FINISH_BYTES[11-i] ) break;
								}
								if( i == FINISH_BYTES.length ){
									fos.write(buffer, 0, len-FINISH_BYTES.length);
									fos.flush();
									break;
								}
							}
							fos.write(buffer, 0, len);
							fos.flush();
						}
						fos.close();
						socket.close();
						aiInfo.setFileRecvTime(new Date());
						if( recvFileEventListener != null ){
							recvFileEventListener.receiveFile( new ReceiveFileEvent(this, aiInfo) );
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			private String genFileName( String srcFileName ){
				int index = srcFileName.lastIndexOf( "/" );
				if( index != -1 ){
					srcFileName = srcFileName.substring(index+1);
				}
				index = srcFileName.lastIndexOf( "\\" );
				if( index != -1 ){
					srcFileName = srcFileName.substring(index+1);
				}
				srcFileName = srcFileName.replace(" ", "_");
				srcFileName = srcFileName.replace("\t", "_");//is it necessary?
				if( srcFileName.length() > 20 ){
					//int dot_index = srcFileName.lastIndexOf(".");
					srcFileName = srcFileName.substring(srcFileName.length()-20);
				}
				String dateString = FILE_DATA_FORMAT.format( Calendar.getInstance().getTime() );
				String dstfilename = recvFolder + dateString+"_"+String.valueOf(RANDOM.nextInt())+"_"+srcFileName;
				return dstfilename;
			}
		});
		listenThread.setName("com.njucs.aiep.net.file.FileTransmit.listenThread");
		listenThread.start();

	}
	
	public void stop(){
		if( serverSocket != null ) {
			stop = true;
			try { serverSocket.close(); } catch (IOException e) { e.printStackTrace(); }
		} else {
			System.out.println( "serverSocket == null" );
		}
	}
	
	public void sendAI(AIInfo aiInfo) throws IOException {
		if( aiInfo == null ) {
			throw new IllegalArgumentException( "aiInfo cannot be null!" );
		}
		Socket socket = new Socket( ip, port );
		OutputStream os = socket.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( os );
		//AIInfo aiInfo = AILoader.genAIInfo( aiFileName );
		System.out.println( "id="+aiInfo.getId()+", version="+aiInfo.getVersion() );
		oos.writeObject( aiInfo );

		FileInputStream fis = new FileInputStream( new File(aiInfo.getSrcFileName()) );
		byte[] buf = new byte[10240];//1KB
		int c = 0;
		while( (c = fis.read(buf)) != -1 ){
			os.write(buf, 0, c);
			os.flush();
		}
		os.write(FINISH_BYTES, 0, FINISH_BYTES.length);
		os.flush();
		socket.close();
		oos.close();
		fis.close();
	}
	
	public static void main (String[] args) throws IOException, InterruptedException{
		char port = 12345;
		FileTransmit server = new FileTransmit( port, "upload", new ReceiveFileEventListener(){
			@Override
			public void receiveFile(ReceiveFileEvent event) {
				System.out.println( "@"+event.getAIInfo().getSrcFileName() );
				System.out.println( "@"+event.getAIInfo().getJarFileName() );
				System.out.println( "@"+event.getAIInfo().getFileRecvTime() );
			}
		} );
		server.execute();/*
		FileTransmit client = new FileTransmit( "127.0.0.1", port );
		try{
			client.sendAI( AILoader.genAIInfo("res\\plugin\\sp-fir.jar") );
		} catch(Exception e ){
			e.printStackTrace();
			System.out.println( "e="+e.getMessage() );
		}//*/
		//server.stop();
	}

	
}



