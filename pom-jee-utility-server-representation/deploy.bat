mvn -N -P cyk.repo.repsy.io deploy:deploy-file -Dpackaging=pom -Dfile="pom.xml" ^
-DrepositoryId=cyk.repo.repsy.io -Durl=https://repo.repsy.io/mvn/kycdev/default ^
-DgroupId=org.cyk.pom.jee.utility.server.representation ^
-DartifactId=pom-jee-utility-server-representation ^
-Dversion=pom.jee.utility.server.representation.0.1.0