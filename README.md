cassandra-jom
=============
<ul>
<li>An easy to use java object mapper, built around DataStax's Java CQL driver.
<li>It can automatically create tables and add columns from your cql annotated domain objects.
<li>jom can auto-generate UUID
<li>Composite keys are well supported. Keys can be designated as either partition key or cluster keys, using the CqlId annotation.
<li>Automatically converts Java camel-casing namings to cql style naming. For example, if you have an entity CarModel, the corresponding table name will be car_model.
<li>Embed objects within your entity. For example, you can embed Address object inside your User entity.
<li>JPA style annotations. CqlEntity, CqlId, CqlAutoGen, CqlEmbed, CqlStoreAsJson
<li>Cassandra has very limited quering capabilities. Jom gets round the limilitation by implementing filter feature. 
<li>In cassandra, it is efficient to update just the modified fileds. Coloumn level updates can be done, using updateColoumn method.
<li>Cassandra light weight transaction support. Inserts, updates and deletes can be done in batches.
<li>Counter column support with @CqlColumn(dataType=DataType.COUNTER) annotation on any nemeric fields
</ul>

<h5>Maven Dependency</h5>

    <dependency>
      <groupId>org.w3cloud.api</groupId>
      <artifactId>cassandra-jom</artifactId>
      <version>1.2.9</version>
    </dependency>
    
    If cassandra-jom is not resolved, add sonatype repo to your pom.xml
    
      <repositories>
    <repository>
      <id>sonatype-repo</id>
      <name>Sonatype</name>
      <url>https://oss.sonatype.org/content/repositories/releases</url>
    </repository>
  </repositories>
    
<h5>Your entity object<h5>
<pre>
package org.w3cloud.jom.testmodels;

import java.util.UUID;
import org.w3cloud.jom.annotations.CqlAutoGen;
import org.w3cloud.jom.annotations.CqlEntity;
import org.w3cloud.jom.annotations.CqlId;

@CqlEntity
public class CarModel {
	@CqlId(idType=IdType.PARTITION_KEY) //another idType is CLUSTER_KEY
	@CqlAutoGen
	private UUID id;
	private String make;
  	private String modelName;
  @CqlIndex
  private int modelYear;
	public UUID getId(){
          return id;
        }
	public void setId(UUID id){
          this.id=id;
        }
        ..... Other getters and setters
}

</pre>
Insert, update and find your entity

<pre>
			Properties props=new Properties();
			//You can also pass multiple contact points and port numbers.
			// Example: props.put("cql.contactpoints", "localhost:9042,anotherhost:9042");
			props.put("cql.contactpoints", "localhost");
			props.put("cql.keyspace","jom_test");
			props.put("cql.synctableschema", "true");
			//if you have more than one package to scan,
			//Pass a comma separated list
			props.put("cql.packagestoscan", "org.w3cloud.jom.testmodels");
			//Store EntityManager as a static variable at the applicaiton level.
			CqlEntityManager em=CqlEntityManagerFactory.createEntityManger(props);
			CarModel carModel=new CarModel();
			carModel.setMake("Mazda");
			carModel.setModelYear(2015);
			carModel.setModelName("3");
			em.insert(carModel);
			carModel.setModelName("Cx5");
			em.update(carModel);
			List<CarModel>carModels=em.findAll(CqlBuilder.select(CarModel.class).
				field("modelYear").eq(2015);
			//Update only one coloumn; Use this approach only if 
			//you are sure about the modified coloumns. 
			//In cassandra this approach is more effiecient
			carModel.setModelName("6");
			em.updateColumn(carModel, CqlBuilder.update(CarModel.class)
				.field("modelName").set(carModel.getModelName()));
			

</pre>
<p>For more complex usage, look into testcases</p>
<h5>Upcoming release</h5>
<ul>
<li> With 1.2.9, freezing the interface. If you use a prior versions, please upgrade to 1.2.9 or later.
<li> No planned release. 
</ul>
<h5>Caution</h5>
<ul>
<li> CqlStoreAsJson annotation does not work well with Lists. Do  your own serilization and de-seriazation, if needed with gson. Serilization is straight-forward. For de-serialization, use the following technique: <pre>order.orderItems=gson.fromJson(order.orderItemsJson,new TypeToken&lt;List&lt;OrderItem&gt;&gt;(){}.getType());</pre>
<li> Do not use CqlIndex. Cassandra encourages to use link table for queries instead of indexing a column.
<li> Avoid using the keyspace parameter of CqlEntity. Instead put entities belonging to different keyspaces in  different root packages and have an entity manager for each keyspace.
<li>jom does not support casssandra datatypes like map, list and set.
</ul>
<h5>Contribute</h5>
<ul>
<li>Please post your comments on our cassandra-jom blog page: http://www.w3cloud.org/cassandra-jom
<li> Please report bugs, by creating an issue.

</ul>
