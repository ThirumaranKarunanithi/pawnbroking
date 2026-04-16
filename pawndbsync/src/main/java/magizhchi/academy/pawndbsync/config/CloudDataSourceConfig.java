package magizhchi.academy.pawndbsync.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class CloudDataSourceConfig {

    @Value("${cloud.datasource.url}")
    private String url;

    @Value("${cloud.datasource.username}")
    private String username;

    @Value("${cloud.datasource.password}")
    private String password;

    @Bean(name = "cloudDataSource")
    @Primary
    public DataSource cloudDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setConnectionInitSql("SET TIME ZONE 'UTC'");
        ds.setMaximumPoolSize(5);
        ds.setPoolName("CloudPool");
        return ds;
    }

    @Bean(name = "cloudJdbcTemplate")
    @Primary
    public JdbcTemplate cloudJdbcTemplate(@org.springframework.beans.factory.annotation.Qualifier("cloudDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
