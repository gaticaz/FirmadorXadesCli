java -jar target/FirmadorXadesCli.jar documento.xml certificado.p12 satsold  
	firma documento.xml y lo graba como documento.xml
java -jar target/FirmadorXadesCli.jar documento.xml certificado.p12 satsold --detached true
	firma documento.xml, queda sin cambios y crea documento_fd.xml con la firma desacoplada
java -jar target/FirmadorXadesCli.jar documento.xml certificado.p12 satsold aaa.xml --detached false 
	firma documento.xml y lo graba como aaa.xml
java -jar target/FirmadorXadesCli.jar documento.xml certificado.p12 satsold aaa.xml --detached true
	firma documento.xml, queda sin cambios y crea aaa.xml con la firma desacoplada

java -jar target/FirmadorXadesCli.jar --sql consulta.sql  certificado.p12 satsold
	ejecuta la consulta del archivo consulta.sql, la graba en un archivo consulta.xml y lo firma
java -jar target/FirmadorXadesCli.jar --sql consulta.sql  certificado.p12 satsold --detached true
	ejecuta la consulta del archivo consulta.sql, la graba en un archivo consulta.xml la firma se graba en consulta_fd.xml
java -jar target/FirmadorXadesCli.jar --sql consulta.sql  certificado.p12 satsold aaa.xml --detached false
	ejecuta la consulta del archivo consulta.sql, la graba en un archivo aaa.xml y lo firma
java -jar target/FirmadorXadesCli.jar --sql consulta.sql  certificado.p12 satsold aaa.xml --detached true
	ejecuta la consulta del archivo consulta.sql, la graba en un archivo aaa.xml la firma se graba en aaa_fd.xml

En lugar de un archivo .sql se puede usar un string 'select * from tabla where id = un_valor;'
El comportamiento será igual al del uso de un archivo .sql
