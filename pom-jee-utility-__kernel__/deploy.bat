mvn -N -P cyk.repo.repsy.io deploy:deploy-file -Dpackaging=pom -Dfile="pom.xml" ^
-DrepositoryId=cyk.repo.repsy.io -Durl=https://repo.repsy.io/mvn/kycdev/default ^
-DgroupId=org.cyk.pom.jee.utility.__kernel__ ^
-DartifactId=pom-jee-utility-__kernel__ ^
-Dversion=pom.jee.utility.__kernel__.0.1.0