package de.iritgo.skillfull.pid;

public interface PidProcessor
{
	public double read ();
	
	public double write (double outReal);
}
