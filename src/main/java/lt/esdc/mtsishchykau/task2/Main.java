package lt.esdc.mtsishchykau.task2;

import lt.esdc.mtsishchykau.task2.port.Port;
import lt.esdc.mtsishchykau.task2.port.PortStorage;
import lt.esdc.mtsishchykau.task2.ship.ShipBase;
import lt.esdc.mtsishchykau.task2.ship.impl.LoadShip;
import lt.esdc.mtsishchykau.task2.ship.impl.UnloadShip;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public final class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private static final int BERTHS = 2;
    private static final int SHIPS_CONTAINER = 10;
    private static final int UNLOAD_SHIPS = 4;
    private static final int LOAD_SHIPS = 4;
    private static final int WAIT_NO_BERTH_SEC = 4;
    private static final int PER_CONTAINER_SEC = 1;

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        PortStorage storage = new PortStorage();
        Port port = new Port(BERTHS, WAIT_NO_BERTH_SEC, storage);

        List<ShipBase> unloadShips = new ArrayList<>();
        for (int i = 1; i <= UNLOAD_SHIPS; i++) {
            unloadShips.add(new UnloadShip(i, SHIPS_CONTAINER, port, PER_CONTAINER_SEC));
        }
        List<ShipBase> loadShips = new ArrayList<>();
        for (int i = 1; i <= LOAD_SHIPS; i++) {
            loadShips.add(new LoadShip(UNLOAD_SHIPS + i, SHIPS_CONTAINER, port, PER_CONTAINER_SEC));
        }

        try (var pool = Executors.newFixedThreadPool(8)) {
            LOGGER.info("Simulation start");

            ArrayList<ShipBase> ships = new ArrayList<>(loadShips);
            ships.addAll(unloadShips);
            Collections.shuffle(ships);

            pool.invokeAll(ships);
        }

        LOGGER.info("Simulation finish. storage={}", storage.size());
    }
}
