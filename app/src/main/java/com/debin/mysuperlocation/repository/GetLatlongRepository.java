package com.debin.mysuperlocation.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.debin.mysuperlocation.model.data.direction.Direction;
import com.debin.mysuperlocation.model.data.latlong.LatLong;
import com.debin.mysuperlocation.model.data.location.LocationDetails;
import com.debin.mysuperlocation.model.network.LocationRetrofitInstance;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class GetLatlongRepository {

    private static final String TAG = "GetLatlongRepository";
    private static GetLatlongRepository latlongRepository = null;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<LatLong> latlongLiveData = new MutableLiveData<>();
    private MutableLiveData<LocationDetails> locationLiveData = new MutableLiveData<>();
    private MutableLiveData<Direction> directionsLiveData = new MutableLiveData<>();
    private LocationRetrofitInstance locationRetrofitInstance
            = LocationRetrofitInstance.getLocationRetrofitInstance();

    public static GetLatlongRepository getLatlongRepository() {
        if(latlongRepository == null){
            latlongRepository = new GetLatlongRepository();
        }
        return latlongRepository;
    }

    public MutableLiveData<LatLong> getLatlongLiveData(String address) {
        compositeDisposable.add(
                locationRetrofitInstance.getLatlongFromAddress(address)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(latlongData -> {
                    Log.d(TAG, "Latlong Data" +latlongData);
                  latlongLiveData.setValue(latlongData);
                },throwable -> {
                    Log.d(TAG, "An error occured! " + throwable.getStackTrace());
                    throwable.printStackTrace();
                        })
        );
        return latlongLiveData;
    }

    public MutableLiveData<LocationDetails> getLocationDetails(String latLng) {
        clearDisposible();
        compositeDisposable.add(
                locationRetrofitInstance.getLocationAddress(latLng)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(locationDetails -> {
                            Log.d(TAG, "Received - > "+locationDetails.toString());
                            locationLiveData.setValue(locationDetails);
                        }, throwable -> {
                            Log.d(TAG, "An error occured! " + throwable.getStackTrace());

                            throwable.printStackTrace();
                        })
        );
        return locationLiveData;
    }

    public MutableLiveData<Direction> getDirections(String mode, String routePreference,
                                                    String origin, String destination) {
        clearDisposible();
        compositeDisposable.add(
                locationRetrofitInstance.getDirections(mode, routePreference, origin, destination)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(directionDetails -> {
                    directionsLiveData.setValue(directionDetails);
                }, throwable -> {
                            Log.d(TAG, "An error occured! " + throwable.getStackTrace());
                            throwable.printStackTrace();
                        })
        );
        return directionsLiveData;
    }
    public void clearDisposible() {
        compositeDisposable.clear();
    }
}
