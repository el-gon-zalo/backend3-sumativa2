package cl.translog.batch.listener;

import cl.translog.batch.domain.Transaccion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class TransaccionSkipListener
        implements SkipListener<Transaccion, Transaccion> {

    private static final Logger log =
            LoggerFactory.getLogger(TransaccionSkipListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        log.error(
                "[SKIP-READ] {}",
                t.getMessage()
        );
    }

    @Override
    public void onSkipInProcess(
            Transaccion item,
            Throwable t) {

        log.error(
                "[SKIP-PROCESS] transaccion={} motivo={}",
                item.getId(),
                t.getMessage()
        );
    }

    @Override
    public void onSkipInWrite(
            Transaccion item,
            Throwable t) {

        log.error(
                "[SKIP-WRITE] transaccion={} motivo={}",
                item.getId(),
                t.getMessage()
        );
    }
}
