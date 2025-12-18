package lt.esdc.mtsishchykau.task2.ship.impl;

import lt.esdc.mtsishchykau.task2.container.Container;
import lt.esdc.mtsishchykau.task2.port.Port;
import lt.esdc.mtsishchykau.task2.port.PortStorage;
import lt.esdc.mtsishchykau.task2.ship.ShipBase;

public class LoadShip extends ShipBase {
    private final int targetContainers;

    public LoadShip(int id, int targetContainers, Port port, int perContainerSec) {
        super(id, 0, port, perContainerSec);
        this.targetContainers = targetContainers;
    }

    @Override
    protected boolean isDone() {
        return containers.size() >= targetContainers;
    }

    @Override
    protected Container moveOne(PortStorage storage) throws Exception {
        Container c = storage.take();
        if (c == null) {
            return null;
        }

        containers.addLast(c);
        return c;
    }
}
