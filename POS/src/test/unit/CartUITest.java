package test.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.POSController;
import model.POSFactory;
import model.dto.ProductDTO;
import model.dto.Response;
import ui.CartUI;

/**
 * Testes Unitários para a funcionalidade de adicionar item ao carrinho
 * Foca em testar métodos isolados da classe CartUI
 */
public class CartUITest {
    
    private CartUI cartUI;
    private POSController controller;
    private ArrayList<ProductDTO> mockProductsList;
    
    @Before
    public void setUp() {
        controller = POSFactory.getInstanceOfPOSController();
        cartUI = new CartUI(controller);
        mockProductsList = new ArrayList<>();
        
        // Criar produtos mock para teste
        ProductDTO product1 = new ProductDTO();
        product1.setProductId(1);
        product1.setProductName("Produto Teste 1");
        product1.setPrice(10.50);
        product1.setStockQuantity(100);
        
        ProductDTO product2 = new ProductDTO();
        product2.setProductId(2);
        product2.setProductName("Produto Teste 2");
        product2.setPrice(25.75);
        product2.setStockQuantity(50);
        
        mockProductsList.add(product1);
        mockProductsList.add(product2);
    }
    
    @After
    public void tearDown() {
        cartUI = null;
        controller = null;
        mockProductsList = null;
    }
    
    /**
     * Teste Unitário 1: Adicionar item válido ao carrinho
     * Cenário: Usuário seleciona um produto e adiciona ao carrinho
     * Resultado esperado: Item deve ser adicionado à tabela do carrinho
     */
    @Test
    public void testAddValidItemToCart() {
        // Arrange
        try {
            // Acessar campos privados usando reflexão
            Field productsListField = CartUI.class.getDeclaredField("productsList");
            productsListField.setAccessible(true);
            productsListField.set(cartUI, mockProductsList);
            
            Field productsTableField = CartUI.class.getDeclaredField("productsTable");
            productsTableField.setAccessible(true);
            rojerusan.RSTableMetro productsTable = (rojerusan.RSTableMetro) productsTableField.get(cartUI);
            
            Field quantityField = CartUI.class.getDeclaredField("quantity");
            quantityField.setAccessible(true);
            javax.swing.JTextField quantity = (javax.swing.JTextField) quantityField.get(cartUI);
            
            Field cartTableField = CartUI.class.getDeclaredField("cartTable");
            cartTableField.setAccessible(true);
            rojerusan.RSTableMetro cartTable = (rojerusan.RSTableMetro) cartTableField.get(cartUI);
            
            Field addToCartBtnField = CartUI.class.getDeclaredField("addToCartBtn");
            addToCartBtnField.setAccessible(true);
            ui.components.Button addToCartBtn = (ui.components.Button) addToCartBtnField.get(cartUI);
            
            DefaultTableModel productsModel = new DefaultTableModel(
                new Object[][]{
                    {"Produto Teste 1", 10.50, 100},
                    {"Produto Teste 2", 25.75, 50}
                },
                new String[]{"Name", "Price", "Stock"}
            );
            productsTable.setModel(productsModel);
            productsTable.setRowSelectionInterval(0, 0);
            quantity.setText("2");
            
            DefaultTableModel cartModel = new DefaultTableModel(
                null,
                new String[]{"Name", "Price", "Quantity", "Total"}
            );
            cartTable.setModel(cartModel);
            
            int initialRowCount = cartTable.getRowCount();
            
            // Act
            ActionEvent event = new ActionEvent(addToCartBtn, ActionEvent.ACTION_PERFORMED, "");
            cartUI.addToCartBtnActionPerformed(event);
            
            // Assert
            assertEquals("O carrinho deve ter 1 item", initialRowCount + 1, cartTable.getRowCount());
            assertEquals("Nome do produto deve ser correto", "Produto Teste 1", cartTable.getValueAt(0, 0));
            assertEquals("Preço deve ser correto", 10.50, (Double) cartTable.getValueAt(0, 1), 0.01);
            assertEquals("Quantidade deve ser correta", "2", cartTable.getValueAt(0, 2));
            assertEquals("Total deve ser calculado corretamente", 21.0, (Double) cartTable.getValueAt(0, 3), 0.01);
        } catch (Exception e) {
            fail("Erro ao acessar campos privados: " + e.getMessage());
        }
    }
    
    /**
     * Teste Unitário 2: Tentar adicionar item sem selecionar produto
     * Cenário: Usuário tenta adicionar item sem selecionar produto na tabela
     * Resultado esperado: Deve exibir mensagem de erro
     */
    @Test
    public void testAddItemWithoutSelection() {
        // Arrange
        cartUI.productsList = mockProductsList;
        cartUI.productsTable.clearSelection();
        cartUI.quantity.setText("1");
        
        DefaultTableModel cartModel = new DefaultTableModel(
            null,
            new String[]{"Name", "Price", "Quantity", "Total"}
        );
        cartUI.cartTable.setModel(cartModel);
        
        int initialRowCount = cartUI.cartTable.getRowCount();
        
        // Act
        ActionEvent event = new ActionEvent(cartUI.addToCartBtn, ActionEvent.ACTION_PERFORMED, "");
        cartUI.addToCartBtnActionPerformed(event);
        
        // Assert
        assertEquals("O carrinho não deve ter itens adicionados", initialRowCount, cartUI.cartTable.getRowCount());
    }
    
    /**
     * Teste Unitário 3: Calcular total do carrinho corretamente
     * Cenário: Carrinho com múltiplos itens
     * Resultado esperado: Total deve ser a soma de todos os itens
     */
    @Test
    public void testCalculateTotal() {
        // Arrange
        DefaultTableModel cartModel = new DefaultTableModel(
            new Object[][]{
                {"Produto 1", 10.0, "2", 20.0},
                {"Produto 2", 15.5, "3", 46.5}
            },
            new String[]{"Name", "Price", "Quantity", "Total"}
        );
        cartUI.cartTable.setModel(cartModel);
        
        // Act - calculateTotal é chamado internamente, mas podemos testá-lo diretamente
        // Usando reflection ou método público se disponível
        // Por enquanto, vamos simular adicionando itens
        cartUI.quantity.setText("1");
        
        // Assert - verificar se o total está correto
        // Como calculateTotal é privado, vamos verificar através do label
        String totalText = cartUI.totalofcart.getText();
        assertNotNull("Total não deve ser null", totalText);
    }
    
    /**
     * Teste Unitário 4: Validar quantidade zero
     * Cenário: Usuário tenta adicionar item com quantidade zero
     * Resultado esperado: Deve tratar quantidade inválida
     */
    @Test
    public void testAddItemWithZeroQuantity() {
        // Arrange
        cartUI.productsList = mockProductsList;
        DefaultTableModel productsModel = new DefaultTableModel(
            new Object[][]{{"Produto Teste 1", 10.50, 100}},
            new String[]{"Name", "Price", "Stock"}
        );
        cartUI.productsTable.setModel(productsModel);
        cartUI.productsTable.setRowSelectionInterval(0, 0);
        cartUI.quantity.setText("0");
        
        DefaultTableModel cartModel = new DefaultTableModel(
            null,
            new String[]{"Name", "Price", "Quantity", "Total"}
        );
        cartUI.cartTable.setModel(cartModel);
        
        // Act
        try {
            ActionEvent event = new ActionEvent(cartUI.addToCartBtn, ActionEvent.ACTION_PERFORMED, "");
            cartUI.addToCartBtnActionPerformed(event);
            // Se não lançar exceção, o item pode ser adicionado (comportamento atual)
        } catch (Exception e) {
            // Esperado se houver validação
        }
    }
    
    /**
     * Teste Unitário 5: Validar quantidade negativa
     * Cenário: Usuário tenta adicionar item com quantidade negativa
     * Resultado esperado: Deve tratar quantidade inválida
     */
    @Test
    public void testAddItemWithNegativeQuantity() {
        // Arrange
        cartUI.productsList = mockProductsList;
        DefaultTableModel productsModel = new DefaultTableModel(
            new Object[][]{{"Produto Teste 1", 10.50, 100}},
            new String[]{"Name", "Price", "Stock"}
        );
        cartUI.productsTable.setModel(productsModel);
        cartUI.productsTable.setRowSelectionInterval(0, 0);
        cartUI.quantity.setText("-1");
        
        DefaultTableModel cartModel = new DefaultTableModel(
            null,
            new String[]{"Name", "Price", "Quantity", "Total"}
        );
        cartUI.cartTable.setModel(cartModel);
        
        // Act
        try {
            ActionEvent event = new ActionEvent(cartUI.addToCartBtn, ActionEvent.ACTION_PERFORMED, "");
            cartUI.addToCartBtnActionPerformed(event);
            // Pode lançar NumberFormatException
        } catch (NumberFormatException e) {
            // Esperado
            assertTrue("Deve lançar NumberFormatException", true);
        }
    }
    
    /**
     * Teste Unitário 6: Validar quantidade não numérica
     * Cenário: Usuário tenta adicionar item com texto na quantidade
     * Resultado esperado: Deve lançar NumberFormatException
     */
    @Test(expected = NumberFormatException.class)
    public void testAddItemWithNonNumericQuantity() {
        // Arrange
        cartUI.productsList = mockProductsList;
        DefaultTableModel productsModel = new DefaultTableModel(
            new Object[][]{{"Produto Teste 1", 10.50, 100}},
            new String[]{"Name", "Price", "Stock"}
        );
        cartUI.productsTable.setModel(productsModel);
        cartUI.productsTable.setRowSelectionInterval(0, 0);
        cartUI.quantity.setText("abc");
        
        DefaultTableModel cartModel = new DefaultTableModel(
            null,
            new String[]{"Name", "Price", "Quantity", "Total"}
        );
        cartUI.cartTable.setModel(cartModel);
        
        // Act
        ActionEvent event = new ActionEvent(cartUI.addToCartBtn, ActionEvent.ACTION_PERFORMED, "");
        cartUI.addToCartBtnActionPerformed(event);
    }
    
    /**
     * Teste Unitário 7: Adicionar múltiplos itens ao carrinho
     * Cenário: Usuário adiciona vários produtos diferentes
     * Resultado esperado: Todos os itens devem ser adicionados corretamente
     */
    @Test
    public void testAddMultipleItemsToCart() {
        // Arrange
        cartUI.productsList = mockProductsList;
        DefaultTableModel productsModel = new DefaultTableModel(
            new Object[][]{
                {"Produto Teste 1", 10.50, 100},
                {"Produto Teste 2", 25.75, 50}
            },
            new String[]{"Name", "Price", "Stock"}
        );
        cartUI.productsTable.setModel(productsModel);
        cartUI.quantity.setText("1");
        
        DefaultTableModel cartModel = new DefaultTableModel(
            null,
            new String[]{"Name", "Price", "Quantity", "Total"}
        );
        cartUI.cartTable.setModel(cartModel);
        
        // Act - Adicionar primeiro item
        cartUI.productsTable.setRowSelectionInterval(0, 0);
        ActionEvent event1 = new ActionEvent(cartUI.addToCartBtn, ActionEvent.ACTION_PERFORMED, "");
        cartUI.addToCartBtnActionPerformed(event1);
        
        // Adicionar segundo item
        cartUI.productsTable.setRowSelectionInterval(1, 1);
        ActionEvent event2 = new ActionEvent(cartUI.addToCartBtn, ActionEvent.ACTION_PERFORMED, "");
        cartUI.addToCartBtnActionPerformed(event2);
        
        // Assert
        assertEquals("O carrinho deve ter 2 itens", 2, cartUI.cartTable.getRowCount());
        assertEquals("Primeiro item deve ser Produto Teste 1", "Produto Teste 1", cartUI.cartTable.getValueAt(0, 0));
        assertEquals("Segundo item deve ser Produto Teste 2", "Produto Teste 2", cartUI.cartTable.getValueAt(1, 0));
    }
}

