package com.in28minuteschristian.rest.webservices.restfulwebservices.cassandra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.*;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.util.*;

@Configuration
@EnableCassandraRepositories
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Override
    protected String getKeyspaceName() {
        return "todo";
    }

    @Override
    protected String getContactPoints() {
        return "localhost";
    }

    @Override
    protected int getPort() {
        return 9042;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    public String[] getEntityBasePackages() {
        String basePackages = "com.in28minuteschristian.rest.webservices.restfulwebservices.model";
        return new String[] {basePackages};
    }

    @Bean
    public CassandraClusterFactoryBean cluster() {
        // CassandraClusterFactoryBean cluster = super.cluster();
        CassandraClusterFactoryBean cluster =
                new CassandraClusterFactoryBean();
        cluster.setJmxReportingEnabled(false);
        cluster.setContactPoints("127.0.0.1");
        cluster.setPort(9042);
        return cluster;
    }


    @Bean
    public CassandraSessionFactoryBean session() {

        CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
        session.setCluster(Objects.requireNonNull(cluster().getObject()));
        session.setKeyspaceName("todo");
        try {
            session.setConverter(converter());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        session.setSchemaAction(SchemaAction.CREATE_IF_NOT_EXISTS);

        return session;
    }

    @Bean
    public CassandraMappingContext mappingContext() throws ClassNotFoundException {
        CassandraMappingContext mappingContext= new CassandraMappingContext();
        mappingContext.setInitialEntitySet(getInitialEntitySet());
        return mappingContext;
    }


    @Bean
    public CassandraConverter converter() throws ClassNotFoundException {
        return new MappingCassandraConverter(mappingContext());
    }

    @Override
    protected List<String> getStartupScripts() {

        String script = "CREATE KEYSPACE IF NOT EXISTS todo "
                + "WITH durable_writes = true "
                + "AND replication = { 'class' : 'SimpleStrategy, 'replication_factor' : 1 };";

        return Collections.singletonList(script);
    }

    /*@Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        CreateKeyspaceSpecification keyspace = CreateKeyspaceSpecification.createKeyspace("todo")
                .withSimpleReplication(1).ifNotExists();
        return Collections.singletonList(keyspace);
    }*/
}
