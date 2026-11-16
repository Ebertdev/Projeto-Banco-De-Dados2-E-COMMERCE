package br.edu.bd2.loja.carga;

import br.edu.bd2.loja.dao.JPAUtil;
import br.edu.bd2.loja.modelo.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class CargaDados {

    static final LocalDate INICIO = LocalDate.of(2024, 11, 1);
    static final LocalDate FIM = LocalDate.of(2026, 10, 31);
    static final int QTD_CLIENTES = 300;
    static final int QTD_PEDIDOS = 2600;

    private static final String[][] CATALOGO = {
        {"Informática", "Notebooks",
            "Dell Inspiron 15 i5 8GB 256GB;3499.00", "Lenovo IdeaPad 3 Ryzen 5 8GB 512GB;3299.00", "Acer Aspire 5 i5 16GB 512GB;3899.00",
            "Samsung Galaxy Book2 i5 8GB 256GB;3599.00", "Asus Vivobook 15 i7 16GB 512GB;4799.00", "Apple MacBook Air M2 8GB 256GB;8999.00",
            "HP 246 G8 i3 4GB 256GB;2599.00", "Dell G15 Gamer RTX 3050;6499.00", "Lenovo LOQ 15 RTX 4050;7299.00"},
        {"Informática", "Monitores",
            "LG UltraGear 24 144Hz;1199.00", "Samsung Odyssey G3 27 165Hz;1599.00", "Dell P2422H 24 Full HD;1199.00",
            "AOC Hero 24G2 144Hz;999.00", "Philips 241V8 24 Full HD;599.00", "LG UltraWide 29 WFHD;1099.00",
            "Samsung Smart Monitor M7 32 4K;2399.00", "Acer Nitro 27 QHD 170Hz;1799.00", "Asus TUF Gaming 27 165Hz;1999.00"},
        {"Informática", "Periféricos",
            "Logitech MX Master 3S Mouse;649.00", "Logitech G203 Mouse Gamer;119.00", "Redragon Kumara Teclado Mecânico;249.00",
            "Logitech K380 Teclado Bluetooth;229.00", "HyperX Alloy Origins Teclado;599.00", "Razer DeathAdder Essential Mouse;199.00",
            "Logitech C920 Webcam Full HD;429.00", "Microsoft Teclado e Mouse sem fio;179.00", "Multilaser Mousepad Gamer Extra Grande;49.90"},
        {"Informática", "Armazenamento",
            "Kingston NV2 SSD NVMe 500GB;249.00", "Kingston NV2 SSD NVMe 1TB;389.00", "Samsung 980 SSD NVMe 1TB;549.00",
            "WD Blue SSD SATA 480GB;229.00", "Seagate Barracuda HD 2TB;429.00", "SanDisk Ultra Pen Drive 128GB;59.90",
            "SanDisk Extreme SSD Portátil 1TB;699.00", "Samsung EVO Plus Cartão microSD 128GB;69.90", "WD Elements HD Externo 2TB;499.00"},
        {"Celulares e Telefonia", "Smartphones",
            "Samsung Galaxy S24 256GB;4299.00", "Samsung Galaxy A55 5G 256GB;2299.00", "Samsung Galaxy A15 128GB;999.00",
            "Motorola Moto G54 5G 256GB;1199.00", "Motorola Edge 50 Fusion 256GB;2199.00", "Xiaomi Redmi Note 13 256GB;1499.00",
            "Xiaomi Poco X6 Pro 512GB;2399.00", "Apple iPhone 15 128GB;4999.00", "Apple iPhone 13 128GB;3599.00"},
        {"Celulares e Telefonia", "Acessórios para celular",
            "Anker Carregador Turbo 20W;89.90", "Apple Cabo USB-C Lightning 1m;149.00", "Baseus Power Bank 20000mAh;189.00",
            "Samsung Carregador Super Rápido 25W;129.00", "Spigen Capa Galaxy S24;99.90", "Gshield Película de Vidro iPhone 15;39.90",
            "Xiaomi Carregador GaN 67W;159.00", "Multilaser Suporte Veicular Magnético;34.90", "Anker Cabo USB-C 2m;49.90"},
        {"Celulares e Telefonia", "Smartwatches",
            "Apple Watch SE 40mm;2299.00", "Apple Watch Series 9 45mm;4299.00", "Samsung Galaxy Watch6 44mm;1899.00",
            "Samsung Galaxy Fit3;399.00", "Xiaomi Smart Band 8;249.00", "Xiaomi Redmi Watch 4;499.00",
            "Amazfit Bip 5;449.00", "Garmin Forerunner 55;1699.00", "Huawei Watch GT 4 46mm;1999.00"},
        {"Áudio e Vídeo", "Fones de ouvido",
            "JBL Tune 510BT;199.00", "JBL Tune 760NC;449.00", "Sony WH-CH520;299.00",
            "Sony WH-1000XM5;1999.00", "Apple AirPods Pro 2ª geração;2099.00", "Apple AirPods 3ª geração;1499.00",
            "Samsung Galaxy Buds2 Pro;899.00", "Xiaomi Redmi Buds 5;179.00", "HyperX Cloud II Headset Gamer;549.00"},
        {"Áudio e Vídeo", "Smart TVs",
            "Samsung Smart TV 50 Crystal 4K;2399.00", "Samsung Smart TV 43 Crystal 4K;1799.00", "LG Smart TV 55 UHD 4K;2799.00",
            "LG Smart TV 43 Full HD;1699.00", "TCL Smart TV 50 4K Google TV;1999.00", "TCL Smart TV 65 QLED 4K;3999.00",
            "Philips Smart TV 50 4K;2199.00", "Sony Bravia 55 4K Google TV;3999.00", "AOC Roku Smart TV 32 HD;1099.00"},
        {"Áudio e Vídeo", "Caixas de som",
            "JBL Charge 5;1199.00", "JBL Flip 6;799.00", "JBL Go 3;249.00",
            "JBL Partybox 110;2999.00", "Sony SRS-XB13;329.00", "Bose SoundLink Flex;1299.00",
            "Amazon Echo Dot 5ª geração;379.00", "Xiaomi Mi Portable Bluetooth;179.00", "Samsung Soundbar HW-B450;1199.00"},
        {"Games e Casa Inteligente", "Consoles e Games",
            "Sony PlayStation 5 Slim Digital;3799.00", "Sony PlayStation 5 Slim com Leitor;4299.00", "Sony Controle DualSense;449.00",
            "Microsoft Xbox Series S 512GB;2699.00", "Microsoft Xbox Series X 1TB;4399.00", "Microsoft Controle Xbox Wireless;429.00",
            "Nintendo Switch OLED;2399.00", "Nintendo Zelda Tears of the Kingdom;349.00", "EA Sports FC 25 PS5;299.00"},
        {"Games e Casa Inteligente", "Casa inteligente",
            "Amazon Echo Show 8;799.00", "Amazon Fire TV Stick 4K;379.00", "Xiaomi Câmera de Segurança 360;249.00",
            "Positivo Lâmpada Smart Wi-Fi RGB;49.90", "Positivo Smart Tomada Wi-Fi;69.90", "TP-Link Roteador Wi-Fi 6 AX1500;329.00",
            "TP-Link Tapo Câmera C200;199.00", "Intelbras Roteador Wi-Fi Mesh Twibi;499.00", "Google Chromecast com Google TV;429.00"},
    };

    private static final Map<String, String> AFINIDADE = new HashMap<>();
    static {
        AFINIDADE.put("Smartphones", "Acessórios para celular");
        AFINIDADE.put("Notebooks", "Periféricos");
        AFINIDADE.put("Smart TVs", "Caixas de som");
        AFINIDADE.put("Consoles e Games", "Fones de ouvido");
        AFINIDADE.put("Monitores", "Periféricos");
        AFINIDADE.put("Smartwatches", "Acessórios para celular");
    }

    private static final String[] NOMES = {"Ana", "Bruno", "Carla", "Diego", "Eduarda", "Felipe", "Gabriela", "Henrique", "Isabela",
        "João", "Karina", "Lucas", "Mariana", "Nicolas", "Olivia", "Pedro", "Quésia", "Rafael", "Sofia", "Thiago", "Ursula",
        "Vinícius", "Wesley", "Ximena", "Yasmin", "Zeca", "Beatriz", "Caio", "Daniela", "Eduardo"};
    private static final String[] SOBRENOMES = {"Silva", "Santos", "Oliveira", "Souza", "Rodrigues", "Ferreira", "Alves", "Pereira",
        "Lima", "Gomes", "Costa", "Ribeiro", "Martins", "Carvalho", "Almeida", "Lopes", "Soares", "Fernandes", "Vieira", "Barbosa",
        "Rocha", "Dias", "Nascimento", "Andrade", "Moreira", "Nunes", "Marques", "Machado", "Mendes", "Freitas"};
    private static final String[] RUAS = {"Rua das Flores", "Av. Brasil", "Rua Sete de Setembro", "Av. Paulista", "Rua XV de Novembro",
        "Rua da Paz", "Av. Getúlio Vargas", "Rua Bahia", "Rua São José", "Av. Independência"};
    private static final String[] CIDADES = {"São Paulo|SP", "Campinas|SP", "Santos|SP", "Rio de Janeiro|RJ", "Niterói|RJ",
        "Belo Horizonte|MG", "Uberlândia|MG", "Curitiba|PR", "Londrina|PR", "Porto Alegre|RS", "Florianópolis|SC",
        "Salvador|BA", "Recife|PE", "Fortaleza|CE", "Brasília|DF", "Goiânia|GO", "Manaus|AM", "Belém|PA", "Vitória|ES", "Natal|RN"};

    private static final String[][] LOJAS = {
        {"CD São Paulo", "São Paulo", "SP"}, {"CD Rio de Janeiro", "Rio de Janeiro", "RJ"}, {"CD Belo Horizonte", "Belo Horizonte", "MG"},
        {"CD Curitiba", "Curitiba", "PR"}, {"CD Recife", "Recife", "PE"}};
    private static final Map<String, Integer> LOJA_POR_UF = new HashMap<>();
    static {
        for (String uf : new String[]{"SP", "ES", "GO", "DF"}) LOJA_POR_UF.put(uf, 0);
        LOJA_POR_UF.put("RJ", 1);
        LOJA_POR_UF.put("MG", 2);
        for (String uf : new String[]{"PR", "SC", "RS"}) LOJA_POR_UF.put(uf, 3);
        for (String uf : new String[]{"PE", "BA", "CE", "RN", "PA", "AM"}) LOJA_POR_UF.put(uf, 4);
    }

    private static final class ProdutoRef {
        final Long id; final BigDecimal preco; final String categoria;
        ProdutoRef(Long id, BigDecimal preco, String categoria) { this.id = id; this.preco = preco; this.categoria = categoria; }
    }

    private static final class ClienteRef {
        final Long id; final LocalDate cadastro; final int lojaIdx;
        ClienteRef(Long id, LocalDate cadastro, int lojaIdx) { this.id = id; this.cadastro = cadastro; this.lojaIdx = lojaIdx; }
    }

    private final Random rnd = new Random(2026);
    private final Set<String> emails = new HashSet<>();
    private final Set<String> cpfs = new HashSet<>();

    private final List<Long> lojaIds = new ArrayList<>();
    private final List<ProdutoRef> produtos = new ArrayList<>();
    private final Map<String, List<ProdutoRef>> produtosPorCategoria = new HashMap<>();
    private final List<ClienteRef> clientes = new ArrayList<>();

    public boolean jaCarregado() {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(c) FROM Cliente c", Long.class).getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public void executar() {
        long t0 = System.currentTimeMillis();
        carregarCadastros();
        carregarPedidos();
        System.out.printf("Carga concluída em %.1fs: %d lojas, %d produtos, %d clientes, %d pedidos.%n",
                (System.currentTimeMillis() - t0) / 1000.0, lojaIds.size(), produtos.size(), clientes.size(), QTD_PEDIDOS);
    }

    private void carregarCadastros() {
        EntityManager em = JPAUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Cargo[] cargos = {Cargo.GERENTE, Cargo.VENDEDOR, Cargo.ESTOQUISTA};
            for (String[] l : LOJAS) {
                Loja loja = new Loja();
                loja.setNome(l[0]);
                loja.setEndereco(novoEndereco(l[1], l[2]));
                for (Cargo cargo : cargos) {
                    Funcionario f = new Funcionario();
                    preencherPessoa(f, l[1], l[2]);
                    f.setDataCadastro(LocalDate.of(2024, 1, 15));
                    f.setCargo(cargo);
                    f.setSalario(BigDecimal.valueOf(cargo == Cargo.GERENTE ? 9000 : 3200 + rnd.nextInt(1500)).setScale(2));
                    f.setDataAdmissao(LocalDate.of(2023 + rnd.nextInt(2), 1 + rnd.nextInt(12), 1 + rnd.nextInt(28)));
                    loja.adicionarFuncionario(f);
                }
                em.persist(loja);
                lojaIds.add(loja.getId());
            }

            List<Fornecedor> fornecedores = new ArrayList<>();
            String[] razoes = {"Distribuidora Tech Brasil", "Importadora Digital Sul", "Atacado Eletrônicos SP", "Global Gadgets Ltda",
                "Mega Informática Distribuição", "Conecta Telecom Comércio", "Nova Era Games e Acessórios", "Casa Smart Fornecimentos"};
            for (int i = 0; i < razoes.length; i++) {
                Fornecedor f = new Fornecedor(razoes[i], String.format("%02d.%03d.%03d/0001-%02d", 10 + i, 100 + i * 7, 200 + i * 13, 10 + i));
                em.persist(f);
                fornecedores.add(f);
            }

            Map<String, Departamento> deptos = new LinkedHashMap<>();
            for (String[] linha : CATALOGO) {
                Departamento d = deptos.get(linha[0]);
                if (d == null) {
                    d = new Departamento(linha[0]);
                    deptos.put(linha[0], d);
                }
                Categoria cat = new Categoria(linha[1]);
                d.adicionarCategoria(cat);
            }
            for (Departamento d : deptos.values()) {
                em.persist(d);
            }
            for (String[] linha : CATALOGO) {
                Categoria cat = null;
                for (Categoria c : deptos.get(linha[0]).getCategorias()) {
                    if (c.getNome().equals(linha[1])) cat = c;
                }
                for (int i = 2; i < linha.length; i++) {
                    String[] partes = linha[i].split(";");
                    Produto p = new Produto();
                    p.setNome(partes[0]);
                    p.setMarca(partes[0].split(" ")[0]);
                    p.setDescricao(partes[0] + " - " + linha[1] + ". Garantia de 12 meses.");
                    p.setPreco(new BigDecimal(partes[1]));
                    p.setEstoque(50 + rnd.nextInt(450));
                    p.setDataCadastro(INICIO.minusDays(30 + rnd.nextInt(300)));
                    p.setCategoria(cat);
                    p.getFornecedores().add(fornecedores.get(rnd.nextInt(fornecedores.size())));
                    if (rnd.nextInt(100) < 40) {
                        p.getFornecedores().add(fornecedores.get(rnd.nextInt(fornecedores.size())));
                    }
                    em.persist(p);
                    ProdutoRef ref = new ProdutoRef(p.getId(), p.getPreco(), linha[1]);
                    produtos.add(ref);
                    List<ProdutoRef> lista = produtosPorCategoria.get(linha[1]);
                    if (lista == null) {
                        lista = new ArrayList<>();
                        produtosPorCategoria.put(linha[1], lista);
                    }
                    lista.add(ref);
                }
            }

            LocalDate cadInicio = LocalDate.of(2024, 6, 1);
            long dias = ChronoUnit.DAYS.between(cadInicio, LocalDate.of(2026, 8, 31));
            for (int i = 0; i < QTD_CLIENTES; i++) {
                String[] cidade = CIDADES[rnd.nextInt(CIDADES.length)].split("\\|");
                Cliente c = new Cliente();
                preencherPessoa(c, cidade[0], cidade[1]);
                c.setDataCadastro(cadInicio.plusDays(rnd.nextInt((int) dias)));
                c.setDataNascimento(LocalDate.of(1960 + rnd.nextInt(45), 1 + rnd.nextInt(12), 1 + rnd.nextInt(28)));
                em.persist(c);
                Integer lojaIdx = LOJA_POR_UF.get(cidade[1]);
                clientes.add(new ClienteRef(c.getId(), c.getDataCadastro(), lojaIdx == null ? 0 : lojaIdx));
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private void carregarPedidos() {
        int compradores = (int) (clientes.size() * 0.92);
        EntityManager em = JPAUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (int n = 1; n <= QTD_PEDIDOS; n++) {
                ClienteRef cr = clientes.get((int) (Math.pow(rnd.nextDouble(), 1.6) * compradores));
                LocalDateTime quando = sortearDataHora(cr.cadastro);

                Pedido pedido = new Pedido();
                pedido.setCliente(em.getReference(Cliente.class, cr.id));
                pedido.setLoja(em.getReference(Loja.class, lojaIds.get(rnd.nextDouble() < 0.85 ? cr.lojaIdx : rnd.nextInt(lojaIds.size()))));
                pedido.setDataHora(quando);

                FormaPagamento forma = sortearForma();
                for (ProdutoRef pr : sortearProdutos()) {
                    ItemPedido item = new ItemPedido();
                    item.setProduto(em.getReference(Produto.class, pr.id));
                    item.setQuantidade(sortearQuantidade(pr));
                    item.setPrecoUnitario(pr.preco);
                    item.setDesconto(calcularDesconto(pr.preco, item.getQuantidade(), forma, quando));
                    pedido.adicionarItem(item);
                }
                BigDecimal itens = pedido.getValorItens();
                pedido.setValorFrete(itens.compareTo(new BigDecimal("500")) > 0 ? BigDecimal.ZERO
                        : BigDecimal.valueOf(15 + rnd.nextInt(46)).setScale(2));

                StatusPedido status = sortearStatus(quando.toLocalDate());
                pedido.setStatus(status);
                pedido.setPagamento(criarPagamento(forma, status, pedido));
                em.persist(pedido);

                if (n % 50 == 0) {
                    tx.commit();
                    em.clear();
                    tx.begin();
                }
                if (n % 500 == 0) {
                    System.out.println("  ... " + n + " pedidos gravados");
                }
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private LocalDateTime sortearDataHora(LocalDate cadastro) {
        LocalDate inicio = cadastro.isAfter(INICIO) ? cadastro : INICIO;
        long dias = ChronoUnit.DAYS.between(inicio, FIM);
        LocalDate data;
        do {
            data = inicio.plusDays(rnd.nextInt((int) dias + 1));
        } while (rnd.nextDouble() > pesoMes(data.getMonthValue()) / 1.8);
        return LocalDateTime.of(data, LocalTime.of(rnd.nextInt(24), rnd.nextInt(60), rnd.nextInt(60)));
    }

    private double pesoMes(int mes) {
        switch (mes) {
            case 11: return 1.8;
            case 12: return 1.5;
            case 1: case 2: return 0.8;
            default: return 1.0;
        }
    }

    private FormaPagamento sortearForma() {
        int r = rnd.nextInt(100);
        if (r < 45) return FormaPagamento.CARTAO_CREDITO;
        if (r < 75) return FormaPagamento.PIX;
        if (r < 90) return FormaPagamento.BOLETO;
        return FormaPagamento.CARTAO_DEBITO;
    }

    private List<ProdutoRef> sortearProdutos() {
        int r = rnd.nextInt(100);
        int qtd = r < 45 ? 1 : r < 75 ? 2 : r < 90 ? 3 : 4;
        Map<Long, ProdutoRef> escolhidos = new LinkedHashMap<>();
        ProdutoRef primeiro = produtos.get((int) (Math.pow(rnd.nextDouble(), 1.8) * produtos.size()));
        escolhidos.put(primeiro.id, primeiro);
        String afim = AFINIDADE.get(primeiro.categoria);
        if (afim != null && qtd > 1 && rnd.nextInt(100) < 55) {
            List<ProdutoRef> lista = produtosPorCategoria.get(afim);
            ProdutoRef par = lista.get(rnd.nextInt(lista.size()));
            escolhidos.put(par.id, par);
        }
        while (escolhidos.size() < qtd) {
            ProdutoRef p = produtos.get((int) (Math.pow(rnd.nextDouble(), 1.8) * produtos.size()));
            escolhidos.put(p.id, p);
        }
        return new ArrayList<>(escolhidos.values());
    }

    private int sortearQuantidade(ProdutoRef p) {
        if (p.preco.compareTo(new BigDecimal("1500")) > 0) return 1;
        int r = rnd.nextInt(100);
        return r < 80 ? 1 : r < 95 ? 2 : 3;
    }

    private BigDecimal calcularDesconto(BigDecimal preco, int qtd, FormaPagamento forma, LocalDateTime quando) {
        double pct = 0;
        if (forma == FormaPagamento.PIX) pct = 0.05;
        if (quando.getMonthValue() == 11) pct = Math.max(pct, 0.10);
        return preco.multiply(BigDecimal.valueOf(qtd)).multiply(BigDecimal.valueOf(pct)).setScale(2, RoundingMode.HALF_UP);
    }

    private StatusPedido sortearStatus(LocalDate data) {
        long idade = ChronoUnit.DAYS.between(data, FIM);
        int r = rnd.nextInt(100);
        if (idade <= 3) return r < 30 ? StatusPedido.AGUARDANDO_PAGAMENTO : r < 65 ? StatusPedido.PAGO : StatusPedido.ENVIADO;
        if (idade <= 10) return r < 50 ? StatusPedido.ENVIADO : r < 95 ? StatusPedido.ENTREGUE : StatusPedido.CANCELADO;
        return r < 93 ? StatusPedido.ENTREGUE : StatusPedido.CANCELADO;
    }

    private Pagamento criarPagamento(FormaPagamento forma, StatusPedido status, Pedido pedido) {
        Pagamento pg = new Pagamento();
        pg.setForma(forma);
        pg.setValor(pedido.getValorTotal());
        if (forma == FormaPagamento.CARTAO_CREDITO) {
            int[] opcoes = {1, 1, 2, 3, 6, 10, 12};
            pg.setParcelas(opcoes[rnd.nextInt(opcoes.length)]);
        }
        switch (status) {
            case AGUARDANDO_PAGAMENTO:
                pg.setStatus(StatusPagamento.PENDENTE);
                break;
            case CANCELADO:
                pg.setStatus(rnd.nextBoolean() ? StatusPagamento.ESTORNADO : StatusPagamento.RECUSADO);
                break;
            default:
                pg.setStatus(StatusPagamento.APROVADO);
                pg.setDataPagamento(pedido.getDataHora().plusMinutes(1 + rnd.nextInt(180)));
        }
        return pg;
    }

    private void preencherPessoa(Pessoa p, String cidade, String uf) {
        String nome = NOMES[rnd.nextInt(NOMES.length)];
        String sobrenome = SOBRENOMES[rnd.nextInt(SOBRENOMES.length)];
        p.setNome(nome + " " + sobrenome + " " + SOBRENOMES[rnd.nextInt(SOBRENOMES.length)]);
        p.setEmail(emailUnico(nome, sobrenome));
        p.setCpf(cpfUnico());
        p.setEndereco(novoEndereco(cidade, uf));
    }

    private Endereco novoEndereco(String cidade, String uf) {
        return new Endereco(RUAS[rnd.nextInt(RUAS.length)], String.valueOf(1 + rnd.nextInt(2000)), "Centro", cidade, uf,
                String.format("%05d-%03d", 1000 + rnd.nextInt(98000), rnd.nextInt(1000)));
    }

    private String emailUnico(String nome, String sobrenome) {
        String base = semAcento(nome + "." + sobrenome).toLowerCase();
        String email;
        int n = 0;
        do {
            email = base + (n == 0 ? "" : String.valueOf(n)) + "@email.com";
            n++;
        } while (!emails.add(email));
        return email;
    }

    private String semAcento(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    private String cpfUnico() {
        while (true) {
            int[] d = new int[11];
            for (int i = 0; i < 9; i++) d[i] = rnd.nextInt(10);
            d[9] = digitoCpf(d, 9);
            d[10] = digitoCpf(d, 10);
            String cpf = String.format("%d%d%d.%d%d%d.%d%d%d-%d%d", d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7], d[8], d[9], d[10]);
            if (cpfs.add(cpf)) return cpf;
        }
    }

    private int digitoCpf(int[] d, int n) {
        int soma = 0;
        for (int i = 0; i < n; i++) soma += d[i] * (n + 1 - i);
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }
}
