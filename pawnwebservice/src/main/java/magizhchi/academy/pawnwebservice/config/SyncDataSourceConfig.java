package magizhchi.academy.pawnwebservice.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;

@Configuration
public class SyncDataSourceConfig {

    /**
     * Local PostgreSQL DataSource.
     * initializationFailTimeout = 0 means HikariCP will not try to validate
     * the connection on startup — safe when local DB may be unreachable
     * (e.g. when the app is deployed to Railway without a local DB nearby).
     */
    @Bean(name = "localDataSource")
    public DataSource localDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://localhost:5432/pawnbroking");
        ds.setUsername("postgres");
        ds.setPassword("happy");
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setMaximumPoolSize(3);
        ds.setConnectionTimeout(5000);          // 5 s — fail fast when internet is off
        ds.setInitializationFailTimeout(0);     // do NOT fail app startup if local DB is down
        ds.setPoolName("LocalPool");
        return ds;
    }

    @Bean(name = "localJdbcTemplate")
    public JdbcTemplate localJdbcTemplate(@Qualifier("localDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
