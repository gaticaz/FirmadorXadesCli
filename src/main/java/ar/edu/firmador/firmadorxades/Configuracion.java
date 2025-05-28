/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ar.edu.firmador.firmadorxades;

/**
 *
 * @author gaticaz
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuracion {

    private static final Properties props = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar config.properties", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static String getTruststorePath() {
        return get("truststore.path");
    }

    public static String getTruststorePassword() {
        return get("truststore.password");
    }

    public static String getTruststoreType() {
        return get("truststore.type");
    }
    
    public static String getTsaUrl() {
        return get("tsa.url");
    }
    
    public static String getSignedSuffix() {
        return get("signed.suffix");
    }
}

