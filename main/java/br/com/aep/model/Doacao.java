package br.com.aep.model;

public class Doacao {

    private String codigo;
    private String nomeDoador;
    private String alimento;
    private int quantidade;
    private String unidade;
    private String validade;
    private String destino;

    public Doacao() {
    }

    public Doacao(String codigo, String nomeDoador, String alimento,
                  int quantidade, String unidade, String validade,
                  String destino) {
        this.codigo = codigo;
        this.nomeDoador = nomeDoador;
        this.alimento = alimento;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.validade = validade;
        this.destino = destino;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNomeDoador() {
        return nomeDoador;
    }

    public void setNomeDoador(String nomeDoador) {
        this.nomeDoador = nomeDoador;
    }

    public String getAlimento() {
        return alimento;
    }

    public void setAlimento(String alimento) {
        this.alimento = alimento;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getValidade() {
        return validade;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }
}
