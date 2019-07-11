package org.cyk.utility.server.persistence;

@javax.annotation.sql.DataSourceDefinition(
		name="java:global/utility/persistence/testDataSource",
		className="org.h2.jdbcx.JdbcDataSource",
		url="jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
		user="sa",
		password="sa"
)
/*
@javax.annotation.sql.DataSourceDefinition(
		name="java:global/utility/persistence/testDataSource"
		,className="org.cyk.utility.server.persistence.DataSource"
		,url="jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
		,user="sa"
		,password="sa"
)
*/
public class DataSourceDefinition {

}
