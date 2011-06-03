package de.iritgo.skillfull.bullet;

public class ActionData
{
	public boolean[] init;

	public boolean[] active;

	public boolean[] actionDone;

	public int[] stopTime;

	public int[] startTime;

	public float[] currentX;

	public float[] currentY;

	public float[] currentRotation;

	public float[] currentHeading;

	public float[] currentAcceleration;

	public float[] currentSpeed;

	public float[] startX;

	public float[] startY;

	public float[] startRotation;

	public float[] startHeading;

	public float[] startAcceleration;

	public float[] startSpeed;

	public float[] movedWay;

	public ActionData (int bullets)
	{
		init = new boolean[bullets];
		active = new boolean[bullets];
		actionDone = new boolean[bullets];
		startTime = new int[bullets];
		stopTime = new int[bullets];

		startX = new float[bullets];
		startY = new float[bullets];
		startRotation = new float[bullets];
		startHeading = new float[bullets];
		startAcceleration = new float[bullets];
		startSpeed = new float[bullets];

		currentX = new float[bullets];
		currentY = new float[bullets];
		currentRotation = new float[bullets];
		currentHeading = new float[bullets];
		currentAcceleration = new float[bullets];
		currentSpeed = new float[bullets];

		movedWay = new float[bullets];
}
}
