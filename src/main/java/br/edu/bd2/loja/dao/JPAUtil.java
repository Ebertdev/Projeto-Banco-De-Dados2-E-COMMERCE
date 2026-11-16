package br.edu.bd2.loja.dao;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class JPAUtil {

    public static final String UNIDADE = "lojaPU";
    private static EntityManagerFactory emf;

    private JPAUtil() { }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory(UNIDADE, propriedadesDoAmbiente());
            Runtime.getRuntime().addShutdownHook(new Thread(JPAUtil::fechar));
        }
        return emf;
    }

    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static synchronized void fechar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    public static Map<String, Object> propriedadesDoAmbiente() {
        Map<String, Object> props = new HashMap<>();
        copiar(props, "DB_URL", "javax.persistence.jdbc.url");
        copiar(props, "DB_USER", "javax.persistence.jdbc.user");
        copiar(props, "DB_PASSWORD", "javax.persistence.jdbc.password");
        copiar(props, "SHOW_SQL", "hibernate.show_sql");
        return props;
    }

    private static void copiar(Map<String, Object> props, String variavel, String propriedade) {
        String valor = System.getenv(variavel);
        if (valor != null && !valor.isEmpty()) {
            props.put(propriedade, valor);
        }
    }

    public static void gerarDDL(String arquivoDestino) {
        Map<String, Object> props = propriedadesDoAmbiente();
        props.put("javax.persistence.schema-generation.scripts.action", "create");
        props.put("javax.persistence.schema-generation.scripts.create-target", arquivoDestino);
        props.put("javax.persistence.schema-generation.database.action", "none");
        props.put("hibernate.hbm2ddl.auto", "none");
        Persistence.generateSchema(UNIDADE, props);
    }
}
