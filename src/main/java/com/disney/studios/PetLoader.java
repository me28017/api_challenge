package com.disney.studios;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.disney.studios.data_access.DogDAO;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads stored objects from the file system and builds up
 * the appropriate objects to add to the data source.
 *
 * Created by fredjean on 9/21/15.
 */
@Component
public class PetLoader implements InitializingBean {
    // Resources to the different files we need to load.
    @Value("classpath:data/labrador.txt")
    private Resource labradors;

    @Value("classpath:data/pug.txt")
    private Resource pugs;

    @Value("classpath:data/retriever.txt")
    private Resource retrievers;

    @Value("classpath:data/yorkie.txt")
    private Resource yorkies;

    @Autowired
    DataSource dataSource;

    /**
     * Load the different breeds into the data source after
     * the application is ready.
     *
     * @throws Exception In case something goes wrong while we load the breeds.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
    	
    	// set the dataSource
    	DogDAO.setDataSource(dataSource);

    	// create the tables
    	DogDAO.createDogBreedTable();
    	
    	DogDAO.createClientTrackingTable();

        loadBreed("Labrador", labradors);
        loadBreed("Pug", pugs);
        loadBreed("Retriever", retrievers);
        loadBreed("Yorkie", yorkies);
    }

	/**
     * Reads the list of dogs in a category and (eventually) add
     * them to the data source.
     * @param breed The breed that we are loading.
     * @param source The file holding the breeds.
     * @throws IOException In case things go horribly, horribly wrong.
	 * @throws SQLException 
     */
    private void loadBreed(String breed, Resource source) throws IOException, SQLException {
    	
    	List<String> dogs = new ArrayList<>();
 
        try ( BufferedReader br = new BufferedReader(new InputStreamReader(source.getInputStream()))) 
        {
            String line;

            while ((line = br.readLine()) != null)
            {
                //System.out.println(line);
                /* TODO: Create appropriate objects and save them to
                 *       the datasource.
                 */
                
            	dogs.add(line);
                
            }
        }

        DogDAO.insertDogsToTable(dogs, breed);
    }
}