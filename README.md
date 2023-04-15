# DroneAGTechTest

# DroneAG Technical Test Documentation

* Language: Kotlin.

* The app is using the MVVM architecture.

* The MainActivity class is the main activity of an Android application that allows users to 
draw and save polygons on a Google Map, and retrieve and display saved polygons on the 
map. 

* The MainViewModel class is responsible for managing data and providing data to the UI.

* The MainRepository class serves as an intermediary between the data source and the rest.

of the application, providing methods for inserting, retrieving and managing PolygonEntity 
objects using a PolygonDao. 

* The PolygonDatabase class is a Room database that defines the database configuration and
serves as the main access point for the application's persistent data storage. 

* The PolygonEntity class represents an entity in the polygons table of a Room database, 
with columns for id, points, centerPoint, and area. 

* The PolygonDao interface defines the methods used for data access and manipulation in 
the PolygonEntity database. 

* The PolygonUtils class provides utility functions for calculating the center point and area of 
a polygon represented as a list of LatLng points. 

* The StringUtils class provides utility functions for converting between string 
representations and LatLng objects. 

* The MapApplication class is a custom Android Application class that initializes the 
PolygonDatabase instance when the application is created. 

* Unit Test
