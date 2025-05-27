/**
 * CLI para firmar archivos XML usando XAdES.
 * Uso libre.
 *
 * @author gaticaz
 */

package ar.edu.firmador.firmadorxades;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.signature.XAdESService;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.model.SignatureValue;

import eu.europa.esig.dss.service.tsp.OnlineTSPSource;

import eu.europa.esig.dss.spi.validation.CertificateVerifier;
import eu.europa.esig.dss.model.x509.CertificateToken;

import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.simplereport.SimpleReport;

import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.validation.reports.Reports;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import java.io.Console;

import java.sql.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FirmadorXadesCLI {
    
    //  java -jar target/FirmadorXadesCli.jar --sql "SELECT * FROM personas" certificado.p12 clave123 salida_firmada.xml
//    public static void main(String[] args) {
//        try {
//            if (args.length >= 4 && "--sql".equals(args[0])) {
//                // Modo SQL
//                String sqlSource = args[1];
//                String sqlQuery;
//                File sqlFile = new File(sqlSource);
//                sqlQuery = (sqlFile.exists() && sqlFile.isFile())
//                    ? new String(Files.readAllBytes(sqlFile.toPath()), StandardCharsets.UTF_8)
//                    : sqlSource;
//
//                String p12Path = args[2];
//                String p12Password = args[3];
//                String baseOutputName = "consulta.xml";
//                boolean detached = false;
//
//                for (int i = 4; i < args.length; i++) {
//                    if (args[i].equalsIgnoreCase("--detached") && i + 1 < args.length) {
//                        detached = Boolean.parseBoolean(args[++i]);
//                    } else {
//                        baseOutputName = args[i];
//                    }
//                }
//
//                // Firmar la consulta
//                Properties props = new Properties();
//                try (FileInputStream fis = new FileInputStream("db.properties")) {
//                    props.load(fis);
//                }
//
//                String jdbcUrl = props.getProperty("jdbcUrl");
//                String dbUser = props.getProperty("dbUser");
//                String dbPassword = props.getProperty("dbPassword");
//
//                String xmlOutput = baseOutputName;
//                String signatureOutput = detached
//                    ? insertSuffixBeforeExtension(baseOutputName, "_fd")
//                    : baseOutputName;
//
//                System.out.println("Ejecutando consulta y firmando resultados...");
//                firmarQuery(jdbcUrl, dbUser, dbPassword, sqlQuery, p12Path, p12Password, xmlOutput, signatureOutput, detached);
//                System.out.println("¡Firma completada! Archivo firmado en: " + new File(signatureOutput).getAbsolutePath());
//
//                System.out.println("\nValidando firma...");
//                validarFirma(signatureOutput);
//
//            } else if (args.length >= 3) {
//                // Modo archivo
//                String xmlPath = args[0];
//                String p12Path = args[1];
//                String p12Password = args[2];
//                boolean detached = false;
//                String outputPath = null;
//
//                for (int i = 3; i < args.length; i++) {
//                    if (args[i].equalsIgnoreCase("--detached") && i + 1 < args.length) {
//                        detached = Boolean.parseBoolean(args[i + 1]);
//                        i++;
//                    } else {
//                        outputPath = args[i];
//                    }
//                }
//
//                String signatureOutput;
//                
//                // Definir salida según si se especificó o no
//                if (outputPath == null) {
//                    signatureOutput = detached
//                        ? insertSuffixBeforeExtension(xmlPath, "_fd")
//                        : xmlPath;
//                } else {
//                    signatureOutput = outputPath;
//                }
//
//                System.out.println("Firmando " + xmlPath + "...");
//                firmarXml(xmlPath, p12Path, p12Password, signatureOutput, detached);
//                System.out.println("¡Firma completada! Archivo firmado en: " + new File(signatureOutput).getAbsolutePath());
//
//                System.out.println("\nValidando firma...");
//                validarFirma(signatureOutput);
//            } else {
//                System.err.println("Uso:");
//                System.err.println("  Firma XML: java -jar firmador-xades.jar <archivo.xml> <certificado.p12> <clave> [salida.xml] [--detached true|false]");
//                System.err.println("  Firma SQL: java -jar firmador-xades.jar --sql \"SELECT * FROM tabla\" <certificado.p12> <clave> [salida.xml] [--detached true|false]");
//                System.exit(1);
//            }
//        } catch (Exception e) {
//            System.err.println("Error:");
//            e.printStackTrace();
//            System.exit(2);
//        }
//    }
    
    public static void main(String[] args) {
        try {
            Map<String, String> params = new HashMap<>();
            List<String> positionalArgs = new ArrayList<>();

            // Procesamos los argumentos
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];

                if (arg.startsWith("-")) {
                    String key;
                    String value = null;

                    // Detectar si es -s o --sql
                    String normalizedArg = arg.toLowerCase();

                    if (normalizedArg.equals("--sql") || normalizedArg.equals("-s")) {
                        key = "sql";
                    } else if (normalizedArg.equals("--archivo") || normalizedArg.equals("-a")) {
                        key = "archivo";
                    } else if (normalizedArg.equals("--cert") || normalizedArg.equals("-c")) {
                        key = "cert";
                    } else if (normalizedArg.equals("--salida") || normalizedArg.equals("-o")) {
                        key = "salida";
                    } else if (normalizedArg.equals("--detached") || normalizedArg.equals("-d")) {
                        key = "detached";
                    } else {
                        System.err.println("Parámetro desconocido: " + arg);
                        mostrarUso();
                        return;
                    }

                    // Si hay valor siguiente (y no empieza con "-"), lo asignamos
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        value = args[++i];
                    }

                    // Casos especiales: detached sin valor
                    if ("detached".equals(key)) {
                        params.put(key, value != null ? value : "true");
                    } else {
                        if (value == null) {
                            System.err.println("El parámetro '" + arg + "' requiere un valor.");
                            mostrarUso();
                            return;
                        }
                        params.put(key, value);
                    }

                } else {
                    positionalArgs.add(arg);
                }
            }

            // Validar modos de ejecución
            boolean esSql = params.containsKey("sql");
            boolean esArchivo = params.containsKey("archivo");

            if (!esSql && !esArchivo) {
                System.err.println("Se debe indicar --sql o --archivo.");
                mostrarUso();
            }

            if (esSql && esArchivo) {
                System.err.println("Solo puede usarse uno de los dos: --sql o --archivo.");
                mostrarUso();
            }

            String p12Path = params.get("cert");
            if (p12Path == null || p12Path.isBlank()) {
                System.err.println("Se debe indicar el certificado con --cert");
                mostrarUso();
            }

            // Pedimos la clave del certificado
            Console console = System.console();
            if (console == null) {
                System.err.println("No se puede leer desde consola.");
                System.exit(1);
            }
            char[] passwordChars = console.readPassword("Clave del certificado: ");
            String p12Password = new String(passwordChars);

            boolean detached = Boolean.parseBoolean(params.getOrDefault("detached", "false"));

            if (esSql) {
                // MODO SQL
                String sqlSource = params.get("sql");
                File sqlFile = new File(sqlSource);
                String sqlQuery = sqlSource;

                if (sqlFile.exists() && sqlFile.isFile()) {
                    sqlQuery = new String(Files.readAllBytes(sqlFile.toPath()), StandardCharsets.UTF_8);
                }

                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream("db.properties")) {
                    props.load(fis);
                }

                String jdbcUrl = props.getProperty("jdbcUrl");
                String dbUser = props.getProperty("dbUser");
                String dbPassword = props.getProperty("dbPassword");

                String baseOutputName = params.get("salida");
                if (baseOutputName == null) {
                    baseOutputName = esSql && sqlFile.exists()
                            ? sqlFile.getName().replaceFirst("[.][^.]+$", ".xml")
                            : "salida.xml";
                }

                String xmlOutput = baseOutputName;
                String signatureOutput = detached
                        ? insertSuffixBeforeExtension(baseOutputName, "_fd")
                        : baseOutputName;

                System.out.println("Ejecutando consulta y firmando resultados...");
                firmarQuery(jdbcUrl, dbUser, dbPassword, sqlQuery, p12Path, p12Password, xmlOutput, signatureOutput, detached);
                System.out.println("¡Firma completada! Archivo firmado en: " + new File(signatureOutput).getAbsolutePath());

                System.out.println("\nValidando firma...");
                validarFirma(signatureOutput);

            } else {
                // MODO ARCHIVO XML
                String xmlPath = params.get("archivo");
                File xmlFile = new File(xmlPath);
                if (!xmlFile.exists() || !xmlFile.isFile()) {
                    System.err.println("El archivo XML no existe: " + xmlPath);
                    System.exit(1);
                }

                String outputPath = params.get("salida");
                String signatureOutput;

                if (outputPath == null) {
                    signatureOutput = detached
                            ? insertSuffixBeforeExtension(xmlPath, "_fd")
                            : xmlPath;
                } else {
                    signatureOutput = outputPath;
                }

                System.out.println("Firmando " + xmlPath + "...");
                firmarXml(xmlPath, p12Path, p12Password, signatureOutput, detached);
                System.out.println("¡Firma completada! Archivo firmado en: " + new File(signatureOutput).getAbsolutePath());

                System.out.println("\nValidando firma...");
                validarFirma(signatureOutput);
            }

        } catch (Exception e) {
            System.err.println("Error:");
            e.printStackTrace();
            System.exit(2);
        }
    }

    // Método principal: permite elegir entre firma detached o enveloped
    public static void firmarXml(String xmlPath, String p12Path, String p12Password, String outputPath, boolean detached) throws Exception {
        // 1. Cargar documento a firmar
        FileDocument documentToSign = new FileDocument(xmlPath);

        // 2. Token y clave privada
        SignatureTokenConnection token = new Pkcs12SignatureToken(
            new File(p12Path),
            new KeyStore.PasswordProtection(p12Password.toCharArray())
        );
        DSSPrivateKeyEntry privateKeyEntry = token.getKeys().get(0);

        // 3. Parámetros de firma
        XAdESSignatureParameters params = new XAdESSignatureParameters();
        params.setSignatureLevel(SignatureLevel.XAdES_BASELINE_T);
        params.setDigestAlgorithm(DigestAlgorithm.SHA256);
        params.setSigningCertificate(privateKeyEntry.getCertificate());
        params.setCertificateChain(privateKeyEntry.getCertificateChain());

        if (detached) {
            params.setSignaturePackaging(SignaturePackaging.DETACHED);
            params.setDetachedContents(List.of(documentToSign));
        } else {
            params.setSignaturePackaging(SignaturePackaging.ENVELOPED);
        }

        // 4. Servicio de firma
        XAdESService service = new XAdESService(new CommonCertificateVerifier());
        service.setTspSource(new OnlineTSPSource(Configuracion.getTsaUrl()));

        // 5. Firmar
        ToBeSigned dataToSign = service.getDataToSign(documentToSign, params);
        SignatureValue signatureValue = token.sign(dataToSign, params.getDigestAlgorithm(), privateKeyEntry);

        DSSDocument signedDocument = service.signDocument(documentToSign, params, signatureValue);

        // 6. Guardar
        signedDocument.save(outputPath);
    }

    // Método sobrecargado: mantiene compatibilidad con llamadas anteriores (firma enveloped por defecto)
    public static void firmarXml(String xmlPath, String p12Path, String p12Password, String outputPath) throws Exception {
        firmarXml(xmlPath, p12Path, p12Password, outputPath, false);
    }
    
    private static CommonCertificateVerifier createCertificateVerifier() throws Exception {
        CommonCertificateVerifier verifier = new CommonCertificateVerifier();

        // Configurar la fuente de certificados de confianza para DSS 6.2
        CommonTrustedCertificateSource trustedCertificateSource = new CommonTrustedCertificateSource();

        KeyStoreCertificateSource keyStoreCertificateSource = new KeyStoreCertificateSource(
            new File(Configuracion.getTruststorePath()),
            Configuracion.getTruststoreType(),
            Configuracion.getTruststorePassword().toCharArray()
        );

        // Forma correcta de importar certificados en DSS 6.2:
        for (CertificateToken certToken : keyStoreCertificateSource.getCertificates()) {
            trustedCertificateSource.addCertificate(certToken);
        }

        verifier.setTrustedCertSources(trustedCertificateSource);

        // Configuración recomendada para DSS 6.2
        verifier.setRevocationFallback(true);
        verifier.setCheckRevocationForUntrustedChains(false);

        return verifier;
    }    
    
    public static void validarFirma(String signedXmlPath) {
        try {
            // 1. Cargar documento firmado
            DSSDocument signedDocument = new FileDocument(signedXmlPath);

            // 2. Crear validador
            SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(signedDocument);

            // 3. Configurar verificador de certificados
            CertificateVerifier verifier = createCertificateVerifier();
            validator.setCertificateVerifier(verifier);

            if (signedXmlPath.contains("_fd.xml")) {
                String detachedXmlPath = signedXmlPath.replace("_fd.xml", ".xml");
                File detachedXmlFile = new File(detachedXmlPath);
                if (detachedXmlFile.exists()) {
                    System.out.println("Detectado archivo original para firma detached: " + detachedXmlFile.getAbsolutePath());
                    validator.setDetachedContents(List.of(new FileDocument(detachedXmlFile)));
                } else {
                    System.out.println("Advertencia: No se encontró el XML original para la firma detached en: " + detachedXmlPath);
                }
            }

            // 4. Realizar validación
            Reports reports = validator.validateDocument();
            SimpleReport simpleReport = reports.getSimpleReport();

            // 5. Obtener información de la primera firma
            String signatureId = simpleReport.getFirstSignatureId();

            // 6. Mostrar resultados básicos (100% compatible con DSS 6.2)
            System.out.println("\n=== RESUMEN DE VALIDACIÓN ===");
            System.out.println("Firma válida: " + simpleReport.isValid(signatureId));
            System.out.println("Nivel de firma: " + simpleReport.getSignatureFormat(signatureId));
            System.out.println("Firmado por: " + simpleReport.getSignedBy(signatureId));
            System.out.println("Fecha de firma: " + simpleReport.getSigningTime(signatureId));

            // 7. Información básica de certificado (sin usar métodos obsoletos)
            System.out.println("\n=== INFORMACIÓN DEL CERTIFICADO ===");
            System.out.println("Cadena de certificados disponible: " + 
                (simpleReport.getCertificateChain(signatureId) != null ? "SÍ" : "NO"));

        } catch (Exception e) {
            System.err.println("Error durante la validación:");
            e.printStackTrace();
        }
    }
    
    public static void firmarQuery(String jdbcUrl, String user, String password, String sql,
                               String p12Path, String p12Password, String xmlOutputPath,
                               String signatureOutputPath, boolean detached) throws Exception {

        Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("resultadoConsulta");
        doc.appendChild(root);

        while (rs.next()) {
            Element row = doc.createElement("fila");
            for (int i = 1; i <= columnCount; i++) {
                String columnName = meta.getColumnName(i);
                String value = rs.getString(i);
                Element column = doc.createElement(columnName);
                column.appendChild(doc.createTextNode(value != null ? value : ""));
                row.appendChild(column);
            }
            root.appendChild(row);
        }

        rs.close(); stmt.close(); conn.close();

        // Guardar XML original
        File xmlFile = new File(xmlOutputPath);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(xmlFile));

        // Firmar
        firmarXml(xmlFile.getAbsolutePath(), p12Path, p12Password, signatureOutputPath, detached);

        System.out.println("Archivo XML original guardado como: " + xmlFile.getAbsolutePath());
    }
    
    private static String insertSuffixBeforeExtension(String filename, String suffix) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) return filename + suffix;
        return filename.substring(0, lastDot) + suffix + filename.substring(lastDot);
    }
    
    private static void mostrarUso() {
        System.err.println("Uso:");
        System.err.println("  Firma XML:");
        System.err.println("    java -jar firmador-xades.jar <archivo.xml> --cert cert.p12 --password clave [salida.xml] [--detached true|false]");
        System.err.println("  Firma SQL:");
        System.err.println("    java -jar firmador-xades.jar --sql \"SELECT * FROM tabla\" --cert cert.p12 --password clave [salida.xml] [--detached true|false]");
        System.exit(1);
    }
}