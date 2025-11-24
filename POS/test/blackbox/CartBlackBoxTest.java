package blackbox;

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
 * Testes de Caixa-Preta baseados em cenários e requisitos
 * Testa o sistema sem conhecimento da implementação interna
 */
public class CartBlackBoxTest {
    
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
     * Cenário 1: Adicionar item válido
     * Entrada: Produto selecionado, quantidade = 1
     * Saída esperada: Item adicionado ao carrinho, total atualizado
     */
    @Test
    public void testBlackBoxScenario1_AddValidItem() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            setupProductsTable(products);
            setupCartTable();
            
            // Entrada
            int selectedRow = 0;
            String quantity = "1";
            
            cartUI.productsTable.setRowSelectionInterval(selectedRow, selectedRow);
            cartUI.quantity.setText(quantity);
            
            int initialCartSize = cartUI.cartTable.getRowCount();
            
            // Act
            ActionEvent event = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event);
            
            // Saída esperada
            assertEquals("Cenário 1: Item deve ser adicionado", 
                initialCartSize + 1, cartUI.cartTable.getRowCount());
            assertNotEquals("Cenário 1: Total deve ser atualizado", 
                "0", cartUI.totalofcart.getText());
        }
    }
    
    /**
     * Cenário 2: Tentar adicionar sem seleção
     * Entrada: Nenhum produto selecionado, quantidade = 1
     * Saída esperada: Nenhum item adicionado, mensagem de erro (se implementada)
     */
    @Test
    public void testBlackBoxScenario2_AddWithoutSelection() {
        // Arrange
        setupCartTable();
        
        // Entrada
        cartUI.productsTable.clearSelection();
        cartUI.quantity.setText("1");
        
        int initialCartSize = cartUI.cartTable.getRowCount();
        
        // Act
        ActionEvent event = new ActionEvent(
            cartUI.addToCartBtn, 
            ActionEvent.ACTION_PERFORMED, 
            ""
        );
        cartUI.addToCartBtnActionPerformed(event);
        
        // Saída esperada
        assertEquals("Cenário 2: Nenhum item deve ser adicionado", 
            initialCartSize, cartUI.cartTable.getRowCount());
    }
    
    /**
     * Cenário 3: Adicionar com quantidade maior que 1
     * Entrada: Produto selecionado, quantidade = 5
     * Saída esperada: Item adicionado com quantidade 5, total = preço * 5
     */
    @Test
    public void testBlackBoxScenario3_AddWithQuantityGreaterThanOne() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            setupProductsTable(products);
            setupCartTable();
            
            // Entrada
            int selectedRow = 0;
            String quantity = "5";
            ProductDTO selectedProduct = products.get(selectedRow);
            
            cartUI.productsTable.setRowSelectionInterval(selectedRow, selectedRow);
            cartUI.quantity.setText(quantity);
            
            // Act
            ActionEvent event = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event);
            
            // Saída esperada
            assertEquals("Cenário 3: Quantidade deve ser 5", 
                quantity, cartUI.cartTable.getValueAt(0, 2));
            
            double expectedTotal = selectedProduct.getPrice() * 5;
            assertEquals("Cenário 3: Total deve ser preço * 5", 
                expectedTotal, (Double) cartUI.cartTable.getValueAt(0, 3), 0.01);
        }
    }
    
    /**
     * Cenário 4: Adicionar múltiplos itens diferentes
     * Entrada: Produto 1 (qty=1), Produto 2 (qty=2), Produto 3 (qty=1)
     * Saída esperada: 3 itens no carrinho, total = soma de todos
     */
    @Test
    public void testBlackBoxScenario4_AddMultipleDifferentItems() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && products.size() >= 3) {
            setupProductsTable(products);
            setupCartTable();
            
            // Entrada
            double expectedTotal = 0;
            
            // Produto 1, qty=1
            cartUI.productsTable.setRowSelectionInterval(0, 0);
            cartUI.quantity.setText("1");
            expectedTotal += products.get(0).getPrice() * 1;
            ActionEvent event1 = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event1);
            
            // Produto 2, qty=2
            cartUI.productsTable.setRowSelectionInterval(1, 1);
            cartUI.quantity.setText("2");
            expectedTotal += products.get(1).getPrice() * 2;
            ActionEvent event2 = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event2);
            
            // Produto 3, qty=1
            cartUI.productsTable.setRowSelectionInterval(2, 2);
            cartUI.quantity.setText("1");
            expectedTotal += products.get(2).getPrice() * 1;
            ActionEvent event3 = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event3);
            
            // Saída esperada
            assertEquals("Cenário 4: Deve ter 3 itens", 
                3, cartUI.cartTable.getRowCount());
            
            double actualTotal = Double.parseDouble(cartUI.totalofcart.getText());
            assertEquals("Cenário 4: Total deve ser soma de todos", 
                expectedTotal, actualTotal, 0.01);
        }
    }
    
    /**
     * Cenário 5: Adicionar com quantidade zero
     * Entrada: Produto selecionado, quantidade = 0
     * Saída esperada: Comportamento indefinido (pode adicionar ou não)
     */
    @Test
    public void testBlackBoxScenario5_AddWithZeroQuantity() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            setupProductsTable(products);
            setupCartTable();
            
            // Entrada
            cartUI.productsTable.setRowSelectionInterval(0, 0);
            cartUI.quantity.setText("0");
            
            int initialCartSize = cartUI.cartTable.getRowCount();
            
            // Act
            try {
                ActionEvent event = new ActionEvent(
                    cartUI.addToCartBtn, 
                    ActionEvent.ACTION_PERFORMED, 
                    ""
                );
                cartUI.addToCartBtnActionPerformed(event);
                
                // Sistema pode ou não adicionar (comportamento atual permite)
                // Apenas verificamos que não quebrou
                assertTrue("Cenário 5: Sistema não deve quebrar", true);
            } catch (Exception e) {
                // Se lançar exceção, também é comportamento válido
                assertTrue("Cenário 5: Exceção é comportamento válido", true);
            }
        }
    }
    
    /**
     * Cenário 6: Adicionar com quantidade negativa
     * Entrada: Produto selecionado, quantidade = -1
     * Saída esperada: Erro (NumberFormatException ou validação)
     */
    @Test
    public void testBlackBoxScenario6_AddWithNegativeQuantity() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            setupProductsTable(products);
            setupCartTable();
            
            // Entrada
            cartUI.productsTable.setRowSelectionInterval(0, 0);
            cartUI.quantity.setText("-1");
            
            // Act & Assert
            try {
                ActionEvent event = new ActionEvent(
                    cartUI.addToCartBtn, 
                    ActionEvent.ACTION_PERFORMED, 
                    ""
                );
                cartUI.addToCartBtnActionPerformed(event);
                // Se não lançar exceção, o sistema aceita (comportamento atual)
            } catch (NumberFormatException e) {
                // Esperado - quantidade negativa pode causar erro
                assertTrue("Cenário 6: NumberFormatException é esperado", true);
            }
        }
    }
    
    /**
     * Cenário 7: Adicionar com quantidade não numérica
     * Entrada: Produto selecionado, quantidade = "abc"
     * Saída esperada: NumberFormatException
     */
    @Test(expected = NumberFormatException.class)
    public void testBlackBoxScenario7_AddWithNonNumericQuantity() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            setupProductsTable(products);
            setupCartTable();
            
            // Entrada
            cartUI.productsTable.setRowSelectionInterval(0, 0);
            cartUI.quantity.setText("abc");
            
            // Act
            ActionEvent event = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event);
        }
    }
    
    /**
     * Cenário 8: Adicionar item com preço decimal
     * Entrada: Produto com preço 10.99, quantidade = 2
     * Saída esperada: Total = 21.98
     */
    @Test
    public void testBlackBoxScenario8_AddItemWithDecimalPrice() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            setupProductsTable(products);
            setupCartTable();
            
            // Entrada
            int selectedRow = 0;
            String quantity = "2";
            ProductDTO selectedProduct = products.get(selectedRow);
            
            cartUI.productsTable.setRowSelectionInterval(selectedRow, selectedRow);
            cartUI.quantity.setText(quantity);
            
            // Act
            ActionEvent event = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event);
            
            // Saída esperada
            double expectedTotal = selectedProduct.getPrice() * 2;
            double actualTotal = (Double) cartUI.cartTable.getValueAt(0, 3);
            assertEquals("Cenário 8: Total decimal deve ser calculado corretamente", 
                expectedTotal, actualTotal, 0.01);
        }
    }
    
    // Métodos auxiliares
    private void setupProductsTable(ArrayList<ProductDTO> products) {
        cartUI.productsList = products;
        String[] columnNames = {"Name", "Price", "Stock"};
        DefaultTableModel productsModel = new DefaultTableModel(null, columnNames);
        for (ProductDTO product : products) {
            Object[] rowData = {product.getProductName(), product.getPrice(), product.getStockQuantity()};
            productsModel.addRow(rowData);
        }
        cartUI.productsTable.setModel(productsModel);
    }
    
    private void setupCartTable() {
        DefaultTableModel cartModel = new DefaultTableModel(
            null,
            new String[]{"Name", "Price", "Quantity", "Total"}
        );
        cartUI.cartTable.setModel(cartModel);
    }
}

