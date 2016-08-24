package com.disney.studios.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.disney.studios.data_access.DogDAO;
import com.disney.studios.model.DogException;
import com.disney.studios.model.DogPicture;

@RestController
@RequestMapping(value = "/rest/dog")
public class DogController
{
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public DogPicture getDetails(@RequestParam(value = "id") long id)
	{
		return null;
	}

	@RequestMapping(value = "/groupByBreed", method = RequestMethod.GET)
	public List<DogPicture> groupByBreed() throws DogException
	{	
		return DogDAO.getDogsGroupedByBreed();	
	}

	@RequestMapping(value = "/getDogsForBreed", method = RequestMethod.GET)
	public List<DogPicture> getDogsForBreed(@RequestParam(value = "breed") String breed) throws DogException
	{
		return DogDAO.getDogsForBreed(breed);	
	}

	@RequestMapping(value = "/getDogPicture", method = RequestMethod.GET)
	public DogPicture getDogPicture(@RequestParam(value = "id") String id) throws DogException
	{
		return DogDAO.getDogPicture(id);	
	}

	@RequestMapping(value = "/voteUpDogPicture", method = RequestMethod.GET)
	public void voteUpDogPicture(@RequestParam(value = "id") String id,
			HttpServletRequest request) throws DogException
	{
		DogDAO.handleUpVote(request.getRemoteAddr(), id);
	}
	
	@RequestMapping(value = "/voteDownDogPicture", method = RequestMethod.GET)
	public void voteDownDogPicture(@RequestParam(value = "id") String id,
			HttpServletRequest request) throws DogException
	{
		DogDAO.handleDownVote(request.getRemoteAddr(), id);
	}

	@RequestMapping(value = "/error")
	public String handleError(DogException de)
	{
		return de.getMessage();
	}
}

