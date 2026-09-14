package cl.translog.batch.listener;

import cl.translog.batch.domain.CuentaAnual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class CuentaAnualSkipListener
        implements SkipListener<CuentaAnual, CuentaAnual> {

    private static final Logger log =
            LoggerFactory.getLogger(CuentaAnualSkipListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        log.error(
                "[SKIP-READ] {}",
                t.getMessage()
        );
    }

    @Override
    public void onSkipInProcess(
            CuentaAnual item,
            Throwable t) {

        log.error(
                "[SKIP-PROCESS] cuentaAnual={} motivo={}",
                item.getCuentaId(),
                t.getMessage()
        );
    }

    @Override
    public void onSkipInWrite(
            CuentaAnual item,
            Throwable t) {

        log.error(
                "[SKIP-WRITE] cuentaAnual={} motivo={}",
                item.getCuentaId(),
                t.getMessage()
        );
    }
}
