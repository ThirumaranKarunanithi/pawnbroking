package magizhchi.academy.pawnwebservice.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class CloudDataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.hikari.connection-init-sql:SET TIME ZONE 'UTC'}")
    private String connectionInitSql;

    /**
     * Railway cloud DataSource — marked @Primary so JPA, the auto-configured
     * JdbcTemplate, and all unqualified DataSource injections use this one.
     */
    @Bean(name = "cloudDataSource")
    @Primary
    public DataSource cloudDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setConnectionInitSql(connectionInitSql);
        ds.setMaximumPoolSize(10);
        ds.setPoolName("CloudPool");
        return ds;
    }

    @Bean(name = "cloudJdbcTemplate")
    @Primary
    public JdbcTemplate cloudJdbcTemplate(@Qualifier("cloudDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
