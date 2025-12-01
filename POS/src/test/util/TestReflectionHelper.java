package test.util;

import java.lang.reflect.Field;

/**
 * Classe utilitária para acessar campos privados em testes usando reflexão
 */
public class TestReflectionHelper {
    
    /**
     * Obtém o valor de um campo privado de um objeto
     */
    @SuppressWarnings("unchecked")
    public static <T> T getPrivateField(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Erro ao acessar campo privado: " + fieldName, e);
        }
    }
    
    /**
     * Define o valor de um campo privado de um objeto
     */
    public static void setPrivateField(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Erro ao definir campo privado: " + fieldName, e);
        }
    }
}

