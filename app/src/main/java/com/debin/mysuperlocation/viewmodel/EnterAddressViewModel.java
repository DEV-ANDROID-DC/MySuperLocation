package com.debin.mysuperlocation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.debin.mysuperlocation.model.data.direction.Direction;
import com.debin.mysuperlocation.model.data.latlong.LatLong;
import com.debin.mysuperlocation.model.data.location.LocationDetails;
import com.debin.mysuperlocation.repository.GetLatlongRepository;

public class EnterAddressViewModel extends ViewModel {

    private GetLatlongRepository latlongRepository = GetLatlongRepository.getLatlongRepository();

    public LiveData<LatLong> getLatlong(String address) {
        return latlongRepository.getLatlongLiveData(address);
    }

    public LiveData<LocationDetails> getLocationDetails(String latLng){
        return latlongRepository.getLocationDetails(latLng);
    }

    public LiveData<Direction> getDirections(String mode, String routePreference,
                                             String origin, String destination) {
        return latlongRepository.getDirections(mode, routePreference, origin, destination);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        latlongRepository.clearDisposible();
    }
}
