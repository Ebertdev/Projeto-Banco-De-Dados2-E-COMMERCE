package br.edu.bd2.loja.console;

import br.edu.bd2.loja.carga.CargaDados;
import br.edu.bd2.loja.dao.*;
import br.edu.bd2.loja.modelo.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleApp {

    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Scanner in = new Scanner(System.in);
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ConsultaJpqlDAO jpql = new ConsultaJpqlDAO();
    private final ConsultaCriteriaDAO criteria = new ConsultaCriteriaDAO();

    public void executar() {
        while (true) {
            System.out.println("\n===== LOJA DE ELETRÔNICOS E-COMMERCE (OLTP) =====");
            System.out.println("1) Clientes (CRUD)");
            System.out.println("2) Produtos (CRUD)");
            System.out.println("3) Registrar novo pedido");
            System.out.println("4) Consultas JPQL");
            System.out.println("5) Consultas Criteria API");
            System.out.println("6) Estatísticas do banco");
            System.out.println("0) Sair");
            switch (lerTexto("Opção")) {
                case "1": protegido(this::menuClientes); break;
                case "2": protegido(this::menuProdutos); break;
                case "3": protegido(this::novoPedido); break;
                case "4": protegido(this::menuJpql); break;
                case "5": protegido(this::menuCriteria); break;
                case "6": protegido(this::estatisticas); break;
                case "0": return;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private void protegido(Runnable acao) {
        try {
            acao.run();
        } catch (Exception e) {
            Throwable causa = e;
            while (causa.getCause() != null) causa = causa.getCause();
            System.out.println("Erro: " + causa.getMessage());
        }
    }

    private void menuClientes() {
        System.out.println("\n-- Clientes: 1) Listar  2) Buscar por id  3) Buscar por nome  4) Cadastrar  5) Atualizar  6) Remover");
        switch (lerTexto("Opção")) {
            case "1":
                int pagina = lerInt("Página", 1);
                imprimirClientes(clienteDAO.listarPaginado(pagina, 10));
                break;
            case "2":
                Cliente c = clienteDAO.buscarPorId(lerLong("Id"));
                if (c == null) System.out.println("Não encontrado."); else imprimirClientes(java.util.Collections.singletonList(c));
                break;
            case "3":
                imprimirClientes(clienteDAO.buscarPorNome(lerTexto("Trecho do nome"), 1, 10));
                break;
            case "4":
                Cliente novo = new Cliente();
                preencherCliente(novo);
                clienteDAO.salvar(novo);
                System.out.println("Cliente cadastrado com id " + novo.getId());
                break;
            case "5":
                Cliente existente = clienteDAO.buscarPorId(lerLong("Id"));
                if (existente == null) { System.out.println("Não encontrado."); break; }
                preencherCliente(existente);
                clienteDAO.atualizar(existente);
                System.out.println("Cliente atualizado.");
                break;
            case "6":
                System.out.println(clienteDAO.remover(lerLong("Id")) ? "Cliente removido." : "Não encontrado.");
                break;
            default: System.out.println("Opção inválida.");
        }
    }

    private void preencherCliente(Cliente c) {
        c.setNome(lerTexto("Nome"));
        c.setEmail(lerTexto("E-mail"));
        c.setCpf(lerTexto("CPF (000.000.000-00)"));
        c.setDataNascimento(LocalDate.parse(lerTexto("Nascimento (aaaa-mm-dd)")));
        Endereco e = new Endereco(lerTexto("Logradouro"), lerTexto("Número"), lerTexto("Bairro"),
                lerTexto("Cidade"), lerTexto("UF").toUpperCase(), lerTexto("CEP"));
        c.setEndereco(e);
    }

    private void imprimirClientes(List<Cliente> lista) {
        System.out.printf("%-6s %-34s %-34s %-16s %s%n", "ID", "NOME", "E-MAIL", "CPF", "CIDADE/UF");
        for (Cliente c : lista) {
            System.out.printf("%-6d %-34s %-34s %-16s %s/%s%n", c.getId(), c.getNome(), c.getEmail(), c.getCpf(),
                    c.getEndereco().getCidade(), c.getEndereco().getEstado());
        }
    }

    private void menuProdutos() {
        System.out.println("\n-- Produtos: 1) Listar  2) Buscar por id  3) Cadastrar  4) Atualizar preço/estoque  5) Remover");
        switch (lerTexto("Opção")) {
            case "1":
                imprimirProdutos(produtoDAO.listarComCategoria(lerInt("Página", 1), 10));
                break;
            case "2":
                Produto p = produtoDAO.buscarComCategoria(lerLong("Id"));
                if (p == null) System.out.println("Não encontrado."); else imprimirProdutos(java.util.Collections.singletonList(p));
                break;
            case "3":
                Produto novo = new Produto();
                novo.setNome(lerTexto("Nome"));
                novo.setMarca(lerTexto("Marca"));
                novo.setDescricao(lerTexto("Descrição"));
                novo.setPreco(lerDecimal("Preço"));
                novo.setEstoque(lerInt("Estoque", 0));
                Categoria cat = new GenericDAO<Categoria, Long>(Categoria.class).buscarPorId(lerLong("Id da categoria"));
                if (cat == null) { System.out.println("Categoria inexistente."); break; }
                novo.setCategoria(cat);
                produtoDAO.salvar(novo);
                System.out.println("Produto cadastrado com id " + novo.getId());
                break;
            case "4":
                Produto existente = produtoDAO.buscarPorId(lerLong("Id"));
                if (existente == null) { System.out.println("Não encontrado."); break; }
                existente.setPreco(lerDecimal("Novo preço"));
                existente.setEstoque(lerInt("Novo estoque", existente.getEstoque()));
                produtoDAO.atualizar(existente);
                System.out.println("Produto atualizado.");
                break;
            case "5":
                System.out.println(produtoDAO.remover(lerLong("Id")) ? "Produto removido." : "Não encontrado.");
                break;
            default: System.out.println("Opção inválida.");
        }
    }

    private void imprimirProdutos(List<Produto> lista) {
        System.out.printf("%-6s %-46s %-12s %-24s %s%n", "ID", "NOME", "PREÇO", "CATEGORIA", "ESTOQUE");
        for (Produto p : lista) {
            System.out.printf("%-6d %-46s R$ %-9s %-24s %d%n", p.getId(), p.getNome(), p.getPreco(),
                    p.getCategoria().getNome(), p.getEstoque());
        }
    }

    private void novoPedido() {
        Long clienteId = lerLong("Id do cliente");
        System.out.println("Lojas: " + new GenericDAO<Loja, Long>(Loja.class).listarTodos().stream()
                .map(l -> l.getId() + "=" + l.getNome()).collect(java.util.stream.Collectors.joining(", ")));
        Long lojaId = lerLong("Id da loja");
        Map<Long, Integer> itens = new LinkedHashMap<>();
        System.out.println("Informe os itens (id do produto e quantidade). Id em branco encerra.");
        while (true) {
            String id = lerTexto("Id do produto");
            if (id.isEmpty()) break;
            itens.put(Long.parseLong(id), lerInt("Quantidade", 1));
        }
        System.out.println("Formas: " + java.util.Arrays.toString(FormaPagamento.values()));
        FormaPagamento forma = FormaPagamento.valueOf(lerTexto("Forma de pagamento").toUpperCase());
        int parcelas = forma == FormaPagamento.CARTAO_CREDITO ? lerInt("Parcelas", 1) : 1;
        Pedido p = pedidoDAO.registrar(clienteId, lojaId, itens, forma, parcelas);
        System.out.printf("Pedido %d registrado em %s. Total: R$ %s%n", p.getId(), p.getDataHora().format(DATA_HORA), p.getValorTotal());
    }

    private void menuJpql() {
        System.out.println("\n-- JPQL: 1) Pedidos por estado  2) Pedido com itens (JOIN FETCH)  3) Total por cliente (HAVING)  "
                + "4) Produtos acima da média  5) Produtos por categoria (paginado)  6) Faturamento mensal  "
                + "7) Top produtos  8) Clientes sem pedidos  9) Faturamento por loja  10) Resumo por forma de pagamento");
        switch (lerTexto("Opção")) {
            case "1":
                for (Object[] r : jpql.pedidosPorEstado(lerTexto("UF"), 15)) {
                    System.out.printf("%-34s pedido %-6s %s %-15s R$ %s%n", r[0], r[1], ((java.time.LocalDateTime) r[2]).format(DATA_HORA), r[3], r[4]);
                }
                break;
            case "2":
                Pedido p = jpql.pedidoComItens(lerLong("Id do pedido"));
                if (p == null) { System.out.println("Não encontrado."); break; }
                System.out.printf("Pedido %d de %s em %s [%s]%n", p.getId(), p.getCliente().getNome(), p.getDataHora().format(DATA_HORA), p.getStatus());
                for (ItemPedido i : p.getItens()) {
                    System.out.printf("  %dx %-46s R$ %s (desc. %s)%n", i.getQuantidade(), i.getProduto().getNome(), i.getPrecoUnitario(), i.getDesconto());
                }
                System.out.println("  Itens: R$ " + p.getValorItens() + " | Frete: R$ " + p.getValorFrete() + " | Total: R$ " + p.getValorTotal());
                break;
            case "3":
                for (Object[] r : jpql.totalPorCliente(lerDecimal("Gasto mínimo"), 15)) {
                    System.out.printf("%-6s %-36s R$ %s%n", r[0], r[1], r[2]);
                }
                break;
            case "4":
                for (Produto pr : jpql.produtosAcimaDaMedia(15)) {
                    System.out.printf("%-46s %-24s R$ %s%n", pr.getNome(), pr.getCategoria().getNome(), pr.getPreco());
                }
                break;
            case "5":
                for (Produto pr : jpql.produtosPorCategoria(lerTexto("Categoria (ex.: Smartphones)"), lerInt("Página", 1), 5)) {
                    System.out.printf("%-46s R$ %s%n", pr.getNome(), pr.getPreco());
                }
                break;
            case "6":
                for (Object[] r : jpql.faturamentoMensal(lerInt("Ano", 2025))) {
                    System.out.printf("mês %-3s pedidos %-6s R$ %s%n", r[0], r[1], r[2]);
                }
                break;
            case "7":
                for (Object[] r : jpql.topProdutosVendidos(10)) {
                    System.out.printf("%-46s %s un.%n", r[0], r[1]);
                }
                break;
            case "8":
                List<Cliente> sem = jpql.clientesSemPedidos();
                System.out.println(sem.size() + " clientes nunca compraram:");
                imprimirClientes(sem.subList(0, Math.min(10, sem.size())));
                break;
            case "9":
                for (Object[] r : jpql.faturamentoPorLoja(10)) {
                    System.out.printf("%-22s %-18s pedidos %-6s R$ %s%n", r[0], r[1], r[2], r[3]);
                }
                break;
            case "10":
                for (Object[] r : jpql.resumoPorFormaPagamento()) {
                    System.out.printf("%-16s pedidos %-6s ticket médio R$ %.2f%n", r[0], r[1], r[2]);
                }
                break;
            default: System.out.println("Opção inválida.");
        }
    }

    private void menuCriteria() {
        System.out.println("\n-- Criteria: 1) Produtos por faixa de preço  2) Pedidos com filtros dinâmicos  3) Pedidos por status");
        switch (lerTexto("Opção")) {
            case "1":
                for (Produto p : criteria.produtosPorFaixaDePreco(lerDecimal("Preço mínimo"), lerDecimal("Preço máximo"), 15)) {
                    System.out.printf("%-46s %-24s R$ %s%n", p.getNome(), p.getCategoria().getNome(), p.getPreco());
                }
                break;
            case "2":
                System.out.println("(deixe em branco o que não quiser filtrar)");
                FiltroPedido f = new FiltroPedido();
                String nome = lerTexto("Nome do cliente contém");
                f.nomeCliente = nome.isEmpty() ? null : nome;
                String st = lerTexto("Status " + java.util.Arrays.toString(StatusPedido.values()));
                f.status = st.isEmpty() ? null : StatusPedido.valueOf(st.toUpperCase());
                String loja = lerTexto("Id da loja");
                f.lojaId = loja.isEmpty() ? null : Long.valueOf(loja);
                String ini = lerTexto("Data inicial (aaaa-mm-dd)");
                f.dataInicio = ini.isEmpty() ? null : LocalDate.parse(ini);
                String fim = lerTexto("Data final (aaaa-mm-dd)");
                f.dataFim = fim.isEmpty() ? null : LocalDate.parse(fim);
                f.tamanho = 15;
                for (Pedido p : criteria.pedidosFiltrados(f)) {
                    System.out.printf("pedido %-6d %s %-22s %-12s %s%n", p.getId(), p.getDataHora().format(DATA_HORA),
                            p.getStatus(), "loja " + p.getLoja().getId(), p.getCliente().getNome());
                }
                break;
            case "3":
                for (Object[] r : criteria.pedidosPorStatus()) {
                    System.out.printf("%-24s %s%n", r[0], r[1]);
                }
                break;
            default: System.out.println("Opção inválida.");
        }
    }

    private void estatisticas() {
        Object[][] entidades = {
            {"Departamentos", Departamento.class}, {"Categorias", Categoria.class}, {"Produtos", Produto.class},
            {"Fornecedores", Fornecedor.class}, {"Lojas", Loja.class}, {"Funcionários", Funcionario.class},
            {"Clientes", Cliente.class}, {"Pedidos", Pedido.class}, {"Itens de pedido", ItemPedido.class}, {"Pagamentos", Pagamento.class}};
        System.out.println("Registros no OLTP:");
        for (Object[] e : entidades) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            long n = new GenericDAO((Class) e[1]).contar();
            System.out.printf("  %-18s %d%n", e[0], n);
        }
        if (!new CargaDados().jaCarregado()) {
            System.out.println("\nBanco vazio. Rode a carga: java -jar loja-ecommerce.jar carga");
        }
    }

    private String lerTexto(String rotulo) {
        System.out.print(rotulo + ": ");
        return in.hasNextLine() ? in.nextLine().trim() : "0";
    }

    private int lerInt(String rotulo, int padrao) {
        String s = lerTexto(rotulo + " [" + padrao + "]");
        return s.isEmpty() ? padrao : Integer.parseInt(s);
    }

    private Long lerLong(String rotulo) {
        return Long.parseLong(lerTexto(rotulo));
    }

    private BigDecimal lerDecimal(String rotulo) {
        return new BigDecimal(lerTexto(rotulo).replace(',', '.'));
    }
}
