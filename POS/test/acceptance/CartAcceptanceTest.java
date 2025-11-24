package acceptance;

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
 * Testes de Aceitação para adicionar item ao carrinho
 * Testa requisitos de negócio e casos de uso do usuário
 */
public class CartAcceptanceTest {
    
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
     * Teste de Aceitação 1: Requisito - Usuário deve poder adicionar produto ao carrinho
     * Critério de Aceitação: 
     * - Dado que o usuário está na tela de vendas
     * - Quando o usuário seleciona um produto e clica em "Adicionar ao Carrinho"
     * - Então o produto deve aparecer no carrinho com nome, preço, quantidade e total
     */
    @Test
    public void testAcceptanceUserCanAddProductToCart() {
        // Given - Usuário está na tela de vendas
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        assertNotNull("Dado: Sistema deve ter produtos disponíveis", products);
        
        if (products != null && !products.isEmpty()) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
            
            // When - Usuário seleciona produto e clica em adicionar
            cartUI.productsTable.setRowSelectionInterval(0, 0);
            ProductDTO selectedProduct = products.get(0);
            cartUI.quantity.setText("1");
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            
            ActionEvent event = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event);
            
            // Then - Produto deve aparecer no carrinho
            assertEquals("Então: Carrinho deve ter 1 item", 
                1, cartUI.cartTable.getRowCount());
            assertEquals("Então: Nome do produto deve estar correto", 
                selectedProduct.getProductName(), cartUI.cartTable.getValueAt(0, 0));
            assertEquals("Então: Preço deve estar correto", 
                selectedProduct.getPrice(), (Double) cartUI.cartTable.getValueAt(0, 1), 0.01);
            assertEquals("Então: Quantidade deve estar correta", 
                "1", cartUI.cartTable.getValueAt(0, 2));
            assertEquals("Então: Total deve ser calculado corretamente", 
                selectedProduct.getPrice() * 1, (Double) cartUI.cartTable.getValueAt(0, 3), 0.01);
        }
    }
    
    /**
     * Teste de Aceitação 2: Requisito - Sistema deve calcular total automaticamente
     * Critério de Aceitação:
     * - Dado que há itens no carrinho
     * - Quando um novo item é adicionado
     * - Então o total do carrinho deve ser atualizado automaticamente
     */
    @Test
    public void testAcceptanceSystemCalculatesTotalAutomatically() {
        // Given - Há itens no carrinho
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
            
            // Adicionar primeiro item
            cartUI.productsTable.setRowSelectionInterval(0, 0);
            cartUI.quantity.setText("1");
            ActionEvent event1 = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event1);
            
            double firstTotal = Double.parseDouble(cartUI.totalofcart.getText());
            
            // When - Novo item é adicionado
            cartUI.productsTable.setRowSelectionInterval(1, 1);
            cartUI.quantity.setText("1");
            ActionEvent event2 = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event2);
            
            // Then - Total deve ser atualizado
            double newTotal = Double.parseDouble(cartUI.totalofcart.getText());
            assertTrue("Então: Total deve ser maior que o anterior", newTotal > firstTotal);
            
            double expectedTotal = products.get(0).getPrice() + products.get(1).getPrice();
            assertEquals("Então: Total deve ser a soma dos itens", 
                expectedTotal, newTotal, 0.01);
        }
    }
    
    /**
     * Teste de Aceitação 3: Requisito - Usuário deve poder especificar quantidade
     * Critério de Aceitação:
     * - Dado que o usuário selecionou um produto
     * - Quando o usuário informa uma quantidade e adiciona ao carrinho
     * - Então o item deve ser adicionado com a quantidade especificada
     */
    @Test
    public void testAcceptanceUserCanSpecifyQuantity() {
        // Given - Usuário selecionou um produto
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
            cartUI.productsTable.setRowSelectionInterval(0, 0);
            ProductDTO selectedProduct = products.get(0);
            
            // When - Usuário informa quantidade e adiciona
            int quantity = 3;
            cartUI.quantity.setText(String.valueOf(quantity));
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            
            ActionEvent event = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event);
            
            // Then - Item deve ter a quantidade especificada
            assertEquals("Então: Quantidade deve ser a especificada", 
                String.valueOf(quantity), cartUI.cartTable.getValueAt(0, 2));
            
            double expectedTotal = selectedProduct.getPrice() * quantity;
            assertEquals("Então: Total deve considerar a quantidade", 
                expectedTotal, (Double) cartUI.cartTable.getValueAt(0, 3), 0.01);
        }
    }
    
    /**
     * Teste de Aceitação 4: Requisito - Sistema deve prevenir adição sem seleção
     * Critério de Aceitação:
     * - Dado que nenhum produto está selecionado
     * - Quando o usuário tenta adicionar ao carrinho
     * - Então o sistema deve exibir mensagem de erro e não adicionar item
     */
    @Test
    public void testAcceptanceSystemPreventsAddWithoutSelection() {
        // Given - Nenhum produto está selecionado
        cartUI.productsTable.clearSelection();
        cartUI.quantity.setText("1");
        
        DefaultTableModel cartModel = new DefaultTableModel(
            null,
            new String[]{"Name", "Price", "Quantity", "Total"}
        );
        cartUI.cartTable.setModel(cartModel);
        int initialSize = cartUI.cartTable.getRowCount();
        
        // When - Usuário tenta adicionar
        ActionEvent event = new ActionEvent(
            cartUI.addToCartBtn, 
            ActionEvent.ACTION_PERFORMED, 
            ""
        );
        cartUI.addToCartBtnActionPerformed(event);
        
        // Then - Sistema não deve adicionar item
        assertEquals("Então: Carrinho não deve ter novos itens", 
            initialSize, cartUI.cartTable.getRowCount());
    }
    
    /**
     * Teste de Aceitação 5: Requisito - Sistema deve permitir múltiplos itens
     * Critério de Aceitação:
     * - Dado que o usuário quer fazer uma venda com vários produtos
     * - Quando o usuário adiciona vários produtos ao carrinho
     * - Então todos os produtos devem aparecer no carrinho
     */
    @Test
    public void testAcceptanceSystemAllowsMultipleItems() {
        // Given - Usuário quer fazer venda com vários produtos
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && products.size() >= 3) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartUI.cartTable.setModel(cartModel);
            
            // When - Usuário adiciona vários produtos
            int itemsToAdd = Math.min(3, products.size());
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
            
            // Then - Todos devem aparecer no carrinho
            assertEquals("Então: Carrinho deve ter todos os itens adicionados", 
                itemsToAdd, cartUI.cartTable.getRowCount());
        }
    }
}

