mvn -N -P cyk.repo.repsy.io deploy:deploy-file -Dpackaging=jar -Dfile="target/jee-utility-server-representation-impl-0.1.0.jar" ^
-DrepositoryId=cyk.repo.repsy.io -Durl=https://repo.repsy.io/mvn/kycdev/default ^
-DgroupId=org.cyk.jee.utility.server.representation.impl ^
-DartifactId=jee-utility-server-representation-impl ^
-Dversion=0.1.0