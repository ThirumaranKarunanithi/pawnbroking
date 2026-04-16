package magizhchi.academy.pawndbsync.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class LocalDataSourceConfig {

    @Value("${local.datasource.url}")
    private String url;

    @Value("${local.datasource.username}")
    private String username;

    @Value("${local.datasource.password}")
    private String password;

    @Bean(name = "localDataSource")
    public DataSource localDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setMaximumPoolSize(3);
        ds.setConnectionTimeout(5000);
        ds.setInitializationFailTimeout(0); // do NOT fail startup if local DB is down
        ds.setPoolName("LocalPool");
        return ds;
    }

    @Bean(name = "localJdbcTemplate")
    public JdbcTemplate localJdbcTemplate(@org.springframework.beans.factory.annotation.Qualifier("localDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
