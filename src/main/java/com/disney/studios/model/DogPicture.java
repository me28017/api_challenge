package com.disney.studios.model;

public class DogPicture
{
	String id;
	String url;
	String breed;
	int numLikes = 0;

	/**
	 * @param id
	 * @param url
	 * @param breed
	 * @param numLikes
	 */
	public DogPicture(String id, String url, String breed, int numLikes)
	{
		super();
		this.id = id;
		this.url = url;
		this.breed = breed;
		this.numLikes = numLikes;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * @return the breed
	 */
	public String getBreed()
	{
		return breed;
	}

	/**
	 * @param breed the breed to set
	 */
	public void setBreed(String breed)
	{
		this.breed = breed;
	}

	/**
	 * @return the numLikes
	 */
	public int getNumLikes()
	{
		return numLikes;
	}

	/**
	 * @param numLikes the numLikes to set
	 */
	public void setNumLikes(int numLikes)
	{
		this.numLikes = numLikes;
	}	
}
