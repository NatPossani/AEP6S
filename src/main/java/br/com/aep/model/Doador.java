package br.com.aep.model;

public class Doador {

    private String codigo;
    private String nome;
    private String tipo;
    private Contato contato;

    public Doador() {
    }

    public Doador(String codigo, String nome, String tipo, Contato contato) {
        this.codigo = codigo;
        this.nome = nome;
        this.tipo = tipo;
        this.contato = contato;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Contato getContato() {
        return contato;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }
}
