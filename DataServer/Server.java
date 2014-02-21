

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server implements Runnable {

	Socket csocket;
	Server(Socket csocket){
		this.csocket = csocket;
	}

	public static void main(String []args) throws IOException{

		int port = 4444;
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("Server is listening at port no. 4444");

		while(true)
		{
			Socket server = serverSocket.accept();
			System.out.println("conencted");
			new Thread(new Server(server)).start();
		}

	}

	public static void doProcessing(){

		BufferedReader br = null;
		Path path = Paths.get("C:\\Users\\SKANDA GURUANAND\\workspace\\Accel\\logdata.txt");
		//	System.out.println("File Path:"+path);
		String[] data_type;
		String[] values;

		try {

			String line;

			br = new BufferedReader(new FileReader(path.toString()));

			while ((line = br.readLine()) != null) {
				//	System.out.println(line);
				data_type = line.split(":");

				if(data_type[0].equalsIgnoreCase("ACC"))
				{
					values = data_type[1].split(",");
					System.out.println(values[0]+","+values[1]+","+values[2]+","+values[3]+"\n");
				}

				if(data_type[0].equalsIgnoreCase("GPS"))
				{
					values = data_type[1].split(",");
					System.out.println(values[0]+","+values[1]+","+values[2]+"\n");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try
		{
			Path path = Paths.get("C:\\Users\\SKANDA GURUANAND\\workspace\\Accel\\logdata.txt");
			File file = new File(path.toString());
			file.delete();
			FileWriter fw = new FileWriter(file, true);
			//Server is running always. This is done using this while(true) loop
			BufferedReader rd = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
			String str;

			while((str = rd.readLine())!=null) 
			{	
			//	System.out.println(str);
				fw.write(str);
				fw.flush();
				fw.write("\n");
			}
			rd.close();
			fw.close();
			csocket.close();
		//	doProcessing();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}

