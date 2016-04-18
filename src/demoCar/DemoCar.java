package demoCar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.robotics.RegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

import java.io.File;
import static java.lang.System.out;
import lejos.hardware.Sound;

public class DemoCar{
	public static void main(String[] args) throws IOException {
		
		
		try{
			boolean ongoing = true;
			String in;
			
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
			 * 		       seatDepth. positive input -> seat moves backwards (700 max) default 0
			 * motorThree: steering wheel depth, positive input -> swd moves backwards (700 max) default 340
			 * 			   leftMirror, positive input -> mirror moves ... (max +-200) default 0
			 * 			   steering wheel tilt, positive input -> tilt moves downwards (-2100 max) default 0
			 * 
			 * 
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
			System.out.println("W8 4 ctrlr 2 start...");
			Socket servSock = serverSocket.accept();
			System.out.println("Ctrlr started...");
			
			RegulatedMotor stateChanger = new EV3MediumRegulatedMotor(MotorPort.A);
			RegulatedMotor motorOne = new EV3LargeRegulatedMotor(MotorPort.B);
			RegulatedMotor motorTwo = new EV3LargeRegulatedMotor(MotorPort.C);
			RegulatedMotor motorThree = new EV3MediumRegulatedMotor(MotorPort.D);
				
			stateChanger.setSpeed(100);
			motorOne.setSpeed(100);
			motorTwo.setSpeed(100);
			motorThree.setSpeed(100);

			BufferedReader input = new BufferedReader(new InputStreamReader(servSock.getInputStream()));
			
			while (ongoing){ 
				
				System.out.println("W8ing 4 input...");
				in = input.readLine(); //reads what to change, radio (that needs a unique handling) or one of the motors
									   //or if the program has to quit
				
				if (in.equals("radioStation")){
					File soundFile = new File("abc.wav");
					out.println(Sound.playSample(soundFile));
				}
				
				else if (in.equals("quit")){
					System.out.println("Ending program...");
					Delay.msDelay(1000);
					stateChanger.close();
					motorOne.close();
					motorTwo.close();
					motorThree.close();
					servSock.close();
					ongoing = false;
				}
				
				else{

					//the stateChanger moves the gears so they are connected to the right things (seatBackAngle, leftMirror,...)
					
					currentStateChangerAngle = rotateStateChanger(Integer.parseInt(in), stateChanger, currentStateChangerAngle, motorOne, motorTwo, motorThree, currentSeatBackAngle, currentSeatDepth, currentLeftMirror, currentRightMirror, currentSteeringWheelDepth, currentSteeringWheelTilt, motorOneFunctionOne, motorOneFunctionTwo, MotorTwoFunctionThree);

					/*
					 * depending on which state the gears are on, the 3 motors, that are used to drive the cars functions
					 * are assigned the their current angles since that doesn't happen automatically, you can tell the
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
					
					in = input.readLine(); //reads which motor to should rotate for the following angle
					
					/*
					 * Depending on the current stateChanger state and the motor that has to rotate,
					 * the different things in the car are moved and their angles are saved in their
					 * respective variables
					 * 
					 * The "input" variable send as argument is just a bufferedReader
					 */
					
					if (in.equals("1")){
						currentRightMirror = rotateMotor(input, motorOne, 10); 
					}
					else if (in.equals("2")){
						if (currentStateChangerAngle == 0){
							currentSeatBackAngle = rotateMotor(input, motorTwo, 0);
						}
						else if (currentStateChangerAngle == 160){
							currentSeatDepth = rotateMotor(input, motorTwo, 70);
						}
						
					}
					else if (in.equals("3")){
						if (currentStateChangerAngle == 0){
							currentSteeringWheelDepth = rotateMotor(input, motorThree, 70);
						}
						else if (currentStateChangerAngle == 160){
							currentLeftMirror = rotateMotor(input, motorThree, 10);
						}
						else if (currentStateChangerAngle == 320)
							motorThree.setSpeed(200);
							currentSteeringWheelTilt = rotateMotor(input, motorThree, -210);
							motorThree.setSpeed(100);
					}
				}
			}			
		}
		catch (Exception e){
			e.printStackTrace();
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
	 * Rotates a motor with the given input angle and sends back a converted version of the input angle value
	 * The 'factor' parameter is needed since the motors have to rotate more degrees than the inputAngle,
	 * to reach the desired positions
	 */
	
	public static int rotateMotor(BufferedReader input, RegulatedMotor motor, int factor){
		try{
			int inputAngle = Integer.parseInt(input.readLine());
			if (factor == 0){
				if (inputAngle < 0 || inputAngle > 0){
					motor.rotate(inputAngle * 12);
					return calculateCurrentFunctionMotorAngle(inputAngle);
				}
				else if (inputAngle == 0){
					motor.rotate(inputAngle);
					return calculateCurrentFunctionMotorAngle(inputAngle);
				}
			}
			else{
				motor.rotate(inputAngle * factor);
				return calculateCurrentFunctionMotorAngle(inputAngle);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	/*
	 * This method rotates the stateChanger and takes care of the other motors angles so that no
	 * gears crash into each other while moving between the states
	 */
	
	public static int rotateStateChanger(int desiredAngleInput, RegulatedMotor stateChanger, int currentAngle, RegulatedMotor motorOne, RegulatedMotor motorTwo, RegulatedMotor motorThree, int sba, int sd, int lm, int rm, int swd, int swt, int mofo, int moft, int mtfth){

		if (desiredAngleInput < currentAngle){
			
			while (true){
				
				if (desiredAngleInput == currentAngle){
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
		else if (desiredAngleInput > currentAngle){
			
			while (true){
				
				if (desiredAngleInput == currentAngle){
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
		return 0;
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
