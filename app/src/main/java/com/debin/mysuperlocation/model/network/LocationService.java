package com.debin.mysuperlocation.model.network;

import com.debin.mysuperlocation.model.data.direction.Direction;
import com.debin.mysuperlocation.model.data.latlong.LatLong;
import com.debin.mysuperlocation.model.data.location.LocationDetails;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationService {

   @GET("maps/api/geocode/json")
    public Observable<LatLong> getLatLong(
            @Query("address") String address,
            @Query("key") String apiKey);

    @GET("maps/api/geocode/json")
    public Observable<LocationDetails> getLocationDetails(@Query("latlng") String latLong, @Query("key") String apiKey);


    @GET("maps/api/directions/json")
    public Single<Direction> getDirections(@Query("mode") String mode,
                                           @Query("transit_routing_preference") String routingPreference,
                                           @Query("origin") String origin,
                                           @Query("destination") String destination,
                                           @Query("key") String apiKey
    );
}
