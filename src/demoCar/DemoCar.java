package demoCar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import lejos.robotics.RegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

public class DemoCar{
	public static void main(String[] args) throws IOException {
		
		
		try{
			int currentState = 0;
			boolean ongoing = true;
			String in, out;
			
			//lists for easier change state methods
			ArrayList<Integer> listOne = new ArrayList<Integer>();
			ArrayList<Integer> listTwo = new ArrayList<Integer>();
			ArrayList<Integer> listThree = new ArrayList<Integer>();
			
			listOne.add(0); //steeringWheelTilt
			listOne.add(0); //notInUse, find better solution
			listOne.add(0); //steeringWheelDepth
			listOne.add(0); //notInUse, find better solution
			listOne.add(0); //wingMirrorLeftX
			
			listTwo.add(0); //wingMirrorLeftY
			listTwo.add(0); //notInUse, find better solution
			listTwo.add(0); //wingMirrorRightX
			listTwo.add(0); //notInUse, find better solution
			listTwo.add(0); //wingMirrorRightY
			
			listThree.add(0); //seatHeight
			listThree.add(0); //notInUse, find better solution
			listThree.add(0); //seatDepth
			listThree.add(0); //notInUse, find better solution
			listThree.add(0); //seatBackAngle
			
			//the following int values are angles
			 
			String radioStation = "NRK P3";
			
			System.out.println("Starting program...");
			ServerSocket serverSocket = new ServerSocket(4321);
			System.out.println("W8 4 ctrlr 2 start...");
			Socket servSock = serverSocket.accept();
			System.out.println("Ctrlr started...");
			
			RegulatedMotor stateChanger = new EV3MediumRegulatedMotor(MotorPort.A);
			RegulatedMotor mediumMotor = new EV3MediumRegulatedMotor(MotorPort.B);
			RegulatedMotor largeMotorOne = new EV3LargeRegulatedMotor(MotorPort.C);
			RegulatedMotor largeMotorTwo = new EV3LargeRegulatedMotor(MotorPort.D);
			
			stateChanger.setSpeed(100);
			mediumMotor.setSpeed(100);
			largeMotorOne.setSpeed(100);
			largeMotorTwo.setSpeed(100);
			
			BufferedReader input = new BufferedReader(new InputStreamReader(servSock.getInputStream()));
			PrintStream ps = new PrintStream(servSock.getOutputStream());
			
			//How the motors/code should do their/its work
			// 1. check state, if state right, start motor
			// 2. if state wrong, change with +-1 with stateChanger
			// 3. turn motor to right angle for next carThing
			// 4. change state with stateChanger with +-1
			// 5. if state right, start motor, else go to 2.
			
			//TODO, method for statechanger rotating
			//TODO, methods for dublicated
			
			
			while (ongoing){
				System.out.println("W8ing 4 input...");
				in = input.readLine();
				
				if (in.equals("seatBackAngle")){
					System.out.println("Waiting for seatBackAngle...");
					
					listThree.set(2, Integer.parseInt(input.readLine()));
					while (true){
						
						if (currentState == 5){
							//TODO motorX.rotateTo(listThree.get(4);
							break;
						}
						
						else if ((currentState +1) % 2 == 1){
							//TODO motorX.rotateTo(listThree.get(currentState + 1));
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 
						}
						else{
							//currentState is smaller than 5
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 	 
						}	
					}
					Delay.msDelay(2000);
				}
				
				else if (in.equals("seatDepth")){
					System.out.println("Waiting for seatDepth...");
					
					listThree.set(1, Integer.parseInt(input.readLine()));
					
					while (true){
						
						if (currentState == 3){
							//TODO motorX.rotateTo(listThree.get(2);
							break;
						}
						
						else if (currentState == 1){
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction;
							//TODO motorX.rotateTo(listThree.get(2));
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 
						}
						
						else if (currentState == 5){
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction;
							//TODO motorX.rotateTo(listThree.get(2));
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction;	
						}
					}
					Delay.msDelay(2000);
				}
				
				else if (in.equals("seatHeight")){
					System.out.println("Waiting for seatHeight...");
					
					listThree.set(0, Integer.parseInt(input.readLine()));
					
					while (true){
						
						if (currentState == 1){
							//TODO motorX.rotateTo(listThree.get(0);
							break;
						}
						
						else if ((currentState -1) % 2 == 1){
							//TODO motorX.rotateTo(listThree.get(currentState - 1));
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 
						}
						else{
							//currentState is greater than 1
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 	 
						}	
					}
					
					Delay.msDelay(2000);
				}
				
				else if (in.equals("wingMirrorRightY")){
					System.out.println("Waiting for wingMirrorRight Y...");
					
					listTwo.set(2, Integer.parseInt(input.readLine()));
					
					while (true){
						
						if (currentState == 5){
							//TODO motorX.rotateTo(listTwo.get(4);
							break;
						}
						
						else if ((currentState +1) % 2 == 1){
							//TODO motorX.rotateTo(listTwo.get(currentState + 1));
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 
						}
						else{
							//currentState is smaller than 5
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 	 
						}	
					}
					Delay.msDelay(2000);
				}
				
				else if (in.equals("wingMirrorRightX")){
					System.out.println("Waiting for wingMirrorRightX...");
					
					listTwo.set(1, Integer.parseInt(input.readLine()));
					
					while (true){
						
						if (currentState == 3){
							//TODO motorX.rotateTo(listThree.get(2);
							break;
						}
						
						else if (currentState == 1){
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction;
							//TODO motorX.rotateTo(listThree.get(2));
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 
						}
						
						else if (currentState == 5){
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction;
							//TODO motorX.rotateTo(listThree.get(2));
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction;	
						}
					}
					
					Delay.msDelay(2000);
				}
				
				else if (in.equals("wingMirrorLeftY")){
					System.out.println("Waiting for wingMirrorLeftY...");
					
					listTwo.set(0, Integer.parseInt(input.readLine()));
					
					while (true){
						
						if (currentState == 1){
							//TODO motorX.rotateTo(listTwo.get(0);
							break;
						}
						
						else if ((currentState -1) % 2 == 1){
							//TODO motorX.rotateTo(listTwo.get(currentState - 1));
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 
						}
						else{
							//currentState is greater than 1
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 	 
						}	
					}
					Delay.msDelay(2000);
				}
				
				else if (in.equals("wingMirrorLeftX")){
					System.out.println("Waiting for wingMirrorLeftX...");
					
					listOne.set(2, Integer.parseInt(input.readLine()));
					
					while (true){
						
						if (currentState == 5){
							//TODO motorX.rotateTo(listOne.get(4);
							break;
						}
						
						else if ((currentState +1) % 2 == 1){
							//TODO motorX.rotateTo(listOne.get(currentState + 1));
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 
						}
						else{
							//currentState is smaller than 5
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 	 
						}	
					}
					Delay.msDelay(2000);
				}
				
				else if (in.equals("radioStation")){
					System.out.println("Waiting for radioStation...");
					
					radioStation = input.readLine();
					
					//TODO play small mp3 file if possible
					
					
					
					
					Delay.msDelay(2000); //TODO set delay to autiofile length
				}
				
				else if (in.equals("steeringWheelDepth")){
					System.out.println("Waiting for steeringWheelDepth...");
					
					listOne.set(1, Integer.parseInt(input.readLine()));
					
					while (true){
						
						if (currentState == 3){
							//TODO motorX.rotateTo(listOne.get(2);
							break;
						}
						
						else if (currentState == 1){
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction;
							//TODO motorX.rotateTo(listOne.get(2));
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 
						}
						
						else if (currentState == 5){
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction;
							//TODO motorX.rotateTo(listOne.get(2));
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction;	
						}
					}
					
					Delay.msDelay(2000);
				}
				
				else if (in.equals("steeringWheelTilt")){
					System.out.println("Waiting for steeringWheelTilt...");
					
					listOne.set(0, Integer.parseInt(input.readLine()));

					while (true){
						
						if (currentState == 1){
							//TODO motorX.rotateTo(listOne.get(0);
							break;
						}
						
						else if ((currentState -1) % 2 == 1){
							//TODO motorX.rotateTo(listOne.get(currentState - 1));
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 
						}
						else{
							//currentState is greater than 1
							//TODO stateChanger.rotate(angle that is 1 cm on lego) and right direction; 	 
						}	
					}
					Delay.msDelay(2000);
				}
				
				else if (in.equals("quit")){
					System.out.println("Closing...");
					stateChanger.close();
					mediumMotor.close();
					largeMotorOne.close();
					largeMotorTwo.close();
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