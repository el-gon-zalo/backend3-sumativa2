package cl.translog.batch.service;

import cl.translog.batch.domain.Transaccion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransaccionService {

    private final JdbcTemplate jdbcTemplate;

    public TransaccionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // GET por ID
    public Transaccion obtenerPorId(Long id) {

        String sql = """
                SELECT
                    id,
                    fecha,
                    monto,
                    tipo,
                    estado
                FROM transaccion_processed
                WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    Transaccion transaccion = new Transaccion();

                    transaccion.setId(rs.getInt("id"));
                    transaccion.setFecha(rs.getString("fecha"));
                    transaccion.setMonto(rs.getDouble("monto"));
                    transaccion.setTipo(rs.getString("tipo"));
                    transaccion.setEstado(rs.getString("estado"));

                    return transaccion;
                },
                id
        );
    }

    // GET ALL
    public List<Transaccion> obtenerTodos() {

        String sql = """
                SELECT
                     id,
                    fecha,
                    monto,
                    tipo,
                    estado
                FROM transaccion_processed
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Transaccion transaccion = new Transaccion();

                    transaccion.setId(rs.getInt("id"));
                    transaccion.setFecha(rs.getString("fecha"));
                    transaccion.setMonto(rs.getDouble("monto"));
                    transaccion.setTipo(rs.getString("tipo"));
                    transaccion.setEstado(rs.getString("estado"));

                    return transaccion;
                }
        );
    }

}
