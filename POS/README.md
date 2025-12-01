# POS – Guia de Testes do Carrinho

Este README documenta a suíte de testes criada para a funcionalidade de adicionar itens ao carrinho (`CartUI`). O conteúdo anterior foi substituído para detalhar casos de teste e resultados esperados.

## Estrutura de Testes

Os testes estão organizados por tipo, seguindo as melhores práticas de teste de software:

```
test/
├── unit/              # Testes Unitários
├── integration/        # Testes de Integração
├── system/             # Testes de Sistema
├── acceptance/         # Testes de Aceitação
├── blackbox/           # Testes de Caixa-Preta
├── ui/                 # Testes de Interface
├── performance/        # Testes de Desempenho
├── security/           # Testes de Segurança
└── util/               # Utilitários para testes
```

## Tipos de Testes Implementados

### 1. Testes Unitários (`unit/CartUITest.java`)

Testam métodos isolados da classe `CartUI`:

- ✅ Adicionar item válido ao carrinho
- ✅ Tentar adicionar item sem seleção
- ✅ Calcular total do carrinho
- ✅ Validar quantidade zero
- ✅ Validar quantidade negativa
- ✅ Validar quantidade não numérica
- ✅ Adicionar múltiplos itens

**Cobertura**: Métodos individuais da classe CartUI

### 2. Testes de Integração (`integration/CartIntegrationTest.java`)

Testam a interação entre componentes:

- ✅ Integração CartUI ↔ POSController para buscar produtos
- ✅ Fluxo completo: buscar → selecionar → adicionar
- ✅ Integração com busca de produtos por nome
- ✅ Integração entre adicionar item e cálculo de total
- ✅ Integração com validação de estoque

**Cobertura**: Interação entre múltiplos componentes do sistema

### 3. Testes de Sistema (`system/CartSystemTest.java`)

Testam o sistema completo end-to-end:

- ✅ Fluxo completo de adicionar item
- ✅ Múltiplas operações sequenciais
- ✅ Sistema com busca e adição
- ✅ Tratamento de erro do sistema

**Cobertura**: Fluxos completos do sistema

### 4. Testes de Aceitação (`acceptance/CartAcceptanceTest.java`)

Testam requisitos de negócio e casos de uso:

- ✅ Usuário deve poder adicionar produto ao carrinho
- ✅ Sistema deve calcular total automaticamente
- ✅ Usuário deve poder especificar quantidade
- ✅ Sistema deve prevenir adição sem seleção
- ✅ Sistema deve permitir múltiplos itens

**Cobertura**: Requisitos funcionais e critérios de aceitação

### 5. Testes de Caixa-Preta (`blackbox/CartBlackBoxTest.java`)

Testam baseados em cenários sem conhecimento da implementação:

- ✅ Cenário 1: Adicionar item válido
- ✅ Cenário 2: Tentar adicionar sem seleção
- ✅ Cenário 3: Adicionar com quantidade > 1
- ✅ Cenário 4: Adicionar múltiplos itens diferentes
- ✅ Cenário 5: Adicionar com quantidade zero
- ✅ Cenário 6: Adicionar com quantidade negativa
- ✅ Cenário 7: Adicionar com quantidade não numérica
- ✅ Cenário 8: Adicionar item com preço decimal

**Cobertura**: Cenários de uso baseados em requisitos

### 6. Testes de Interface (`ui/CartUITest.java`)

Testam componentes visuais e interação do usuário:

- ✅ Verificar visibilidade de componentes
- ✅ Verificar se botão está habilitado
- ✅ Verificar valor padrão do campo quantidade
- ✅ Verificar estrutura das tabelas
- ✅ Verificar atualização visual após adicionar
- ✅ Verificar limpeza de seleção
- ✅ Verificar se tabelas são editáveis
- ✅ Verificar formatação do total

**Cobertura**: Interface gráfica e experiência do usuário

### 7. Testes de Desempenho (`performance/CartPerformanceTest.java`)

Testam performance e tempo de resposta:

- ✅ Tempo de resposta para adicionar um item (< 1s)
- ✅ Tempo de resposta para adicionar múltiplos itens (< 5s)
- ✅ Tempo de carregamento inicial da tela
- ✅ Tempo de cálculo de total
- ✅ Tempo de busca de produtos
- ✅ Teste de estresse (muitos itens)

**Cobertura**: Performance e escalabilidade

### 8. Testes de Segurança (`security/CartSecurityTest.java`)

Testam vulnerabilidades e validações de segurança:

- ✅ Prevenção de SQL Injection na busca
- ✅ Validação de entrada no campo quantidade
- ✅ Prevenção de manipulação de índice
- ✅ Validação de valores numéricos extremos
- ✅ Prevenção de acesso não autorizado
- ✅ Validação de tipos de dados
- ✅ Prevenção de buffer overflow

**Cobertura**: Segurança e validação de entrada

## Requisitos Testados

### Requisito Principal: Adicionar Item ao Carrinho

**Descrição**: O sistema deve permitir que o usuário adicione produtos ao carrinho de compras.

**Critérios de Aceitação**:
1. ✅ Usuário pode selecionar um produto da lista
2. ✅ Usuário pode especificar a quantidade desejada
3. ✅ Sistema adiciona o item ao carrinho com informações corretas
4. ✅ Sistema calcula o total automaticamente
5. ✅ Sistema valida entrada do usuário
6. ✅ Sistema trata erros graciosamente

## Como Executar os Testes

### Pré-requisitos

1. **JUnit 4**: Os testes usam JUnit 4. Certifique-se de ter o JUnit no classpath:
   ```xml
   <dependency>
       <groupId>junit</groupId>
       <artifactId>junit</artifactId>
       <version>4.13.2</version>
       <scope>test</scope>
   </dependency>
   ```

2. **Banco de Dados**: O banco de dados MySQL deve estar configurado e acessível, pois alguns testes de integração e sistema requerem conexão real.

3. **Dados de Teste**: Recomenda-se ter produtos cadastrados no banco para testes completos.

### Executando Testes Individuais

```bash
# Testes unitários
javac -cp ".:../external-lib/*:junit-4.13.2.jar:hamcrest-core-1.3.jar" test/unit/CartUITest.java
java -cp ".:../external-lib/*:junit-4.13.2.jar:hamcrest-core-1.3.jar" org.junit.runner.JUnitCore unit.CartUITest
```

### Executando Todos os Testes

```bash
# No NetBeans ou IDE de sua escolha
# Clique com botão direito no diretório test/ e selecione "Run Tests"
```

### Executando por Tipo

- **Testes Unitários**: Execute `unit.CartUITest`
- **Testes de Integração**: Execute `integration.CartIntegrationTest`
- **Testes de Sistema**: Execute `system.CartSystemTest`
- **Testes de Aceitação**: Execute `acceptance.CartAcceptanceTest`
- **Testes de Caixa-Preta**: Execute `blackbox.CartBlackBoxTest`
- **Testes de Interface**: Execute `ui.CartUITest`
- **Testes de Desempenho**: Execute `performance.CartPerformanceTest`
- **Testes de Segurança**: Execute `security.CartSecurityTest`

## Estrutura de um Teste

Cada teste segue o padrão AAA (Arrange-Act-Assert):

```java
@Test
public void testExample() {
    // Arrange - Preparar dados e estado inicial
    Response response = POSFactory.getInstanceOfResponse();
    ArrayList<ProductDTO> products = controller.getProducts(response);
    
    // Act - Executar ação sendo testada
    ActionEvent event = new ActionEvent(...);
    cartUI.addToCartBtnActionPerformed(event);
    
    // Assert - Verificar resultado esperado
    assertEquals("Item deve ser adicionado", 1, cartUI.cartTable.getRowCount());
}
```

## Cobertura de Testes

### Métodos Testados

- `addToCartBtnActionPerformed()` - Adicionar item ao carrinho
- `calculateTotal()` - Calcular total do carrinho
- `populateProductsData()` - Carregar produtos
- `updateProductsTableData()` - Atualizar tabela de produtos
- `initializeCart()` - Inicializar carrinho

### Cenários Cobertos

- ✅ Caso feliz (happy path)
- ✅ Validação de entrada
- ✅ Tratamento de erros
- ✅ Casos extremos
- ✅ Múltiplos itens
- ✅ Performance
- ✅ Segurança

## Notas Importantes

1. **Acesso a Campos Privados**: Os testes usam reflexão Java para acessar campos privados da classe `CartUI`. Isso é necessário porque os campos são privados e não há métodos públicos de acesso.

2. **Dependência do Banco**: Alguns testes (integração, sistema) requerem conexão com o banco de dados. Certifique-se de que o banco está configurado corretamente.

3. **Dados de Teste**: Para testes completos, é recomendado ter dados de teste no banco. Os testes são escritos para funcionar mesmo sem dados, mas a cobertura será limitada.

4. **Performance**: Os testes de desempenho definem limites de tempo. Ajuste conforme necessário para seu ambiente.

## Melhorias Futuras

- [ ] Adicionar mocks para testes de integração sem banco
- [ ] Adicionar testes de regressão
- [ ] Implementar cobertura de código automatizada
- [ ] Adicionar testes de acessibilidade
- [ ] Implementar testes de usabilidade

## Autor

Testes implementados para o trabalho final da disciplina ENS4003 - Teste de Software.
