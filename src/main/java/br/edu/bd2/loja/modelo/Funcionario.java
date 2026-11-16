package br.edu.bd2.loja.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "funcionario")
public class Funcionario extends Pessoa {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Cargo cargo;

    @Column(precision = 10, scale = 2)
    private BigDecimal salario;

    @Column(name = "data_admissao")
    private LocalDate dataAdmissao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loja_id")
    private Loja loja;

    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }
    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }
    public LocalDate getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(LocalDate dataAdmissao) { this.dataAdmissao = dataAdmissao; }
    public Loja getLoja() { return loja; }
    public void setLoja(Loja loja) { this.loja = loja; }
}
