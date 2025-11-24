package system;

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
 * Testes de Sistema para adicionar item ao carrinho
 * Testa o sistema completo end-to-end
 */
public class CartSystemTest {
    
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
     * Teste de Sistema 1: Fluxo completo de adicionar item
     * Cenário: Usuário acessa a tela, visualiza produtos, seleciona e adiciona
     * Resultado esperado: Item adicionado com sucesso e total atualizado
     */
    @Test
    public void testSystemCompleteAddItemFlow() {
        // Arrange - Simular estado inicial do sistema
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        assertNotNull("Sistema deve retornar lista de produtos", products);
        
        if (products != null && !products.isEmpty()) {
            // Simular carregamento inicial da tela
            cartUI.productsList = products;
            cartUI.populateProductsData();
            
            // Verificar se produtos foram carregados
            assertTrue("Sistema deve carregar produtos na tela", 
                cartUI.productsTable.getRowCount() > 0);
            
            // Simular seleção de produto pelo usuário
            int selectedIndex = 0;
            cartUI.productsTable.setRowSelectionInterval(selectedIndex, selectedIndex);
            cartUI.quantity.setText("1");
            
            // Verificar estado antes da ação
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            int initialCartSize = cartUI.cartTable.getRowCount();
            
            // Act - Simular ação do usuário (clicar em adicionar)
            ActionEvent event = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event);
            
            // Assert - Verificar resultado final do sistema
            assertEquals("Sistema deve adicionar item ao carrinho", 
                initialCartSize + 1, cartUI.cartTable.getRowCount());
            
            // Verificar se total foi atualizado
            String totalText = cartUI.totalofcart.getText();
            assertNotNull("Sistema deve calcular total", totalText);
            assertNotEquals("Total não deve ser zero", "0", totalText);
            
            // Verificar se seleção foi limpa
            assertEquals("Sistema deve limpar seleção após adicionar", 
                -1, cartUI.productsTable.getSelectedRow());
        }
    }
    
    /**
     * Teste de Sistema 2: Sistema com múltiplas operações sequenciais
     * Cenário: Adicionar vários itens em sequência
     * Resultado esperado: Todos os itens adicionados e total correto
     */
    @Test
    public void testSystemMultipleSequentialAdditions() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && products.size() >= 2) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            
            // Act - Adicionar primeiro item
            cartUI.productsTable.setRowSelectionInterval(0, 0);
            cartUI.quantity.setText("1");
            ActionEvent event1 = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event1);
            
            // Adicionar segundo item
            cartUI.productsTable.setRowSelectionInterval(1, 1);
            cartUI.quantity.setText("2");
            ActionEvent event2 = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event2);
            
            // Assert
            assertEquals("Sistema deve ter 2 itens no carrinho", 
                2, cartUI.cartTable.getRowCount());
            
            // Verificar total acumulado
            double total1 = products.get(0).getPrice() * 1;
            double total2 = products.get(1).getPrice() * 2;
            double expectedTotal = total1 + total2;
            
            double actualTotal = Double.parseDouble(cartUI.totalofcart.getText());
            assertEquals("Sistema deve calcular total acumulado corretamente", 
                expectedTotal, actualTotal, 0.01);
        }
    }
    
    /**
     * Teste de Sistema 3: Sistema com busca e adição
     * Cenário: Buscar produto e adicionar ao carrinho
     * Resultado esperado: Produto encontrado e adicionado corretamente
     */
    @Test
    public void testSystemSearchAndAddFlow() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> allProducts = controller.getProducts(response);
        
        if (allProducts != null && !allProducts.isEmpty()) {
            // Simular busca
            String searchTerm = allProducts.get(0).getProductName().substring(0, 
                Math.min(3, allProducts.get(0).getProductName().length()));
            
            ArrayList<ProductDTO> searchResults = controller.searchProductsByName(searchTerm, response);
            
            if (searchResults != null && !searchResults.isEmpty()) {
                cartUI.productsList = searchResults;
                cartUI.updateProductsTableData(searchResults);
                
                // Verificar se busca funcionou
                assertTrue("Sistema deve exibir resultados da busca", 
                    cartUI.productsTable.getRowCount() > 0);
                
                // Adicionar item encontrado
                cartUI.productsTable.setRowSelectionInterval(0, 0);
                cartUI.quantity.setText("1");
                
                DefaultTableModel cartModel = new DefaultTableModel(
                    null,
                    new String[]{"Name", "Price", "Quantity", "Total"}
                );
                cartUI.cartTable.setModel(cartModel);
                
                // Act
                ActionEvent event = new ActionEvent(
                    cartUI.addToCartBtn, 
                    ActionEvent.ACTION_PERFORMED, 
                    ""
                );
                cartUI.addToCartBtnActionPerformed(event);
                
                // Assert
                assertEquals("Sistema deve adicionar produto encontrado", 
                    1, cartUI.cartTable.getRowCount());
            }
        }
    }
    
    /**
     * Teste de Sistema 4: Sistema com tratamento de erro
     * Cenário: Tentar adicionar sem seleção
     * Resultado esperado: Sistema deve tratar erro graciosamente
     */
    @Test
    public void testSystemErrorHandling() {
        // Arrange
        cartUI.productsTable.clearSelection();
        cartUI.quantity.setText("1");
        
        DefaultTableModel cartModel = new DefaultTableModel(
            null,
            new String[]{"Name", "Price", "Quantity", "Total"}
        );
        cartUI.cartTable.setModel(cartModel);
        int initialSize = cartUI.cartTable.getRowCount();
        
        // Act
        ActionEvent event = new ActionEvent(
            cartUI.addToCartBtn, 
            ActionEvent.ACTION_PERFORMED, 
            ""
        );
        cartUI.addToCartBtnActionPerformed(event);
        
        // Assert - Sistema não deve adicionar item
        assertEquals("Sistema não deve adicionar item sem seleção", 
            initialSize, cartUI.cartTable.getRowCount());
    }
}

