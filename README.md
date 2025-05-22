# FirmadorXadesCli

El firmador crea archivos firmados con estandar xades. Empauqeta la firma o crea archivo de firma separado (detached).
Se puede configurar una conexion a una base de datos postgreSQL para realizar consultas, la salida de las mismas se grabará como xml y se firmará.

> [!IMPORTANT]
> Se debe contar con un certificado p12
> Si se coloca la ruta de confianza se hará una validación de la firma luego del firmado.

**java -jar target/FirmadorXadesCli.jar [--sql consulta.sql|string psqlquery] [documento.xml] certificado.p12 pass [archivo_de_salida.xml] [--detached true|false]**

## Se debe usar un archivo xml para firmar o --sql para generar un archivo xml a partir de una consulta psql.

Firma documento.xml y lo graba como documento.xml 
> java -jar target/FirmadorXadesCli.jar documento.xml certificado.p12 pass

Firma documento.xml, queda sin cambios y crea documento_fd.xml con la firma desacoplada 
> java -jar target/FirmadorXadesCli.jar documento.xml certificado.p12 pass --detached true

Firma documento.xml y lo graba como aaa.xml 
> java -jar target/FirmadorXadesCli.jar documento.xml certificado.p12 pass aaa.xml --detached false 

Firma documento.xml, queda sin cambios y crea aaa.xml con la firma desacoplada
> java -jar target/FirmadorXadesCli.jar documento.xml certificado.p12 pass aaa.xml --detached true

Ejecuta la consulta del archivo consulta.sql, la graba en un archivo consulta.xml y lo firma 
> java -jar target/FirmadorXadesCli.jar --sql consulta.sql certificado.p12 pass 

Ejecuta la consulta del archivo consulta.sql, la graba en un archivo consulta.xml la firma se graba en consulta_fd.xml 
> java -jar target/FirmadorXadesCli.jar --sql consulta.sql certificado.p12 pass --detached true 

Ejecuta la consulta del archivo consulta.sql, la graba en un archivo aaa.xml y lo firma 
> java -jar target/FirmadorXadesCli.jar --sql consulta.sql certificado.p12 pass aaa.xml --detached false 

Ejecuta la consulta del archivo consulta.sql, la graba en un archivo aaa.xml la firma se graba en aaa_fd.xml
> java -jar target/FirmadorXadesCli.jar --sql consulta.sql certificado.p12 pass aaa.xml --detached true



> [!TIP]
> En lugar de un archivo .sql se puede usar un string 'select * from tabla where id = un_valor;'
> 
> El comportamiento será igual al del uso de un archivo .sql
