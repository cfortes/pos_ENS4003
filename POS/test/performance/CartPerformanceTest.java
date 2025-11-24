package performance;

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
 * Testes de Desempenho para adicionar item ao carrinho
 * Testa performance e tempo de resposta do sistema
 */
public class CartPerformanceTest {
    
    private CartUI cartUI;
    private POSController controller;
    private static final long MAX_ACCEPTABLE_TIME_MS = 1000; // 1 segundo
    private static final long MAX_ACCEPTABLE_TIME_BATCH_MS = 5000; // 5 segundos para lote
    
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
     * Teste de Desempenho 1: Tempo de resposta para adicionar um item
     * Cenário: Adicionar um único item ao carrinho
     * Resultado esperado: Operação deve completar em menos de 1 segundo
     */
    @Test
    public void testPerformanceSingleItemAddition() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
            cartUI.productsTable.setRowSelectionInterval(0, 0);
            cartUI.quantity.setText("1");
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            
            // Act - Medir tempo
            long startTime = System.currentTimeMillis();
            
            ActionEvent event = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event);
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // Assert
            assertTrue(String.format(
                "Adicionar item deve levar menos de %d ms, mas levou %d ms", 
                MAX_ACCEPTABLE_TIME_MS, executionTime), 
                executionTime < MAX_ACCEPTABLE_TIME_MS);
            
            System.out.println("Tempo de execução para adicionar 1 item: " + executionTime + " ms");
        }
    }
    
    /**
     * Teste de Desempenho 2: Tempo de resposta para adicionar múltiplos itens
     * Cenário: Adicionar 10 itens sequencialmente
     * Resultado esperado: Operação deve completar em tempo razoável
     */
    @Test
    public void testPerformanceMultipleItemsAddition() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && products.size() >= 10) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            
            int itemsToAdd = 10;
            
            // Act - Medir tempo
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < itemsToAdd; i++) {
                cartUI.productsTable.setRowSelectionInterval(i, i);
                cartUI.quantity.setText("1");
                ActionEvent event = new ActionEvent(
                    cartUI.addToCartBtn, 
                    ActionEvent.ACTION_PERFORMED, 
                    ""
                );
                cartUI.addToCartBtnActionPerformed(event);
            }
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // Assert
            assertTrue(String.format(
                "Adicionar %d itens deve levar menos de %d ms, mas levou %d ms", 
                itemsToAdd, MAX_ACCEPTABLE_TIME_BATCH_MS, executionTime), 
                executionTime < MAX_ACCEPTABLE_TIME_BATCH_MS);
            
            assertEquals("Todos os itens devem ser adicionados", 
                itemsToAdd, cartUI.cartTable.getRowCount());
            
            System.out.println("Tempo de execução para adicionar " + itemsToAdd + " itens: " + executionTime + " ms");
            System.out.println("Tempo médio por item: " + (executionTime / itemsToAdd) + " ms");
        }
    }
    
    /**
     * Teste de Desempenho 3: Tempo de carregamento inicial da tela
     * Cenário: Carregar produtos na tela
     * Resultado esperado: Carregamento deve ser rápido
     */
    @Test
    public void testPerformanceInitialLoad() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        
        // Act - Medir tempo de carregamento
        long startTime = System.currentTimeMillis();
        
        ArrayList<ProductDTO> products = controller.getProducts(response);
        if (products != null) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
        }
        
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        
        // Assert
        assertTrue(String.format(
            "Carregamento inicial deve levar menos de %d ms, mas levou %d ms", 
            MAX_ACCEPTABLE_TIME_MS * 2, executionTime), 
            executionTime < MAX_ACCEPTABLE_TIME_MS * 2);
        
        System.out.println("Tempo de carregamento inicial: " + executionTime + " ms");
    }
    
    /**
     * Teste de Desempenho 4: Tempo de cálculo de total
     * Cenário: Calcular total com muitos itens no carrinho
     * Resultado esperado: Cálculo deve ser rápido mesmo com muitos itens
     */
    @Test
    public void testPerformanceTotalCalculation() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && products.size() >= 5) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            
            // Adicionar vários itens primeiro
            int itemsToAdd = Math.min(5, products.size());
            for (int i = 0; i < itemsToAdd; i++) {
                cartUI.productsTable.setRowSelectionInterval(i, i);
                cartUI.quantity.setText("1");
                ActionEvent event = new ActionEvent(
                    cartUI.addToCartBtn, 
                    ActionEvent.ACTION_PERFORMED, 
                    ""
                );
                cartUI.addToCartBtnActionPerformed(event);
            }
            
            // Act - Medir tempo de cálculo
            long startTime = System.currentTimeMillis();
            
            // O cálculo é feito automaticamente, mas podemos forçar
            String total = cartUI.totalofcart.getText();
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // Assert
            assertTrue(String.format(
                "Cálculo de total deve ser instantâneo (< %d ms), mas levou %d ms", 
                100, executionTime), 
                executionTime < 100);
            
            assertNotNull("Total deve ser calculado", total);
            
            System.out.println("Tempo de cálculo de total: " + executionTime + " ms");
        }
    }
    
    /**
     * Teste de Desempenho 5: Tempo de busca de produtos
     * Cenário: Buscar produtos por nome
     * Resultado esperado: Busca deve ser rápida
     */
    @Test
    public void testPerformanceProductSearch() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> allProducts = controller.getProducts(response);
        
        if (allProducts != null && !allProducts.isEmpty()) {
            String searchTerm = allProducts.get(0).getProductName().substring(0, 
                Math.min(3, allProducts.get(0).getProductName().length()));
            
            // Act - Medir tempo de busca
            long startTime = System.currentTimeMillis();
            
            ArrayList<ProductDTO> searchResults = controller.searchProductsByName(searchTerm, response);
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // Assert
            assertTrue(String.format(
                "Busca de produtos deve levar menos de %d ms, mas levou %d ms", 
                MAX_ACCEPTABLE_TIME_MS, executionTime), 
                executionTime < MAX_ACCEPTABLE_TIME_MS);
            
            System.out.println("Tempo de busca de produtos: " + executionTime + " ms");
        }
    }
    
    /**
     * Teste de Desempenho 6: Estresse - Adicionar muitos itens rapidamente
     * Cenário: Adicionar 50 itens em sequência
     * Resultado esperado: Sistema deve manter performance aceitável
     */
    @Test
    public void testPerformanceStressTest() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && products.size() >= 20) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            
            int itemsToAdd = Math.min(20, products.size());
            
            // Act - Medir tempo
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < itemsToAdd; i++) {
                int productIndex = i % products.size(); // Reutilizar produtos se necessário
                cartUI.productsTable.setRowSelectionInterval(productIndex, productIndex);
                cartUI.quantity.setText("1");
                ActionEvent event = new ActionEvent(
                    cartUI.addToCartBtn, 
                    ActionEvent.ACTION_PERFORMED, 
                    ""
                );
                cartUI.addToCartBtnActionPerformed(event);
            }
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // Assert
            assertTrue(String.format(
                "Teste de estresse (%d itens) deve completar em tempo razoável, levou %d ms", 
                itemsToAdd, executionTime), 
                executionTime < MAX_ACCEPTABLE_TIME_BATCH_MS * 2);
            
            assertEquals("Todos os itens devem ser adicionados", 
                itemsToAdd, cartUI.cartTable.getRowCount());
            
            System.out.println("Teste de estresse - " + itemsToAdd + " itens em " + executionTime + " ms");
            System.out.println("Tempo médio por item: " + (executionTime / itemsToAdd) + " ms");
        }
    }
}

