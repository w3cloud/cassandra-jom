package org.w3cloud.jom.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CqlId {
	enum IdType{
		PARTITION_KEY,
		CLUSTER_KEY
	};
	IdType idType();
}
