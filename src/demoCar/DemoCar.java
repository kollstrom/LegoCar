package demoCar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.robotics.RegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

public class DemoCar{
	public static void main(String[] args) throws IOException {
		
		
		try{
			int seatAngle = -90;
			boolean ongoing = true;
			String in, out;
			System.out.println("Starting program...");
			ServerSocket serverSocket = new ServerSocket(4321);
			System.out.println("W8 4 ctrlr 2 start...");
			Socket servSock = serverSocket.accept();
			System.out.println("Ctrlr started...");
			
			RegulatedMotor seat = new EV3MediumRegulatedMotor(MotorPort.A);
			seat.setSpeed(90);
			System.out.println(seat.getSpeed());
			BufferedReader input = new BufferedReader(new InputStreamReader(servSock.getInputStream()));
			PrintStream ps = new PrintStream(servSock.getOutputStream());
			
			while (ongoing){
				System.out.println("W8ing 4 input...");
				in = input.readLine();
//				System.out.println("What i got: " + in);
				
				if (in.equals("seat")){
					System.out.println("Waiting for seat angle...");
					ps.print("Current seat angle " + seatAngle + ". Waiting for seat angle...\n");
					String angle = input.readLine();
					if (angle.equals(null) || angle.equals("")){
						angle = input.readLine();
					}
					seatAngle += Integer.parseInt(angle);
					seat.rotateTo(Integer.parseInt(angle) + 135);
					ps.print("Angle set to: " + angle + "\n");
					Delay.msDelay(2000);
					
				}
				else if (in.equals("quit")){
					System.out.println("Closing...");
					ps.print("EV3 is closing... \n");
					Delay.msDelay(1500);
					seat.close();
					servSock.close();
					ongoing = false;
				}
				else{
					System.out.println("Input empty or unknown command.");
					ps.println("Input empty or unknown command.\n");
				}
			}			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}
}

/*
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class DemoCar {

	private static int angle = 50;
	private static boolean pressed = false;
	
	public static void main(String[] args) {
		
		RegulatedMotor seat = new EV3MediumRegulatedMotor(MotorPort.A);

		try{
			Socket socket = new Socket("127.0.0.1", 4321); //computername
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			Delay.msDelay(3000);
			
			String fromServer = null;
			System.out.println("1");
			while ((fromServer = in.readLine()) != null) {
			    System.out.println("Server: " + fromServer);
			    if (fromServer.equals("Bye."))
			        break;
			}
			
			System.out.println("2");
			
			while (!pressed){
				String input = in.readLine();
				System.out.println("here");
				System.out.println(input);
				
				if (input.equals("") || input.equals(null)){
					continue;
				}
				else if (input.equals("seat")){
					seat.rotate(angle);
					Delay.msDelay(2000);
					angle = angle * -1;
				}
				else if (input.equals("quit")){
					pressed = true;
					seat.close();
				}
				else if (Button.ENTER.isDown()){
					pressed = true;
					seat.close();
					System.exit(1);
				}
			}
		}
		catch (UnknownHostException e) {
		   System.out.println("Unknown host: Laptop");
		   Delay.msDelay(3000);
		   System.exit(1);
		}
		catch  (IOException e) {
			System.out.println("No I/O");
			Delay.msDelay(3000);
			System.exit(1);
		}
		
		System.out.println("Bye Bye!");
		Delay.msDelay(3000);
		System.exit(1);
		
	}
	

	
}
*/