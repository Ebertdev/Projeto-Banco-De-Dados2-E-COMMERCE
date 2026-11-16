package br.edu.bd2.loja.modelo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Endereco implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(length = 150) private String logradouro;
    @Column(length = 10)  private String numero;
    @Column(length = 80)  private String bairro;
    @Column(length = 80)  private String cidade;
    @Column(length = 2)   private String estado;
    @Column(length = 9)   private String cep;

    public Endereco() { }

    public Endereco(String logradouro, String numero, String bairro, String cidade, String estado, String cep) {
        this.logradouro = logradouro; this.numero = numero; this.bairro = bairro;
        this.cidade = cidade; this.estado = estado; this.cep = cep;
    }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    @Override
    public String toString() {
        return logradouro + ", " + numero + " - " + bairro + ", " + cidade + "/" + estado + " (" + cep + ")";
    }
}
