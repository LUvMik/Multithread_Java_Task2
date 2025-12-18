package lt.esdc.mtsishchykau.task2.ship;

import lt.esdc.mtsishchykau.task2.container.Container;
import lt.esdc.mtsishchykau.task2.port.Port;
import lt.esdc.mtsishchykau.task2.port.PortStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public abstract class ShipBase implements Callable<Void> {
    private static final Logger LOGGER = LogManager.getLogger(ShipBase.class);
    private static final AtomicLong ID_GEN = new AtomicLong(0);

    protected final int id;
    protected final Deque<Container> containers = new ArrayDeque<>();
    protected final Port port;
    protected final int perContainerSec;

    protected ShipBase(int id, int initialContainers, Port port, int perContainerSec) {
        this.id = id;
        this.port = port;
        this.perContainerSec = perContainerSec;

        for (int i = 0; i < initialContainers; i++) {
            containers.addLast(new Container(ID_GEN.incrementAndGet()));
        }
    }

    @Override
    public final Void call() throws Exception {
        TimeUnit.SECONDS.sleep(1);

        PortStorage storage = port.storage();

        while (!isDone()) {
            if (!canEnterPort(storage)) {
                TimeUnit.SECONDS.sleep(1);
                continue;
            }

            int berthId = port.enterPort(id);
            try {
                while (!isDone()) {
                    Container moved = moveOne(storage);
                    if (moved == null) {
                        LOGGER.info("Ship#{} nothing to move on berth#{}, leaving and retrying", id, berthId);
                        break;
                    }
                    LOGGER.info("Ship#{} moved {} on berth#{} (ship={}, storage={})",
                            id, moved, berthId, containers.size(), storage.size());
                    TimeUnit.SECONDS.sleep(perContainerSec);
                }

                if (isDone()) {
                    LOGGER.info("Ship#{} finished on berth#{}", id, berthId);
                }
            } finally {
                port.leavePort(id, berthId);
            }

            if (!isDone()) {
                TimeUnit.SECONDS.sleep(1);
            }
        }

        return null;
    }

    protected abstract boolean isDone();

    protected abstract Container moveOne(PortStorage storage) throws Exception;

    protected boolean canEnterPort(PortStorage storage) {
        return true;
    }
}
