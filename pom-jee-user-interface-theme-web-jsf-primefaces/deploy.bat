mvn -N -P cyk.repo.repsy.io deploy:deploy-file -Dpackaging=pom -Dfile="pom.xml" ^
-DrepositoryId=cyk.repo.repsy.io -Durl=https://repo.repsy.io/mvn/kycdev/default ^
-DgroupId=org.cyk.pom.jee.user.interface.theme.web.jsf.primefaces ^
-DartifactId=pom-jee-user-interface-theme-web-jsf-primefaces ^
-Dversion=pom.jee.user.interface.theme.web.jsf.primefaces.0.1.0