package ua.com.d_garage.deutschegarage.data.service.executor;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public class ShutdownExecutor implements Executor {

    private final Executor executor;
    private final AtomicBoolean shutdown = new AtomicBoolean();

    public ShutdownExecutor(@NonNull Executor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(@NonNull Runnable command) {
        if (shutdown.get()) {
            return;
        }
        executor.execute(() -> {
            if (shutdown.get()) {
                return;
            }
            command.run();
        });
    }

    public void shutdown() {
        shutdown.set(true);
    }

}
