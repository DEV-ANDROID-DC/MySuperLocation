package com.debin.mysuperlocation.model.network;

import android.util.Log;
import com.debin.mysuperlocation.model.data.direction.Direction;
import com.debin.mysuperlocation.model.data.latlong.LatLong;
import com.debin.mysuperlocation.model.data.location.LocationDetails;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.debin.mysuperlocation.util.Constants.API_KEY;
import static com.debin.mysuperlocation.util.Constants.BASE_URL;

public class LocationRetrofitInstance {

    private static final String TAG = "LocationRetrofitInstance";
    private static LocationRetrofitInstance locationRetrofitInstance = null;
    private LocationService locationService;

    public LocationRetrofitInstance() {
        locationService = createLocationService(getRetrofitInstance());
    }

    public static LocationRetrofitInstance getLocationRetrofitInstance() {
        if(locationRetrofitInstance == null) {
            locationRetrofitInstance = new LocationRetrofitInstance();
        }
        return locationRetrofitInstance;
    }

    private Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private LocationService createLocationService(Retrofit retrofitInstance) {
       return retrofitInstance.create(LocationService.class);
    }

    public Observable<LatLong> getLatlongFromAddress(String address) {
        return locationService.getLatLong(address, API_KEY);
    }

    public Observable<LocationDetails> getLocationAddress(String latLng) {
        return locationService.getLocationDetails(latLng, API_KEY);
    }

    public Single<Direction> getDirections(String mode, String routingPreference,
                                           String origin, String destination) {
        return locationService.getDirections(mode, routingPreference, origin, destination, API_KEY);
    }

    private static OkHttpClient okHttpClient(){
        return new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor()) // for logging
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }



    private static HttpLoggingInterceptor httpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(message -> Log.d(TAG, "log: http log: " + message));
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }


}
