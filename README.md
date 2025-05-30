# Firmador XAdES CLI
### FirmadorXadesCli.jar

Herramienta en línea de comandos para firmar documentos XML generados desde archivos o consultas SQL, usando certificados P12.  
Soporta firma **enveloped** o **detached**.  
El firmador crea archivos firmados con estandar XAdES.  
Empaqueta la firma o crea archivo de firma separado (detached).
Se puede configurar una conexión a una base de datos PostgreSQL para realizar consultas, la salida de las mismas se grabará como xml y se firmará.

---

## Requisitos

- Java 8+
- Archivo de propiedades para configurar acceso a una BD `db.properties` si usas modo SQL
- Archivo de propiedades de configuración general `config.properties`
- Certificado en formato `.p12`

---

## Uso general

BaseDir/  
├── FirmadorXadesCli.jar  
├── config.properties  
└── db.properties  


```bash
java -jar FirmadorXadesCli.jar [opciones]
```

---

## Opciones disponibles

| Opción larga       | Opción corta | Descripción |
|--------------------|--------------|-------------|
| `--sql`	     | `-s`         | Especifica una consulta SQL (ya sea como cadena o ruta a archivo `.sql`). |
| `--archivo`        | `-a`         | Archivo XML a firmar. |
| `--cert`           | `-c`         | Ruta al certificado `.p12`. |
| `--salida`         | `-o`         | Nombre del archivo de salida (opcional). Si no se especifica, se usa el nombre base del archivo de entrada. |
| `--detached`       | `-d`         | Firma en formato detached (`true`) u enveloped (`false`). Por defecto: `false`. |

> [!WARNING]
> La clave del certificado se solicita interactivamente por consola.

---

## Modos de uso

### 1. Firmar resultado de una consulta SQL

```bash
java -jar FirmadorXadesCli.jar --sql archivo.sql --cert cert.p12 [--salida salida.xml] [--detached true|false]
```

O usando alias cortos:
```bash
java -jar FirmadorXadesCli.jar -s archivo.sql -c cert.p12 -o salida.xml -d true
```

Si `archivo.sql` existe, se ejecutará su contenido como consulta SQL. 
Si no, se intentará ejecutar directamente como string SQL inline.

> [!TIP]
> En lugar de un archivo .sql se puede usar un string _'select * from tabla where id = un_valor;'_
> 
> El comportamiento será igual al del uso de un archivo .sql

---

### 2. Firmar archivo XML existente

```bash
java -jar FirmadorXadesCli.jar --archivo entrada.xml --cert cert.p12 [--salida salida.xml] [--detached true|false]
```

O usando alias cortos:
```bash
java -jar FirmadorXadesCli.jar -a entrada.xml -c cert.p12 -o salida.xml -d true
```

---

## Comportamiento de salida

- Si **no se especifica `--salida`**, se usa el nombre del archivo de entrada con extensión `.xml`.
- Si **`--detached true`**, se genera un segundo archivo con un agregado antes de la extensión `.xml`.
> [!NOTE]
> El agregado se configura en el archivo `config.properties`

__Ejemplo:__  
Si en el archivo config.properties la variable signed.suffix=_fd
```bash
--salida salida.xml --detached true
→ salida.xml (XML)
→ salida_fd.xml (firma detached)
```

---

## 🕛 Tiempo

Se agregará un sello de tiempo con un servicio TSA de acceso libre.  
Este servicio se configura en el archivo config.properties usando la variable tsa.url=  
Una lista de TSAs libres se puede ver en [esta lista](TSA.MD)

---

## ❓ Ayuda

```bash
# Para ver ayuda desde la consola
java -jar FirmadorXadesCli.jar
```

Mostrará instrucciones de uso y ejemplos.

> [!IMPORTANT]
> Se debe contar con un certificado p12.  
> Si se coloca la ruta de confianza en el archivo config.properties se hará una validación de la firma luego del firmado.

---
