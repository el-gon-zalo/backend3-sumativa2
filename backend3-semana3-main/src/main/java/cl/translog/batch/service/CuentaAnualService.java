package cl.translog.batch.service;

import cl.translog.batch.domain.CuentaAnual;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuentaAnualService {

    private final JdbcTemplate jdbcTemplate;

    public CuentaAnualService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // GET por ID
    public CuentaAnual obtenerPorId(Long id) {

        String sql = """
                SELECT
                    id,
                    cuenta_id,
                    fecha,
                    transaccion,
                    monto,
                    descripcion
                FROM cuentaAnual_processed
                WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    CuentaAnual cuenta = new CuentaAnual();

                    cuenta.setId(rs.getLong("id"));
                    cuenta.setCuentaId(rs.getInt("cuenta_id"));
                    cuenta.setFecha(rs.getString("fecha"));
                    cuenta.setTransaccion(rs.getString("transaccion"));
                    cuenta.setMonto(rs.getDouble("monto"));
                    cuenta.setDescripcion(rs.getString("descripcion"));

                    return cuenta;
                },
                id
        );
    }

    // GET ALL
    public List<CuentaAnual> obtenerTodos() {

        String sql = """
                SELECT
                    id,
                    cuenta_id,
                    fecha,
                    transaccion,
                    monto,
                    descripcion
                FROM cuentaAnual_processed
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    CuentaAnual cuenta = new CuentaAnual();

                    cuenta.setId(rs.getLong("id"));
                    cuenta.setCuentaId(rs.getInt("cuenta_id"));
                    cuenta.setFecha(rs.getString("fecha"));
                    cuenta.setTransaccion(rs.getString("transaccion"));
                    cuenta.setMonto(rs.getDouble("monto"));
                    cuenta.setDescripcion(rs.getString("descripcion"));

                    return cuenta;
                }
        );
    }

    
    public List<CuentaAnual> obtenerPorCuentaId(Integer cuentaId) {

        String sql = """
                SELECT
                    id,
                    cuenta_id,
                    fecha,
                    transaccion,
                    monto,
                    descripcion
                FROM cuentaAnual_processed
                WHERE cuenta_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    CuentaAnual cuenta = new CuentaAnual();

                    cuenta.setId(rs.getLong("id"));
                    cuenta.setCuentaId(rs.getInt("cuenta_id"));
                    cuenta.setFecha(rs.getString("fecha"));
                    cuenta.setTransaccion(rs.getString("transaccion"));
                    cuenta.setMonto(rs.getDouble("monto"));
                    cuenta.setDescripcion(rs.getString("descripcion"));

                    return cuenta;
                },
                cuentaId
        );
    }
}
