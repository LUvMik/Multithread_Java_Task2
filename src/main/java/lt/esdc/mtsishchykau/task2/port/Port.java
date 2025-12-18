package lt.esdc.mtsishchykau.task2.port;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Port {
    private static final Logger LOGGER = LogManager.getLogger(Port.class);
    private final int waitNoBerthSec;

    private final Semaphore berths;
    private final PortStorage storage;

    private final int berthCount;
    private final AtomicInteger berthSeq = new AtomicInteger(0);


    public Port(int berthCount, int waitNoBerthSec, PortStorage storage) {
        if (berthCount <= 0) throw new IllegalArgumentException("berthCount");
        this.berths = new Semaphore(berthCount, true);
        this.berthCount = berthCount;
        this.waitNoBerthSec = waitNoBerthSec;
        this.storage = storage;

        LOGGER.info("Port created. berths={}", berthCount);
    }

    public PortStorage storage() {
        return storage;
    }

    public int enterPort(int shipId) throws InterruptedException {
        while (true) {
            boolean ok = berths.tryAcquire();
            if (ok) {
                int berthId = (berthSeq.incrementAndGet() % berthCount) + 1;
                LOGGER.info("Ship#{} entered port. berth#{} freeBerths={}", shipId, berthId, berths.availablePermits());
                return berthId;
            }
            LOGGER.info("Ship#{} no free berth, wait {}s", shipId, waitNoBerthSec);
            TimeUnit.SECONDS.sleep(waitNoBerthSec);
        }
    }

    public void leavePort(int shipId, int berthId) {
        berths.release();
        LOGGER.info("Ship#{} left port. berth#{} freeBerths={}", shipId, berthId, berths.availablePermits());
    }
}
