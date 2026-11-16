package br.edu.bd2.loja.dao;

import br.edu.bd2.loja.modelo.StatusPedido;
import java.time.LocalDate;

public class FiltroPedido {

    public String nomeCliente;
    public StatusPedido status;
    public Long lojaId;
    public LocalDate dataInicio;
    public LocalDate dataFim;
    public int pagina = 1;
    public int tamanho = 20;
}
