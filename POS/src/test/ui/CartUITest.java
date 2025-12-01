package test.ui;

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
 * Testes de Interface para adicionar item ao carrinho
 * Testa componentes visuais e interação do usuário
 */
public class CartUITest {
    
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
     * Teste de Interface 1: Verificar se componentes estão visíveis
     * Cenário: Tela de carrinho é carregada
     * Resultado esperado: Todos os componentes necessários devem estar visíveis
     */
    @Test
    public void testUIComponentsVisibility() {
        // Assert
        assertNotNull("Botão adicionar ao carrinho deve existir", cartUI.addToCartBtn);
        assertTrue("Botão adicionar ao carrinho deve estar visível", 
            cartUI.addToCartBtn.isVisible());
        
        assertNotNull("Campo quantidade deve existir", cartUI.quantity);
        assertTrue("Campo quantidade deve estar visível", 
            cartUI.quantity.isVisible());
        
        assertNotNull("Tabela de produtos deve existir", cartUI.productsTable);
        assertTrue("Tabela de produtos deve estar visível", 
            cartUI.productsTable.isVisible());
        
        assertNotNull("Tabela do carrinho deve existir", cartUI.cartTable);
        assertTrue("Tabela do carrinho deve estar visível", 
            cartUI.cartTable.isVisible());
        
        assertNotNull("Label de total deve existir", cartUI.totalofcart);
        assertTrue("Label de total deve estar visível", 
            cartUI.totalofcart.isVisible());
    }
    
    /**
     * Teste de Interface 2: Verificar se botão está habilitado
     * Cenário: Tela carregada
     * Resultado esperado: Botão deve estar habilitado
     */
    @Test
    public void testUIButtonEnabled() {
        // Assert
        assertTrue("Botão adicionar ao carrinho deve estar habilitado", 
            cartUI.addToCartBtn.isEnabled());
    }
    
    /**
     * Teste de Interface 3: Verificar estado inicial do campo quantidade
     * Cenário: Tela carregada
     * Resultado esperado: Campo quantidade deve ter valor padrão "1"
     */
    @Test
    public void testUIQuantityDefaultValue() {
        // Assert
        assertEquals("Campo quantidade deve ter valor padrão '1'", 
            "1", cartUI.quantity.getText());
    }
    
    /**
     * Teste de Interface 4: Verificar estrutura da tabela de produtos
     * Cenário: Tela carregada com produtos
     * Resultado esperado: Tabela deve ter colunas corretas
     */
    @Test
    public void testUIProductsTableStructure() {
        // Arrange
        Response response = POSFactory.getInstanceOfResponse();
        ArrayList<ProductDTO> products = controller.getProducts(response);
        
        if (products != null && !products.isEmpty()) {
            cartUI.productsList = products;
            cartUI.populateProductsData();
            
            // Assert
            DefaultTableModel model = (DefaultTableModel) cartUI.productsTable.getModel();
            assertEquals("Tabela de produtos deve ter 3 colunas", 
                3, model.getColumnCount());
            assertEquals("Primeira coluna deve ser 'Name'", 
                "Name", model.getColumnName(0));
            assertEquals("Segunda coluna deve ser 'Price'", 
                "Price", model.getColumnName(1));
            assertEquals("Terceira coluna deve ser 'Stock'", 
                "Stock", model.getColumnName(2));
        }
    }
    
    /**
     * Teste de Interface 5: Verificar estrutura da tabela do carrinho
     * Cenário: Tela carregada
     * Resultado esperado: Tabela do carrinho deve ter colunas corretas
     */
    @Test
    public void testUICartTableStructure() {
        // Arrange
        cartUI.initializeCart();
        
        // Assert
        DefaultTableModel model = (DefaultTableModel) cartUI.cartTable.getModel();
        assertEquals("Tabela do carrinho deve ter 4 colunas", 
            4, model.getColumnCount());
        assertEquals("Primeira coluna deve ser 'Name'", 
            "Name", model.getColumnName(0));
        assertEquals("Segunda coluna deve ser 'Price'", 
            "Price", model.getColumnName(1));
        assertEquals("Terceira coluna deve ser 'Quantity'", 
            "Quantity", model.getColumnName(2));
        assertEquals("Quarta coluna deve ser 'Total'", 
            "Total", model.getColumnName(3));
    }
    
    /**
     * Teste de Interface 6: Verificar atualização visual após adicionar item
     * Cenário: Item adicionado ao carrinho
     * Resultado esperado: Tabela do carrinho e total devem ser atualizados visualmente
     */
    @Test
    public void testUIVisualUpdateAfterAdd() {
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
            
            int initialRowCount = cartUI.cartTable.getRowCount();
            String initialTotal = cartUI.totalofcart.getText();
            
            // Act
            ActionEvent event = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event);
            
            // Assert
            assertTrue("Tabela do carrinho deve ter mais linhas", 
                cartUI.cartTable.getRowCount() > initialRowCount);
            assertNotEquals("Total deve ser atualizado", 
                initialTotal, cartUI.totalofcart.getText());
        }
    }
    
    /**
     * Teste de Interface 7: Verificar limpeza de seleção após adicionar
     * Cenário: Item adicionado ao carrinho
     * Resultado esperado: Seleção na tabela de produtos deve ser limpa
     */
    @Test
    public void testUIClearSelectionAfterAdd() {
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
            
            // Act
            ActionEvent event = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event);
            
            // Assert
            assertEquals("Seleção deve ser limpa após adicionar", 
                -1, cartUI.productsTable.getSelectedRow());
        }
    }
    
    /**
     * Teste de Interface 8: Verificar formatação do total
     * Cenário: Item adicionado com preço decimal
     * Resultado esperado: Total deve ser exibido corretamente formatado
     */
    @Test
    public void testUITotalFormatting() {
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
            
            // Act
            ActionEvent event = new ActionEvent(
                cartUI.addToCartBtn, 
                ActionEvent.ACTION_PERFORMED, 
                ""
            );
            cartUI.addToCartBtnActionPerformed(event);
            
            // Assert
            String totalText = cartUI.totalofcart.getText();
            assertNotNull("Total deve ter valor", totalText);
            assertFalse("Total não deve estar vazio", totalText.isEmpty());
            
            // Verificar se é um número válido
            try {
                Double.parseDouble(totalText);
                assertTrue("Total deve ser um número válido", true);
            } catch (NumberFormatException e) {
                fail("Total deve ser um número válido");
            }
        }
    }
}

