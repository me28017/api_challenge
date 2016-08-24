package com.disney.studios.model;

public class DogException extends Exception
{
	private static final long serialVersionUID = 8059863322960154601L;

	public DogException(Throwable cause)
	{
		super(cause);
	}

	public DogException(String msg)
	{
		super(msg);
	}
}
