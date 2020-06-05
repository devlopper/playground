call mvn -N -P cyk.repo.repsy.io deploy:deploy-file -Dpackaging=pom -Dfile="pom.xml" ^
-DrepositoryId=cyk.repo.repsy.io -Durl=https://repo.repsy.io/mvn/kycdev/default ^
-DgroupId=org.cyk.pom.base ^
-DartifactId=pom-base ^
-Dversion=pom.base.0.1.0

call mvn -N -P cyk.repo.repsy.io deploy:deploy-file -Dpackaging=pom -Dfile="pom.xml" ^
-DrepositoryId=cyk.repo.repsy.io -Durl=https://repo.repsy.io/mvn/kycdev/default ^
-DgroupId=org.cyk.pom.jee ^
-DartifactId=pom-jee ^
-Dversion=pom.jee.0.1.0

call mvn -N -P cyk.repo.repsy.io deploy:deploy-file -Dpackaging=pom -Dfile="pom.xml" ^
-DrepositoryId=cyk.repo.repsy.io -Durl=https://repo.repsy.io/mvn/kycdev/default ^
-DgroupId=org.cyk.pom.jee.server ^
-DartifactId=pom-jee-server ^
-Dversion=pom.jee.server.0.1.0

call mvn -N -P cyk.repo.repsy.io deploy:deploy-file -Dpackaging=pom -Dfile="pom.xml" ^
-DrepositoryId=cyk.repo.repsy.io -Durl=https://repo.repsy.io/mvn/kycdev/default ^
-DgroupId=org.cyk.pom.jee.client ^
-DartifactId=pom-jee-client ^
-Dversion=pom.jee.client.0.1.0

call mvn -N -P cyk.repo.repsy.io deploy:deploy-file -Dpackaging=pom -Dfile="pom.xml" ^
-DrepositoryId=cyk.repo.repsy.io -Durl=https://repo.repsy.io/mvn/kycdev/default ^
-DgroupId=org.cyk.pom.jee.utility.__kernel__ ^
-DartifactId=pom-jee-utility-__kernel__ ^
-Dversion=pom.jee.utility.__kernel__.0.1.0
