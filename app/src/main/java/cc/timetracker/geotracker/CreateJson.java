package cc.timetracker.geotracker;

/**
 * Created by admin on 26.04.17.
 */

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class CreateJson {
    private static final String TAG = CreateJson.class.getSimpleName();

    private Position position;
    private Gson gson;
    private JsonObject feature;



    public CreateJson(Position position) {
        this.position = position;
    }



    public void create() {


        /******* Feature Structue ******/
        /*
        {
          "type": "Feature",
          "properties": [
            {
              "id": 1,
              "status": "Person",
              "created_at": 1,
              "longitude": 1,
              "latitude": 1,
              "provider": "GPS",
              "marker-color": 1,
              "gps_altitude": 1,
              "gps_speed": 1,
              "gps_course": 1,
              "device_battery": 1,
              "temperature": 1,
              "dew_point": 1,
              "humidity": 1,
              "precipitation": 1,
              "wind_speed": 1,
              "wind_gust": 1,
              "wind_direction": 1,
              "pressure": 1
            }
          ],
          "geometry": [
            {
              "type": "Point",
              "coordinates": [1,1]
            }
          ]
        }
        */



        /****** create an object called properties ****/
        JsonObject properties_content = new JsonObject();
        properties_content.addProperty("id", position.getDeviceId());
        properties_content.addProperty("status", "Person");
        properties_content.addProperty("created_at", position.getTime().toString());
        properties_content.addProperty("longitude", position.getLongitude());
        properties_content.addProperty("latitude", position.getLatitude());
        properties_content.addProperty("marker-color", "#cc0000");
        properties_content.addProperty("gps_altitude", position.getAltitude());
        properties_content.addProperty("gps_speed", position.getSpeed());
        properties_content.addProperty("gps_course", position.getCourse());
        properties_content.addProperty("device_battery", position.getBattery());

//        properties_content.addProperty("temperature", 1);
//        properties_content.addProperty("dew_point", 1);
//        properties_content.addProperty("humidity", 1);
//        properties_content.addProperty("precipitation", 1);
//        properties_content.addProperty("wind_speed", 1);
//        properties_content.addProperty("wind_gust", 1);
//        properties_content.addProperty("wind_direction", 1);
//        properties_content.addProperty("pressure", 1);



        /****** create an array called coordinates ****/
        JsonArray coordinates_content = new JsonArray();
        coordinates_content.add(position.getLongitude());
        coordinates_content.add(position.getLatitude());


        /****** create an object called geometry ****/
        JsonObject geometry_content = new JsonObject();
        geometry_content.addProperty("type", "Point");
        geometry_content.add("coordinates", coordinates_content);




        // create the feature object
        feature = new JsonObject();

        // add a properties and geometry to the feature object
        feature.addProperty("type", "Feature");
        feature.add("properties", properties_content);
        feature.add("geometry", geometry_content);




        // create the gson using the GsonBuilder. Set pretty printing on. Allow
        // serializing null and set all fields to the Upper Camel Case
        gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        //Log.d(TAG, "Feature to send " + gson.toJson(feature));
    }




    public String getJsonAsString() {
        return gson.toJson(feature);
    }
}