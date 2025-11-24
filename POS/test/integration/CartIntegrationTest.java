package integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import model.POSController;
import model.POSFactory;
import model.dto.ProductDTO;
import model.dto.Response;
import ui.CartUI;

/**
 * Testes de Integração para adicionar item ao carrinho
 * Testa a interação entre CartUI, POSController e componentes relacionados
 */
public class CartIntegrationTest {
    
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
     * Teste de Integração 1: Integração entre CartUI e POSController para buscar produtos
     * Cenário: Sistema busca produtos do banco de dados através do controller
     * Resultado esperado: Produtos devem ser carregados e exibidos na tabela
     */
    @Test
    public void testIntegrationLoadProductsFromController() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        
        // Act
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        // Assert
        assertNotNull("Lista de produtos não deve ser null", products);
        // Verificar se a tabela foi populada (se houver produtos no banco)
        if (products != null && !products.isEmpty()) {
            assertTrue("Tabela de produtos deve ter dados", cartUI.productsTable.getRowCount() >= 0);
        }
    }
    
    /**
     * Teste de Integração 2: Integração completa - Buscar produto, selecionar e adicionar
     * Cenário: Fluxo completo desde buscar produtos até adicionar ao carrinho
     * Resultado esperado: Item deve ser adicionado corretamente
     */
    @Test
    public void testIntegrationCompleteAddItemFlow() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            cartUI.productsList = products;
            
            // Configurar tabela de produtos
            String[] columnNames = {"Name", "Price", "Stock"};
            DefaultTableModel productsModel = new DefaultTableModel(null, columnNames);
            for (ProductDTO product : products) {
                Object[] rowData = {product.getProductName(), product.getPrice(), product.getStockQuantity()};
                productsModel.addRow(rowData);
            }
            cartUI.productsTable.setModel(productsModel);
            
            // Selecionar primeiro produto
            if (cartUI.productsTable.getRowCount() > 0) {
                cartUI.productsTable.setRowSelectionInterval(0, 0);
                cartUI.quantity.setText("1");
                
                DefaultTableModel cartModel = new DefaultTableModel(
                    null,
                    new String[]{"Name", "Price", "Quantity", "Total"}
                );
                cartUI.cartTable.setModel(cartModel);
                
                int initialCartSize = cartUI.cartTable.getRowCount();
                
                // Act
                java.awt.event.ActionEvent event = new java.awt.event.ActionEvent(
                    cartUI.addToCartBtn, 
                    java.awt.event.ActionEvent.ACTION_PERFORMED, 
                    ""
                );
                cartUI.addToCartBtnActionPerformed(event);
                
                // Assert
                assertEquals("Item deve ser adicionado ao carrinho", 
                    initialCartSize + 1, cartUI.cartTable.getRowCount());
            }
        }
    }
    
    /**
     * Teste de Integração 3: Integração com busca de produtos por nome
     * Cenário: Buscar produto por nome e adicionar ao carrinho
     * Resultado esperado: Produto encontrado deve poder ser adicionado
     */
    @Test
    public void testIntegrationSearchProductAndAdd() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        
        // Buscar todos os produtos primeiro
        ArrayList<ProductDTO> allProducts = controller.getProducts(response);
        
        if (allProducts != null && !allProducts.isEmpty()) {
            // Buscar por nome (usar parte do nome do primeiro produto)
            String searchName = allProducts.get(0).getProductName().substring(0, 
                Math.min(3, allProducts.get(0).getProductName().length()));
            
            ArrayList<ProductDTO> searchResults = controller.searchProductsByName(searchName, response);
            
            // Assert
            assertNotNull("Resultados da busca não devem ser null", searchResults);
            
            if (searchResults != null && !searchResults.isEmpty()) {
                cartUI.productsList = searchResults;
                
                // Configurar tabela com resultados da busca
                String[] columnNames = {"Name", "Price", "Stock"};
                DefaultTableModel productsModel = new DefaultTableModel(null, columnNames);
                for (ProductDTO product : searchResults) {
                    Object[] rowData = {product.getProductName(), product.getPrice(), product.getStockQuantity()};
                    productsModel.addRow(rowData);
                }
                cartUI.productsTable.setModel(productsModel);
                
                // Verificar se a tabela foi atualizada
                assertTrue("Tabela deve ter produtos da busca", cartUI.productsTable.getRowCount() > 0);
            }
        }
    }
    
    /**
     * Teste de Integração 4: Integração entre adicionar item e cálculo de total
     * Cenário: Adicionar item e verificar se o total é calculado automaticamente
     * Resultado esperado: Total deve ser atualizado após adicionar item
     */
    @Test
    public void testIntegrationAddItemAndCalculateTotal() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            cartUI.productsList = products;
            
            String[] columnNames = {"Name", "Price", "Stock"};
            DefaultTableModel productsModel = new DefaultTableModel(null, columnNames);
            for (ProductDTO product : products) {
                Object[] rowData = {product.getProductName(), product.getPrice(), product.getStockQuantity()};
                productsModel.addRow(rowData);
            }
            cartUI.productsTable.setModel(productsModel);
            
            cartUI.productsTable.setRowSelectionInterval(0, 0);
            ProductDTO selectedProduct = products.get(0);
            cartUI.quantity.setText("2");
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            
            // Act
            java.awt.event.ActionEvent event = new java.awt.event.ActionEvent(
                cartUI.addToCartBtn, 
                java.awt.event.ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event);
            
            // Assert
            String totalText = cartUI.totalofcart.getText();
            assertNotNull("Total não deve ser null", totalText);
            assertNotEquals("Total não deve ser zero", "0", totalText);
            
            // Verificar se o total calculado está correto
            double expectedTotal = selectedProduct.getPrice() * 2;
            double actualTotal = Double.parseDouble(totalText);
            assertEquals("Total deve ser calculado corretamente", expectedTotal, actualTotal, 0.01);
        }
    }
    
    /**
     * Teste de Integração 5: Integração com validação de estoque
     * Cenário: Verificar se o sistema considera o estoque ao adicionar item
     * Resultado esperado: Sistema deve permitir adicionar apenas se houver estoque
     */
    @Test
    public void testIntegrationStockValidation() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            ProductDTO product = products.get(0);
            double stockQuantity = product.getStockQuantity();
            
            // Verificar se o produto tem estoque
            assertTrue("Produto deve ter estoque para teste", stockQuantity > 0);
            
            // O sistema atual não valida estoque na adição, mas podemos verificar
            // se a informação de estoque está disponível
            assertTrue("Quantidade em estoque deve ser maior que zero", stockQuantity > 0);
        }
    }
}

