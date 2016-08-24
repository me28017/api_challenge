# API Challenge

## Dependencies

This project uses Maven for builds.
You need Java 8 installed.

## Building

```
$ mvn package
```

## Running

```
$ java -jar target/api_interview-0.1.0.jar
```

## Usage
#### List all of the available dog pictures grouped by breed

```
/rest/dog/groupByBreed

Example: http://localhost:8080/rest/dog/groupByBreed
```

#### List all of the available dog pictures of a particular breed

```
/rest/dog/getDogsForBreed?breed={breed_name}

Example: http://localhost:8080/rest/dog/getDogsForBreed?breed=pug
```

#### Vote up a dog picture
```
/rest/dog/voteUpDogPicture?id={dog_pic_id}

Example: http://localhost:8080/rest/dog/voteUpDogPicture?id=ee29vx4_labrador
```

#### Vote down a dog picture
```
/rest/dog/voteDownDogPicture?id={dog_pic_id}

Example: http://localhost:8080/rest/dog/voteDownDogPicture?id=ee29vx4_labrador
```

#### Get the details associated with a dog picture
```
/rest/dog/getDogPicture?id={dog_pic_id}

Example: http://localhost:8080/rest/dog/getDogPicture?id=ee29vx4_labrador
````

## Notes
- All parameter values are case-insensitive
- For simplicity of testing, all endpoints are avaiable via a GET request
- A vote for a particular dog picture may only be cast once per IP address, for the lifetime of your JVM.
