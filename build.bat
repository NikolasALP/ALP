REM build and deploy project to local directory
REM change your locations
mvn -f c:\ALP\workspace\pom.xml -DaltDeploymentRepository=releases-repo::default::file:c:\alp-repo\repo\releases clean deploy