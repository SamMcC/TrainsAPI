package com.bjss.trainsapi.services;

import java.io.IOException;

public interface IActorInitialisationService {
    void initialiseTrains() throws IOException;
    void initialiseStations() throws IOException;
}
