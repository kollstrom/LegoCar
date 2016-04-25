package demoCar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.robotics.RegulatedMotor;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

import java.io.File;

public class DemoCar{
	
	private static int oldSteeringWheelTilt = 0;
	private static int oldSteeringWheelDepth = -600;
	private static int oldSeatBackAngle = -1260;
	private static int oldSeatDepth = 0;
	private static int oldMirrorLeft = 0;
	private static int oldMirrorRight = 0;
	
	public static void main(String[] args) throws IOException {
		
		try{
			String in, newAngle;
			int stateChangerAngleNeeded = 0, motorToRotate = 1;
			File song = new File("good.wav");
			
			/*
			 * LegoCar default values (angles) and information:
			 * 
			 * the input angles can be higher than 360, but are used in another way:
			 * to make the stateChanger work and to prevent the gears from getting stuck in each other the
			 * input angles are calculated as follows:
			 * 
			 * an input angle of  320 = 320
			 * an input angle of  370 = 10    (360 + 10)
			 * an input angle of -730 = 350  -(-360 - 360 - 10)
			 * 
			 * this is necessary to move the motor to the right angle when the states are changed, remember
			 * that the motor method "rotateTo" uses angles from 0 - 360 to find the right position 
			 * 
			 * motorOne:   mirror right, positive input -> mirror moves backwards (max values +-250) default 0
			 * motorTwo:   seatBack, positive input -> seatBack moves forward (1600 max) default 0
			 * 		       seatDepth. positive input -> seat moves backwards (600 max) default 0
			 * motorThree: steering wheel depth, positive input -> swd moves backwards (700 max) default 340
			 * 			   leftMirror, positive input -> mirror moves ... (max +-200) default 0
			 * 			   steering wheel tilt, positive input -> tilt moves downwards (-2100 max) default 0
			 * 
			 * remember, the default angles change with motorspeed
			 */
			
			
			
			int currentSeatBackAngle = 0,
				currentSeatDepth = 0, 
				currentLeftMirror = 0,
				currentRightMirror = 0,
				currentSteeringWheelDepth = 340,
				currentSteeringWheelTilt = 0,
				
				currentStateChangerAngle = 0,
				currentMotorOneAngle = 0,
				currentMotorTwoAngle = 0,
				currentMotorThreeAngle = 340,
				
				
				// Empty state slots, needed so the stateChanger works as intended 
				
				motorOneFunctionOne = 0,
				motorOneFunctionTwo = 0,
				MotorTwoFunctionThree = 0;
			
			//The legoCar starts a server and waits for input from the Simulator/App
			
			System.out.println("Starting program...");
			ServerSocket serverSocket = new ServerSocket(4321);
			System.out.println("W8 4ctrlr2 start...");
			Socket servSock = serverSocket.accept();
			System.out.println("Ctrlr started...");
			
			RegulatedMotor stateChanger = new EV3MediumRegulatedMotor(MotorPort.A);
			RegulatedMotor motorOne = new EV3LargeRegulatedMotor(MotorPort.B);
			RegulatedMotor motorTwo = new EV3LargeRegulatedMotor(MotorPort.C);
			RegulatedMotor motorThree = new EV3MediumRegulatedMotor(MotorPort.D);
				
			stateChanger.setSpeed(200);
			motorOne.setSpeed(200);
			motorTwo.setSpeed(200);
			motorThree.setSpeed(200);

			BufferedReader input = new BufferedReader(new InputStreamReader(servSock.getInputStream()));

			while (true){ 
						
				System.out.println("W8ing 4 input...");
				in = input.readLine();
				System.out.println("Changing " + in);
				
				//reads what to change, radio (that needs a unique handling) or one of the motors
				//or if the program has to quit
				
				if (in.equals("radioStation")){
					in = input.readLine();
					System.out.println(in);
					if (in.equals("P1")){
						song = new File("good.wav");
					}
					else if (in.equals("P2")){
						song = new File("batman.wav");
					}
					else if (in.equals("P3")){
						song = new File("empire.wav");
					}
					else if (in.equals("P4")){
						song = new File("less.wav");
					}
					else if (in.equals("P5")){
						song = new File("hello.wav");
					}
					else if (in.equals("P6")){
						song = new File("highway.wav");
					}
					
					playSong(song);

					
				}
				
				else if (in.equals("quit")){
					System.out.println("Ending program...");
					Delay.msDelay(1000);
					stateChanger.close();
					motorOne.close();
					motorTwo.close();
					motorThree.close();
					servSock.close();
					break;
				}
				
				else{
					
					if (in.equals("seatBackAngle")){
						stateChangerAngleNeeded = 0;
						motorToRotate = 2;
					}
					else if (in.equals("seatDepth")){
						stateChangerAngleNeeded = 160;
						motorToRotate = 2;
					}
					else if (in.equals("wingMirrorRightY")){
						stateChangerAngleNeeded = 320;
						motorToRotate = 1;
					}
					else if (in.equals("wingMirrorLeftY")){
						stateChangerAngleNeeded = 160;
						motorToRotate = 3;
					}
					else if (in.equals("steeringWheelDepth")){
						stateChangerAngleNeeded = 0;
						motorToRotate = 3;
					}
					else if (in.equals("steeringWheelTilt")){
						stateChangerAngleNeeded = 320;
						motorToRotate = 3;
					}
					
					//the stateChanger moves the gears so they are connected to the right things (seatBackAngle, leftMirror,...)
					currentStateChangerAngle = rotateStateChanger(stateChangerAngleNeeded, stateChanger, currentStateChangerAngle, motorOne, motorTwo, motorThree, currentSeatBackAngle, currentSeatDepth, currentLeftMirror, currentRightMirror, currentSteeringWheelDepth, currentSteeringWheelTilt, motorOneFunctionOne, motorOneFunctionTwo, MotorTwoFunctionThree);
					
					/*
					 * depending on which state the gears are on, the 3 motors, that are used to drive the cars functions
					 * are assigned to their current angles since that doesn't happen automatically, you can tell the
					 * motors where rotate to, but they can't tell you where they are at the moment
					 */

					if (currentStateChangerAngle == 0){
						currentMotorOneAngle = motorOneFunctionOne;
						currentMotorTwoAngle = currentSeatBackAngle;
						currentMotorThreeAngle = currentSteeringWheelDepth;
					}
					else if (currentStateChangerAngle == 160){
						currentMotorOneAngle = motorOneFunctionTwo;
						currentMotorTwoAngle = currentSeatDepth;
						currentMotorThreeAngle = currentLeftMirror;
					}
					else if (currentStateChangerAngle == 320){
						currentMotorOneAngle = currentRightMirror;
						currentMotorTwoAngle = MotorTwoFunctionThree;
						currentMotorThreeAngle = currentSteeringWheelTilt;
					}

					newAngle = input.readLine(); //read which angle to change to
					
					/*
					 * Depending on the current stateChanger state and the motor that has to rotate,
					 * the different things in the car are moved and their angles are saved in their
					 * respective variables
					 * 
					 * The "input" variable send as argument is just a bufferedReader
					 */
					
					if (motorToRotate == 1){
						currentRightMirror = changeRightMirror(Integer.parseInt(newAngle), motorOne); 
					}
					else if (motorToRotate == 2){
						if (currentStateChangerAngle == 0){
							currentSeatBackAngle = changeSeatBackAngle(Integer.parseInt(newAngle), motorTwo);
						}
						else if (currentStateChangerAngle == 160){
							currentSeatDepth = changeSeatDepth(Integer.parseInt(newAngle), motorTwo);
						}
					}
					else if (motorToRotate == 3){
						if (currentStateChangerAngle == 0){
							currentSteeringWheelDepth = changeSteeringWheelDepth(Integer.parseInt(newAngle), motorThree);
						}
						else if (currentStateChangerAngle == 160){
							currentLeftMirror = changeLeftMirror(Integer.parseInt(newAngle), motorThree);
						}
						else if (currentStateChangerAngle == 320){
							currentSteeringWheelTilt = changeSteeringWheelTilt(Integer.parseInt(newAngle), motorThree);
						}
					}
				}				
			}			
		}
		catch (Exception e){
			System.out.println(e);
			Delay.msDelay(10000);
			
		}
	}
	
	/*
	 * This method plays a part of a song that located on the EV3 brick, it is played when the
	 * "radio" is changed
	 */
	
	private static void playSong(File song) {
		Sound.setVolume(50);
		Sound.playSample(song);
		try{
			Thread.sleep(Sound.getTime());
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	/* 
	 * Calculates an incoming angle value to 0-360 degree value and returns it
	 */

	public static int calculateCurrentFunctionMotorAngle(int angle){
		while (true){
			if (angle >= 360){
				angle -= 360;
			}
			else if (angle < 0){
				angle += 360;
			}
			else{
				return angle;
			}
		}
	}
	
	/*
	 * The following 6 methods are used to calculate the exact amount degrees the motors have to
	 * turn and in which direction (+/-), where the incoming angle (new angle) is multiplied
	 * by a factor so that the i.e. seats turning is equal to the desired input from the app
	 */
	
	public static int changeSeatBackAngle(int newAngle, RegulatedMotor motor){
		newAngle *= 14;
		int returnValue = rotateMotor(motor, newAngle - oldSeatBackAngle);
		oldSeatBackAngle = newAngle;
		return returnValue;
	}
	
	public static int changeSeatDepth(int newAngle, RegulatedMotor motor){
		newAngle *= 70;
		if (newAngle > oldSeatDepth){
			int returnValue = rotateMotor(motor, newAngle - oldSeatDepth);
			oldSeatDepth = newAngle;
			return returnValue;
		}
		int returnValue = rotateMotor(motor, oldSeatDepth - newAngle);
		oldSeatDepth = newAngle;
		return returnValue;
		
	}
	
	public static int changeSteeringWheelTilt(int newAngle, RegulatedMotor motor){
		newAngle *= -210;
		int returnValue = rotateMotor(motor, newAngle - oldSteeringWheelTilt);
		oldSteeringWheelTilt = newAngle;
		return returnValue;
	}
	
	public static int changeLeftMirror(int newAngle, RegulatedMotor motor){
		newAngle *= 14;
		int returnValue = rotateMotor(motor, newAngle - oldMirrorLeft);
		oldMirrorLeft = newAngle;
		return returnValue;
	}
	
	public static int changeRightMirror(int newAngle, RegulatedMotor motor){
		newAngle *= 14;
		int returnValue = rotateMotor(motor, newAngle - oldMirrorRight);
		oldMirrorRight = newAngle;
		return returnValue;
	}
	
	public static int changeSteeringWheelDepth(int newAngle, RegulatedMotor motor){
		newAngle *= -60;
		int returnValue = rotateMotor(motor, newAngle - oldSteeringWheelDepth);
		oldSteeringWheelDepth = newAngle;
		return returnValue;
	}
	
	
	
	
	public static int rotateMotor(RegulatedMotor motor, int angle){
		
			motor.rotate(angle);
			return calculateCurrentFunctionMotorAngle(angle);
	}
	
	/*
	 * This method rotates the stateChanger and takes care of the other motors angles so that no
	 * gears crash into each other while moving between the states
	 */
	
	public static int rotateStateChanger(int requiredAngle, RegulatedMotor stateChanger, int currentAngle, RegulatedMotor motorOne, RegulatedMotor motorTwo, RegulatedMotor motorThree, int sba, int sd, int lm, int rm, int swd, int swt, int mofo, int moft, int mtfth){

		if (requiredAngle < currentAngle){
			
			while (true){
				
				if (requiredAngle == currentAngle){
					return currentAngle;
				}
				else if (currentAngle == 320 || currentAngle == 160){ //if the state is one of the two given angles, the stateChanger can rotate to the next state without changing anything else since the next states are empty anyway
					stateChanger.rotate(-80);
					currentAngle -= 80;
				}
				else if (currentAngle == 240){ //before the state changer can rotate to the next state, all the other motors have to adjust/prepare themselves to the angle that is valid for the next state
					rotateMotors(stateChanger, motorOne, motorTwo, motorThree, -80, moft, sd, lm);
					currentAngle -= 80;
				}
				else if (currentAngle == 80){ //before the state changer can rotate to the next state, all the other motors have to adjust/prepare themselves to the angle that is valid for the next state
					rotateMotors(stateChanger, motorOne, motorTwo, motorThree, -80, mofo, sba, swd);
					currentAngle -= 80;
				}
			}
		}
		else if (requiredAngle > currentAngle){
			
			while (true){
				
				if (requiredAngle == currentAngle){
					return currentAngle;
				}
				else if (currentAngle == 0 || currentAngle == 160){ //if the state is one of the two given angles, the stateChanger can rotate to the next state without changing anything else since the next states are empty anyway
					stateChanger.rotate(80);
					currentAngle += 80;
				}
				else if (currentAngle == 240){ //before the state changer can rotate to the next state, all the other motors have to adjust/prepare themselves to the angle that is valid for the next state 
					rotateMotors(stateChanger, motorOne, motorTwo, motorThree, 80, rm, mtfth, swt);
					currentAngle += 80;
				}
				else if (currentAngle == 80){ //before the state changer can rotate to the next state, all the other motors have to adjust/prepare themselves to the angle that is valid for the next state
					rotateMotors(stateChanger, motorOne, motorTwo, motorThree, 80, moft, sd, lm);
					currentAngle += 80;
				}
			}
		}
		return requiredAngle;
	}
	
	/*
	 * Rotates all the motors to the given angles, this methods purpose is to avoid duplicated code only
	 */
	
	public static void rotateMotors(RegulatedMotor stateChanger, RegulatedMotor motorOne, RegulatedMotor motorTwo, RegulatedMotor motorThree, int stateAngle, int angleOne, int angleTwo, int angleThree){
		motorOne.rotateTo(angleOne);
		motorTwo.rotateTo(angleTwo);
		motorThree.rotateTo(angleThree);
		stateChanger.rotate(stateAngle);
	}
}
