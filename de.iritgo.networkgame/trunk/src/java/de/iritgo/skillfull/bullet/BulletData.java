package de.iritgo.skillfull.bullet;

public class BulletData
{
	public float[] x;
	public float[] y;
	public float[] rotation;
	public float[] acceleration;
	public float[] speed;
	public float[] heading;
	public boolean[] headingActive;

	public BulletData (int bullets)
	{
		x = new float[bullets];
		y = new float[bullets];
		rotation = new float[bullets];
		acceleration = new float[bullets];
		speed = new float[bullets];
		heading = new float[bullets];
		headingActive = new boolean[bullets];
	}
}
