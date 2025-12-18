package lt.esdc.mtsishchykau.task2.ship.impl;

import lt.esdc.mtsishchykau.task2.container.Container;
import lt.esdc.mtsishchykau.task2.port.Port;
import lt.esdc.mtsishchykau.task2.port.PortStorage;
import lt.esdc.mtsishchykau.task2.ship.ShipBase;

public class UnloadShip extends ShipBase {

    public UnloadShip(int id, int initialContainers, Port port, int perContainerSec) {
        super(id, initialContainers, port, perContainerSec);
    }

    @Override
    protected boolean isDone() {
        return containers.isEmpty();
    }

    @Override
    protected Container moveOne(PortStorage storage) throws Exception {
        Container c = containers.removeFirst();
        storage.put(c);
        return c;
    }
}
