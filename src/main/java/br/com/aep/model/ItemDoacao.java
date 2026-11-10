package br.com.aep.model;

public class ItemDoacao {

    private String alimento;
    private int quantidade;
    private String unidade;
    private String validade;

    public ItemDoacao() {
    }

    public ItemDoacao(String alimento, int quantidade, String unidade, String validade) {
        this.alimento = alimento;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.validade = validade;
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
}
