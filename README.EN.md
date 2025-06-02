# XAdES Signer CLI

Español clic en la bandera [:es:](README.md)
### FirmadorXadesCli.jar

Command-line tool for signing XML documents generated from files or SQL queries, using P12 certificates.
Supports **enveloped** and **detached** signatures.
The signer creates files compliant with the XAdES standard.
It can embed the signature or create a separate detached signature file.
You can configure a PostgreSQL database connection to run queries; their output will be saved as XML and signed.

---

## Requirements

- Java 8+
- Properties file for database access configuration `db.properties` (required for SQL mode)
- General configuration properties file `config.properties`
- Certificate in `.p12` format

---

## General Usage

BaseDir/  
├── FirmadorXadesCli.jar  
├── config.properties  
└── db.properties  

```bash
java -jar FirmadorXadesCli.jar [options]
```


---

## Available Options

| Long Option | Short Option | Description |
| :-- | :-- | :-- |
| `--sql` | `-s` | Specifies an SQL query (either as a string or path to a `.sql` file). |
| `--archivo` | `-a` | XML file to sign. |
| `--cert` | `-c` | Path to the `.p12` certificate. |
| `--salida` | `-o` | Output file name (optional). If not specified, the input file's base name is used. |
| `--detached` | `-d` | Detached (`true`) or enveloped (`false`) signature. Default: `false`. |

> [!WARNING]
> The certificate password is requested interactively from the console.

---

## Usage Modes

### 1. Sign the result of an SQL query

```bash
java -jar FirmadorXadesCli.jar --sql query.sql --cert cert.p12 [--salida output.xml] [--detached true|false]
```

Or using short aliases:

```bash
java -jar FirmadorXadesCli.jar -s query.sql -c cert.p12 -o output.xml -d true
```

If `query.sql` exists, its contents will be executed as an SQL query.
If not, it will try to execute the argument directly as an inline SQL string.

> [!TIP]
> Instead of a .sql file, you can use a string like _'select * from table where id = some_value;'_
>
> The behavior will be the same as using a .sql file.

---

### 2. Sign an existing XML file

```bash
java -jar FirmadorXadesCli.jar --archivo input.xml --cert cert.p12 [--salida output.xml] [--detached true|false]
```

Or using short aliases:

```bash
java -jar FirmadorXadesCli.jar -a input.xml -c cert.p12 -o output.xml -d true
```


---

## Output Behavior

- If **`--salida` is not specified**, the input file name with `.xml` extension is used.
- If **`--detached true`**, a second file is generated with a suffix before the `.xml` extension.
> [!NOTE]
> The suffix is configured in the `config.properties` file.


__Example:__
If in `config.properties` the variable signed.suffix=_fd


```bash
--salida output.xml --detached true
→ output.xml (XML)
→ output_fd.xml (detached signature)
```


---

## 🕛 Timestamp

A timestamp will be added using a free-access TSA service.
This service is configured in the `config.properties` file with the variable tsa.url=
A list of free TSAs can be found in [this list](TSA.MD)

---

## ❓ Help

```bash
# To view help from the console
java -jar FirmadorXadesCli.jar
```

This will display usage instructions and examples.

> [!IMPORTANT]
> You must have a p12 certificate.
> If you set the trust path in the config.properties file, the signature will be validated after signing.

---

For usage questions or modifications, you can write to ✉️ gaticaz@yahoo.com
