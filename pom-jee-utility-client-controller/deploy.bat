mvn -N -P cyk.repo.repsy.io deploy:deploy-file -Dpackaging=pom -Dfile="pom.xml" ^
-DrepositoryId=cyk.repo.repsy.io -Durl=https://repo.repsy.io/mvn/kycdev/default ^
-DgroupId=org.cyk.pom.jee.utility.client.controller ^
-DartifactId=pom-jee-utility-client-controller ^
-Dversion=pom.jee.utility.client.controller.0.1.0