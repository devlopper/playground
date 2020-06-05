mvn -N -P cyk.repo.repsy.io deploy:deploy-file -Dpackaging=pom -Dfile="pom.xml" ^
-DrepositoryId=cyk.repo.repsy.io -Durl=https://repo.repsy.io/mvn/kycdev/default ^
-DgroupId=org.cyk.pom.jee.utility.server.persistence ^
-DartifactId=pom-jee-utility-server-persistence ^
-Dversion=pom.jee.utility.server.persistence.0.1.0