mvn -N -P cyk.repo.repsy.io deploy:deploy-file -Dpackaging=pom -Dfile="pom.xml" ^
-DrepositoryId=cyk.repo.repsy.io -Durl=https://repo.repsy.io/mvn/kycdev/default ^
-DgroupId=org.cyk.pom.jee.utility.client.deployment ^
-DartifactId=pom-jee-utility-client-deployment ^
-Dversion=pom.jee.utility.client.deployment.0.1.0