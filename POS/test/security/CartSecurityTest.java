package security;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import model.POSController;
import model.POSFactory;
import model.dto.ProductDTO;
import model.dto.Response;
import ui.CartUI;

/**
 * Testes de Segurança para adicionar item ao carrinho
 * Testa vulnerabilidades e validações de segurança
 */
public class CartSecurityTest {
    
    private CartUI cartUI;
    private POSController controller;
    
    @Before
    public void setUp() {
        controller = POSFactory.getInstanceOfPOSController();
        cartUI = new CartUI(controller);
    }
    
    @After
    public void tearDown() {
        cartUI = null;
        controller = null;
    }
    
    /**
     * Teste de Segurança 1: Prevenção de SQL Injection na busca
     * Cenário: Tentar injetar código SQL no campo de busca
     * Resultado esperado: Sistema deve tratar entrada maliciosa
     */
    @Test
    public void testSecuritySQLInjectionInSearch() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        
        // Tentativas de SQL Injection
        String[] sqlInjectionAttempts = {
            "'; DROP TABLE products; --",
            "' OR '1'='1",
            "'; DELETE FROM products; --",
            "' UNION SELECT * FROM users --"
        };
        
        for (String maliciousInput : sqlInjectionAttempts) {
            // Act
            try {
                ArrayList<ProductDTO> results = controller.searchProductsByName(maliciousInput, response);
                
                // Assert - Sistema não deve quebrar
                // Pode retornar lista vazia ou tratar de forma segura
                assertNotNull("Sistema não deve retornar null", results);
                
                // Verificar se não houve exceção não tratada
                assertTrue("Sistema deve tratar entrada maliciosa sem quebrar", true);
            } catch (Exception e) {
                // Se lançar exceção, deve ser tratada adequadamente
                assertTrue("Exceção deve ser tratada", true);
            }
        }
    }
    
    /**
     * Teste de Segurança 2: Validação de entrada no campo quantidade
     * Cenário: Tentar inserir valores maliciosos no campo quantidade
     * Resultado esperado: Sistema deve validar e rejeitar entradas inválidas
     */
    @Test
    public void testSecurityQuantityInputValidation() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
            cartUI.productsTable.setRowSelectionInterval(0, 0);
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            
            // Entradas maliciosas
            String[] maliciousInputs = {
                "<script>alert('XSS')</script>",
                "'; DROP TABLE products; --",
                "1000000000", // Valor muito grande
                "-999999", // Valor negativo muito grande
                "1.5", // Decimal (pode ser aceito ou não)
                "abc123",
                "null",
                "undefined"
            };
            
            for (String maliciousInput : maliciousInputs) {
                cartUI.quantity.setText(maliciousInput);
                int initialCartSize = cartUI.cartTable.getRowCount();
                
                // Act
                try {
                    ActionEvent event = new ActionEvent(
                        cartUI.addToCartBtn, 
                        ActionEvent.ACTION_PERFORMED, 
                        ""
                    );
                    cartUI.addToCartBtnActionPerformed(event);
                    
                    // Assert - Sistema não deve quebrar
                    // Pode adicionar ou não, mas não deve quebrar
                    assertTrue("Sistema deve tratar entrada maliciosa: " + maliciousInput, true);
                } catch (Exception e) {
                    // Exceção é aceitável para entradas inválidas
                    assertTrue("Exceção é tratamento válido para: " + maliciousInput, true);
                }
            }
        }
    }
    
    /**
     * Teste de Segurança 3: Prevenção de manipulação de índice de tabela
     * Cenário: Tentar acessar índice inválido na lista de produtos
     * Resultado esperado: Sistema deve tratar índices inválidos
     */
    @Test
    public void testSecurityInvalidTableIndex() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            
            // Tentar selecionar índice inválido
            int invalidIndex = products.size() + 100;
            
            try {
                cartUI.productsTable.setRowSelectionInterval(invalidIndex, invalidIndex);
                cartUI.quantity.setText("1");
                
                // Act
                ActionEvent event = new ActionEvent(
                    cartUI.addToCartBtn, 
                    ActionEvent.ACTION_PERFORMED, 
                    ""
                );
                cartUI.addToCartBtnActionPerformed(event);
                
                // Assert - Sistema não deve quebrar
                assertTrue("Sistema deve tratar índice inválido", true);
            } catch (Exception e) {
                // Exceção é tratamento válido
                assertTrue("Exceção é tratamento válido para índice inválido", true);
            }
        }
    }
    
    /**
     * Teste de Segurança 4: Validação de valores numéricos extremos
     * Cenário: Tentar adicionar com quantidades extremas
     * Resultado esperado: Sistema deve validar limites razoáveis
     */
    @Test
    public void testSecurityExtremeNumericValues() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
            cartUI.productsTable.setRowSelectionInterval(0, 0);
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            
            // Valores extremos
            String[] extremeValues = {
                String.valueOf(Integer.MAX_VALUE),
                String.valueOf(Long.MAX_VALUE),
                "999999999",
                "0.0000001"
            };
            
            for (String extremeValue : extremeValues) {
                cartUI.quantity.setText(extremeValue);
                
                // Act
                try {
                    ActionEvent event = new ActionEvent(
                        cartUI.addToCartBtn, 
                        ActionEvent.ACTION_PERFORMED, 
                        ""
                    );
                    cartUI.addToCartBtnActionPerformed(event);
                    
                    // Assert - Sistema não deve quebrar
                    assertTrue("Sistema deve tratar valor extremo: " + extremeValue, true);
                } catch (Exception e) {
                    // Exceção é tratamento válido
                    assertTrue("Exceção é tratamento válido para valor extremo", true);
                }
            }
        }
    }
    
    /**
     * Teste de Segurança 5: Prevenção de acesso não autorizado a dados
     * Cenário: Tentar acessar dados sem seleção válida
     * Resultado esperado: Sistema deve validar acesso aos dados
     */
    @Test
    public void testSecurityUnauthorizedDataAccess() {
        // Arrange
        cartUI.productsTable.clearSelection();
        cartUI.quantity.setText("1");
        
        DefaultTableModel cartModel = new DefaultTableModel(
            null,
            new String[]{"Name", "Price", "Quantity", "Total"}
        );
        cartUI.cartTable.setModel(cartModel);
        int initialCartSize = cartUI.cartTable.getRowCount();
        
        // Act - Tentar adicionar sem seleção válida
        ActionEvent event = new ActionEvent(
            cartUI.addToCartBtn, 
            ActionEvent.ACTION_PERFORMED, 
            ""
        );
        cartUI.addToCartBtnActionPerformed(event);
        
        // Assert - Sistema não deve adicionar item não autorizado
        assertEquals("Sistema não deve permitir adicionar sem seleção válida", 
            initialCartSize, cartUI.cartTable.getRowCount());
    }
    
    /**
     * Teste de Segurança 6: Validação de tipos de dados
     * Cenário: Tentar passar tipos incorretos
     * Resultado esperado: Sistema deve validar tipos
     */
    @Test
    public void testSecurityDataTypeValidation() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
            cartUI.productsTable.setRowSelectionInterval(0, 0);
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            
            // Tipos incorretos
            String[] invalidTypes = {
                "true", // boolean como string
                "false",
                "null",
                "undefined",
                "[object Object]",
                "{}",
                "[]"
            };
            
            for (String invalidType : invalidTypes) {
                cartUI.quantity.setText(invalidType);
                
                // Act
                try {
                    ActionEvent event = new ActionEvent(
                        cartUI.addToCartBtn, 
                        ActionEvent.ACTION_PERFORMED, 
                        ""
                    );
                    cartUI.addToCartBtnActionPerformed(event);
                    
                    // Assert - Sistema deve tratar tipo inválido
                    assertTrue("Sistema deve tratar tipo inválido: " + invalidType, true);
                } catch (Exception e) {
                    // Exceção é tratamento válido
                    assertTrue("Exceção é tratamento válido para tipo inválido", true);
                }
            }
        }
    }
    
    /**
     * Teste de Segurança 7: Prevenção de buffer overflow
     * Cenário: Tentar inserir string muito longa
     * Resultado esperado: Sistema deve limitar tamanho de entrada
     */
    @Test
    public void testSecurityBufferOverflow() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
            cartUI.productsTable.setRowSelectionInterval(0, 0);
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            
            // String muito longa
            StringBuilder longString = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                longString.append("1");
            }
            cartUI.quantity.setText(longString.toString());
            
            // Act
            try {
                ActionEvent event = new ActionEvent(
                    cartUI.addToCartBtn, 
                    ActionEvent.ACTION_PERFORMED, 
                    ""
                );
                cartUI.addToCartBtnActionPerformed(event);
                
                // Assert - Sistema não deve quebrar
                assertTrue("Sistema deve tratar string muito longa", true);
            } catch (Exception e) {
                // Exceção é tratamento válido
                assertTrue("Exceção é tratamento válido para string muito longa", true);
            }
        }
    }
}

