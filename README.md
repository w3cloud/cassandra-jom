cassandra-jom
=============
<ul>
<li>An easy to use java object mapper, built around DataStax's Java CQL driver.
<li>It can automatically create tables/indexes and add columns from your datamodel.
<li>jom can autogenerate UUID
<li>Support for composite id
<li>Automatically converts Java camelcasing namings to cql style naming. For example, if you have an entity CarModel, the corresponding table name will be car_model.
<li>Embed objects within your entity. For example, you can embed Address class inside your User entity.
<li>Collections can be embeded using CqlStoreAsJson. Example. Order can have a list of orderItems, an item can have a list of options. The orderItems field can be annotated with CqlStoreAsJson and the jom will store the list as json in a text column.
<li>JPA style annotations. CqlEntity, CqlId, CqlAutoGen, CqlEmbed, CqlStoreAsJson
<li>Cassandra has very limited quering capabilities. Jom gets round the limilitation by implementing filter feature. 
<li>In cassandra, it is efficient to update just the modified fileds. Coloumn level updates can be done, With updateColoumn method.
</ul>

<h5>Maven Dependency</h5>

    <dependency>
      <groupId>org.w3cloud.api</groupId>
      <artifactId>cassandra-jom</artifactId>
      <version>1.2.1</version>
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
	@CqlId
	@CqlAutoGen
	private UUID id;
	private String make;
  private Strign modelName;
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
			props.put("cql.contactpoint", "localhost");
			props.put("cql.keyspace","jom_test");
			props.put("cql.synctableschema", "true");
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
			List<CarModel> carModels=em.findAll(carModel.class,"where model_year=?" , 2015);

</pre>
<p>For more complex usage, look into my testcases</p>
<h5>Upcoming release</h5>
<ul>
<li> Version 1.1.1 scheduled for early November, 2014
<li> Add delete functionality to the entitymanager
<li> Expose datastax session for any advanced queries.
<li> Improve on code doucumenation and javdocs
<li> Any fixes for bugs that we encounter.
</ul>
